package com.islabuy.camiguin.util

import com.islabuy.camiguin.data.model.Conversation

/**
 * Single authority for "who belongs to a conversation".
 *
 * Membership used to be answered three different ways: the repository queried a
 * `participants` field that never existed, the conversation list filtered on
 * `buyerId` only (so sellers saw an empty inbox), and firestore.rules accepted
 * buyer *or* seller. Everything now derives from [isMember], and the rules file
 * carries the same predicate as `isConversationParticipant`.
 */
object ConversationAccess {

    /** True when [userId] is the conversation's buyer or its seller. */
    fun isMember(conversation: Conversation, userId: String): Boolean {
        if (userId.isBlank()) return false
        return userId == conversation.buyerId || userId == conversation.sellerId
    }

    /** The other participant, or null when [userId] is not a member. */
    fun counterpartOf(conversation: Conversation, userId: String): String? = when (userId) {
        conversation.buyerId -> conversation.sellerId
        conversation.sellerId -> conversation.buyerId
        else -> null
    }

    /**
     * ONE PRODUCT + ONE BUYER + ONE SELLER = ONE CONVERSATION.
     */
    fun conversationKey(productId: String, buyerId: String, sellerId: String): String =
        "$productId:$buyerId:$sellerId"

    fun isUniqueConversation(
        existingKeys: Set<String>,
        productId: String,
        buyerId: String,
        sellerId: String
    ): Boolean = conversationKey(productId, buyerId, sellerId) !in existingKeys
}
