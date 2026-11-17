package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.islabuy.camiguin.data.model.User
import com.islabuy.camiguin.data.preferences.NotificationPreferences
import com.islabuy.camiguin.data.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val userRepo = remember { UserRepository() }
    var user by remember { mutableStateOf<User?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = userRepo.currentUser()?.uid
        if (uid != null) {
            user = userRepo.getUserProfile(uid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineSmall)

        user?.let { u ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${u.name}", style = MaterialTheme.typography.bodyLarge)
                    Text("Email: ${u.email}", style = MaterialTheme.typography.bodyMedium)
                    Text("Phone: ${u.phone}", style = MaterialTheme.typography.bodyMedium)
                    Text("Role: ${u.role}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Admin Panel access (only for admins)
            if (u.isAdmin()) {
                Button(
                    onClick = { navController.navigate("admin") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Admin Panel")
                }
            }
        }

        // Notification settings
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notification Settings", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                val context = androidx.compose.ui.platform.LocalContext.current
                val prefs = remember { NotificationPreferences(context) }
                val notifEnabled by prefs.notificationsEnabled.collectAsState(initial = true)
                val msgSounds by prefs.messageSoundsEnabled.collectAsState(initial = true)
                val prodUpdates by prefs.productUpdatesEnabled.collectAsState(initial = true)
                val announcements by prefs.announcementsEnabled.collectAsState(initial = true)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Notifications")
                    Switch(checked = notifEnabled, onCheckedChange = { scope.launch { prefs.setNotificationsEnabled(it) } })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Message Sounds")
                    Switch(checked = msgSounds, onCheckedChange = { scope.launch { prefs.setMessageSoundsEnabled(it) } })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Product Updates")
                    Switch(checked = prodUpdates, onCheckedChange = { scope.launch { prefs.setProductUpdatesEnabled(it) } })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Announcements")
                    Switch(checked = announcements, onCheckedChange = { scope.launch { prefs.setAnnouncementsEnabled(it) } })
                }
            }
        }

        // Account deletion
        Button(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Delete Account")
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
