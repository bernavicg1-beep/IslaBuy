package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.data.model.AppNotification
import com.islabuy.camiguin.data.repository.NotificationRepository
import com.islabuy.camiguin.ui.theme.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationHistoryScreen(
    userId: String,
    onBack: () -> Unit = {}
) {
    val repo = remember { NotificationRepository() }
    val scope = rememberCoroutineScope()
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }

    LaunchedEffect(userId) {
        scope.launch {
            try {
                val snap = repo.forUser(userId).get().await()
                notifications = snap.documents.mapNotNull { it.toObject(AppNotification::class.java)?.copy(notificationId = it.id) }
            } catch (_: Exception) { }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } },
                actions = {
                    TextButton(onClick = { scope.launch { repo.markAllRead(userId); notifications = notifications.map { it.copy(isRead = true) } } }) {
                        Text("Mark all read", color = NavyBlue)
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No notifications.", color = SecondaryText) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
                items(notifications, key = { it.notificationId }) { n ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(n.title, fontWeight = FontWeight.Bold, color = PrimaryText)
                                if (!n.isRead) Badge(containerColor = ErrorRed) { Text(" ") }
                            }
                            Text(n.message, color = SecondaryText, fontSize = 13.sp)
                            TextButton(onClick = { scope.launch { repo.markRead(n.notificationId); notifications = notifications.map { if (it.notificationId == n.notificationId) it.copy(isRead = true) else it } } }) {
                                Text("Mark read", color = NavyBlue, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}