package com.islabuy.camiguin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.islabuy.camiguin.data.model.Conversation
import com.islabuy.camiguin.data.model.Message
import com.islabuy.camiguin.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private var conversationId: String? = null

    fun loadMessages(convId: String) {
        conversationId = convId
        viewModelScope.launch {
            try {
                val snap = repository.getConversation(convId)
                _conversation.value = snap
            } catch (_: Exception) { }
        }
        viewModelScope.launch {
            repository.getMessages(convId).addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val msgs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(messageId = doc.id)
                } ?: emptyList()
                _messages.value = msgs
            }
        }
    }

    fun sendMessage(text: String, senderId: String, receiverId: String) {
        val convId = conversationId ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            _isSending.value = true
            try {
                val message = Message(
                    conversationId = convId,
                    senderId = senderId,
                    receiverId = receiverId,
                    text = text.trim(),
                    createdAt = System.currentTimeMillis()
                )
                repository.sendMessage(message)
            } finally {
                _isSending.value = false
            }
        }
    }

    fun deleteForMe(messageId: String, userId: String) {
        viewModelScope.launch {
            repository.deleteForMe(messageId, userId)
        }
    }

    fun deleteForEveryone(messageId: String, senderId: String) {
        viewModelScope.launch {
            repository.deleteForEveryone(messageId, senderId)
        }
    }

    fun markAsSeen(userId: String) {
        val convId = conversationId ?: return
        viewModelScope.launch {
            repository.markAsSeen(convId, userId)
        }
    }
}
