package com.islabuy.camiguin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islabuy.camiguin.data.model.Conversation
import com.islabuy.camiguin.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var observer: Job? = null

    /**
     * Observes every conversation [userId] belongs to, as buyer or seller.
     */
    fun loadConversations(userId: String) {
        observer?.cancel()
        observer = viewModelScope.launch {
            _isLoading.value = true
            repository.conversationsFor(userId)
                .onEach { conversations ->
                    _conversations.value = conversations
                    _isLoading.value = false
                }
                .catch { _isLoading.value = false }
                .collect { }
        }
    }

    fun getTotalUnread(userId: String): Int {
        return _conversations.value.sumOf { it.unreadCounts[userId] ?: 0 }
    }

    override fun onCleared() {
        observer?.cancel()
        super.onCleared()
    }
}
