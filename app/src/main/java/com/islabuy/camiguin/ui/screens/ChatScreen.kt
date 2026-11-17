package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.islabuy.camiguin.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islabuy.camiguin.data.model.Message
import com.islabuy.camiguin.data.repository.UserRepository
import com.islabuy.camiguin.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(conversationId: String?) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val conversation by viewModel.conversation.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val userRepo = remember { UserRepository() }
    val currentUid = userRepo.currentUser()?.uid ?: ""
    var messageText by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(conversationId) {
        conversationId?.let {
            viewModel.loadMessages(it)
            if (currentUid.isNotBlank()) viewModel.markAsSeen(currentUid)
        }
    }

    val otherParticipantId = conversation?.let { c ->
        if (currentUid == c.buyerId) c.sellerId else c.buyerId
    } ?: ""

    Column(modifier = Modifier.fillMaxSize()) {
        // Product header
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            Text(
                text = "Product Chat",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Messages list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        ) {
            items(messages, key = { it.messageId }) { msg ->
                MessageBubble(
                    message = msg,
                    isOwn = msg.senderId == currentUid,
                    onLongPress = { deleteTarget = msg.messageId }
                )
            }
        }

        // Delete dialog
        deleteTarget?.let { msgId ->
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                title = { Text("Delete Message") },
                text = { Text("Choose deletion option:") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteForMe(msgId, currentUid)
                        deleteTarget = null
                    }) { Text(stringResource(R.string.delete_for_me)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.deleteForEveryone(msgId, currentUid)
                        deleteTarget = null
                    }) { Text(stringResource(R.string.delete_for_everyone)) }
                }
            )
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText, currentUid, otherParticipantId)
                        messageText = ""
                    }
                },
                enabled = !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message, isOwn: Boolean, onLongPress: () -> Unit) {
    val alignment = if (isOwn) Alignment.End else Alignment.Start
    val bgColor = if (isOwn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() })
            },
        horizontalAlignment = alignment
    ) {
        if (message.deletedForEveryone) {
            Text(
                text = "This message was deleted",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        } else if (message.messageId !in message.deletedFor) {
            Surface(
                color = bgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .shadow(4.dp, MaterialTheme.shapes.medium)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formatTimestamp(message.createdAt),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
