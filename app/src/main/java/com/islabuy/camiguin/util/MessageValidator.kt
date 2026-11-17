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

    // Conversation identity (key / uniqueness) lives in ConversationAccess.
}
