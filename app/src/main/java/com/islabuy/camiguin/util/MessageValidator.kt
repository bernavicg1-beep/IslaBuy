package com.islabuy.camiguin.util

object MessageValidator {
    fun isValidMessage(text: String): Boolean {
        return text.isNotBlank() && text.length <= 5000
    }

    fun canDeleteForEveryone(senderId: String, currentUserId: String): Boolean {
        return senderId == currentUserId
    }

    fun canDeleteForMe(userId: String, participants: List<String>): Boolean {
        return userId in participants
    }

    fun conversationKey(productId: String, buyerId: String, sellerId: String): String {
        return "$productId:$buyerId:$sellerId"
    }

    fun isUniqueConversation(
        existingKeys: Set<String>,
        productId: String,
        buyerId: String,
        sellerId: String
    ): Boolean {
        val key = conversationKey(productId, buyerId, sellerId)
        return key !in existingKeys
    }
}
