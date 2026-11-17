package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.islabuy.camiguin.data.preferences.NotificationPreferences
import com.islabuy.camiguin.data.repository.UserRepository
import com.islabuy.camiguin.ui.components.ThreeDCard
import com.islabuy.camiguin.ui.components.ThreeDButton
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val userRepo = remember { UserRepository() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
        }

        // Notification settings
        ThreeDCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notification Settings", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                val context = androidx.compose.ui.platform.LocalContext.current
                val prefs = remember { NotificationPreferences(context) }
                val notifEnabled by prefs.notificationsEnabled.collectAsState(initial = true)
                val msgSounds by prefs.messageSoundsEnabled.collectAsState(initial = true)
                val prodUpdates by prefs.productUpdatesEnabled.collectAsState(initial = true)
                val announcements by prefs.announcementsEnabled.collectAsState(initial = true)

                @Composable
                fun toggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(label)
                        Switch(checked = checked, onCheckedChange = onChange)
                    }
                }

                toggleRow("Enable Notifications", notifEnabled) { scope.launch { prefs.setNotificationsEnabled(it) } }
                toggleRow("Message Sounds", msgSounds) { scope.launch { prefs.setMessageSoundsEnabled(it) } }
                toggleRow("Product Updates", prodUpdates) { scope.launch { prefs.setProductUpdatesEnabled(it) } }
                toggleRow("Announcements", announcements) { scope.launch { prefs.setAnnouncementsEnabled(it) } }
            }
        }

        // Account deletion
        ThreeDCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Account", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ThreeDButton(
                    text = "Delete Account",
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.error
                )
            }
        }

        if (statusMessage.isNotEmpty()) {
            Text(statusMessage)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This will permanently delete your account, listings, messages, and associated media. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        val result = userRepo.deleteAccount()
                        if (result.isSuccess) {
                            statusMessage = "Account deleted successfully"
                        } else {
                            statusMessage = "Failed: ${result.exceptionOrNull()?.message}"
                        }
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
