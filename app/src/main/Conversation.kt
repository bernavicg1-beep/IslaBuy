package com.islabuy.camiguin.data.model

data class Conversation(
    val conversationId: String = "",
    val productId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val productTitle: String = "",
    val productImage: String? = null,
    val lastMessage: String = "",
    val lastMessageAt: Long = 0L,
    val unreadCounts: Map<String, Int> = emptyMap()
)

data class Message(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val createdAt: Long = 0L,
    val status: String = "sent", // sent, delivered, seen
    val deletedFor: List<String> = emptyList(),
    val deletedForEveryone: Boolean = false
)
