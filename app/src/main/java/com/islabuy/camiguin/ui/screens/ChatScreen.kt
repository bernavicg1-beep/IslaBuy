package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islabuy.camiguin.data.model.Message
import com.islabuy.camiguin.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(conversationId: String?) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<String?>(null) }
    var deleteForEveryone by remember { mutableStateOf(false) }

    LaunchedEffect(conversationId) {
        conversationId?.let { viewModel.loadMessages(it) }
    }

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
                    isOwn = msg.senderId == "current_user",
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
                        viewModel.deleteForMe(msgId, "current_user")
                        deleteTarget = null
                    }) { Text("Delete for Me") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.deleteForEveryone(msgId, "current_user")
                        deleteTarget = null
                    }) { Text("Delete for Everyone") }
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
                        viewModel.sendMessage(messageText, "current_user", "seller_id")
                        messageText = ""
                    }
                },
                enabled = !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send")
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
        modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.padding(vertical = 2.dp)
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
