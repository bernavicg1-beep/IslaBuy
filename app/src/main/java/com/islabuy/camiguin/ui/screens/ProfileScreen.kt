package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.islabuy.camiguin.data.model.User
import com.islabuy.camiguin.data.repository.UserRepository

@Composable
fun ProfileScreen(navController: NavController) {
    val userRepo = remember { UserRepository() }
    var user by remember { mutableStateOf<User?>(null) }

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

            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings")
            }
        }
    }
}
