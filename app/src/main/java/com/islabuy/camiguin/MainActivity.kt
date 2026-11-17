package com.islabuy.camiguin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.islabuy.camiguin.ui.screens.*
import com.islabuy.camiguin.ui.theme.IslaBuyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IslaBuyTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    IslaBuyNavHost()
                }
            }
        }
    }
}

@Composable
fun IslaBuyNavHost() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("categories") },
                    icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                    label = { Text("Categories") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("sell") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Sell") },
                    label = { Text("Sell") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("messages") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(padding) }
            composable("categories") { CategoriesScreen() }
            composable("sell") { SellScreen() }
            composable("messages") { MessagesScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("seller_dashboard") { SellerDashboardScreen() }
            composable("product/{id}") { backStack ->
                ProductDetailScreen(backStack.arguments?.getString("id"))
            }
            composable("store/{sellerId}") { backStack ->
                StoreScreen(backStack.arguments?.getString("sellerId"))
            }
            composable("chat/{conversationId}") { backStack ->
                ChatScreen(backStack.arguments?.getString("conversationId"))
            }
            composable("admin") { AdminScreen() }
        }
    }
}
