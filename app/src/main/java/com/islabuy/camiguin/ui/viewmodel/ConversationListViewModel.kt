package com.islabuy.camiguin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.islabuy.camiguin.data.model.Conversation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadConversations(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            db.collection("conversations")
                .whereEqualTo("buyerId", userId)
                .orderBy("lastMessageAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        _isLoading.value = false
                        return@addSnapshotListener
                    }
                    val convs = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Conversation::class.java)?.copy(conversationId = doc.id)
                    } ?: emptyList()
                    _conversations.value = convs
                    _isLoading.value = false
                }
        }
    }

    fun getTotalUnread(userId: String): Int {
        return _conversations.value.sumOf { it.unreadCounts[userId] ?: 0 }
    }
}
