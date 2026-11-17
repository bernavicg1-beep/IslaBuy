package com.islabuy.camiguin.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.islabuy.camiguin.data.model.Conversation
import com.islabuy.camiguin.data.model.Message
import com.islabuy.camiguin.util.ConversationAccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val conversations = db.collection("conversations")
    private val messages = db.collection("messages")

    /**
     * Get or create a conversation for a specific product + buyer + seller.
     * Enforces: ONE PRODUCT + ONE BUYER + ONE SELLER = ONE CONVERSATION.
     */
    suspend fun getOrCreateConversation(
        productId: String,
        buyerId: String,
        sellerId: String,
        productTitle: String,
        productImage: String?
    ): Conversation {
        val existing = conversations
            .whereEqualTo("productId", productId)
            .whereEqualTo("buyerId", buyerId)
            .whereEqualTo("sellerId", sellerId)
            .limit(1)
            .get().await()

        if (!existing.isEmpty) {
            return existing.documents.first().toObject(Conversation::class.java)!!
                .copy(conversationId = existing.documents.first().id)
        }

        val newConv = Conversation(
            productId = productId,
            buyerId = buyerId,
            sellerId = sellerId,
            productTitle = productTitle,
            productImage = productImage,
            lastMessage = "",
            lastMessageAt = System.currentTimeMillis()
        )
        val doc = conversations.add(newConv).await()
        return newConv.copy(conversationId = doc.id)
    }

    /**
     * Every conversation [userId] takes part in, whether they are the buyer or the seller.
     *
     * Two equality listeners are merged here so callers never need to know which side of the
     * conversation the user sits on. Ordering is applied locally, so this needs no composite
     * index on `sellerId` + `lastMessageAt`.
     */
    fun conversationsFor(userId: String): Flow<List<Conversation>> = callbackFlow {
        var asBuyer: List<Conversation> = emptyList()
        var asSeller: List<Conversation> = emptyList()

        fun publish() {
            val merged = (asBuyer + asSeller)
                .distinctBy { it.conversationId }
                .filter { ConversationAccess.isMember(it, userId) }
                .sortedByDescending { it.lastMessageAt }
            trySend(merged)
        }

        val buyerListener = conversations
            .whereEqualTo("buyerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    asBuyer = snapshot.toConversations()
                    publish()
                }
            }

        val sellerListener = conversations
            .whereEqualTo("sellerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    asSeller = snapshot.toConversations()
                    publish()
                }
            }

        awaitClose {
            buyerListener.remove()
            sellerListener.remove()
        }
    }

    suspend fun getConversation(conversationId: String): Conversation? =
        conversations.document(conversationId).get().await()
            .toObject(Conversation::class.java)?.copy(conversationId = conversationId)

    fun getMessages(conversationId: String) = messages
        .whereEqualTo("conversationId", conversationId)
        .orderBy("createdAt", Query.Direction.ASCENDING)

    suspend fun sendMessage(message: Message): Result<String> = runCatching {
        val doc = messages.add(message).await()
        // Update conversation last message + bump receiver unread count
        conversations.document(message.conversationId).update(
            mapOf(
                "lastMessage" to message.text,
                "lastMessageAt" to message.createdAt,
                "unreadCounts.${message.receiverId}" to FieldValue.increment(1)
            )
        ).await()
        doc.id
    }

    suspend fun deleteForMe(messageId: String, userId: String): Result<Unit> = runCatching {
        val doc = messages.document(messageId).get().await()
        val deletedFor = (doc.get("deletedFor") as? List<String> ?: emptyList()) + userId
        messages.document(messageId).update("deletedFor", deletedFor).await()
    }

    suspend fun deleteForEveryone(messageId: String, senderId: String): Result<Unit> = runCatching {
        val doc = messages.document(messageId).get().await()
        if (doc.getString("senderId") != senderId) {
            throw SecurityException("Only the sender can delete for everyone")
        }
        messages.document(messageId).update("deletedForEveryone", true).await()
    }

    suspend fun markAsSeen(conversationId: String, userId: String) {
        // Update unread count for this user to 0
        conversations.document(conversationId).update("unreadCounts.$userId", 0).await()
    }
}

private fun QuerySnapshot.toConversations(): List<Conversation> =
    documents.mapNotNull { doc ->
        doc.toObject(Conversation::class.java)?.copy(conversationId = doc.id)
    }
