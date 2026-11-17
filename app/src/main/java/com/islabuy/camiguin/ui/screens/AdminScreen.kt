package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Products", "Sellers", "Announcements", "Categories")

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Admin Panel", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> AdminProductsTab()
            1 -> AdminSellersTab()
            2 -> AdminAnnouncementsTab()
            3 -> AdminCategoriesTab()
        }
    }
}

@Composable
private fun AdminProductsTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Product Moderation", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("View all products, search, delete inappropriate/fake/expired listings, review reported products.")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Search listings...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("No listings loaded yet.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AdminSellersTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Seller Management", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("View sellers, suspend/reactivate abusive sellers, review seller activity.")
        Text("No sellers loaded yet.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AdminAnnouncementsTab() {
    var newAnnouncement by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Announcement Management", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newAnnouncement,
            onValueChange = { newAnnouncement = it },
            label = { Text("New announcement text") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* create announcement */ }) {
            Text("Publish Announcement")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Active announcements will appear in the news ticker.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AdminCategoriesTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Category Management", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Add, edit, or remove product categories.")
        Text("12 default categories loaded.", style = MaterialTheme.typography.bodySmall)
    }
}
