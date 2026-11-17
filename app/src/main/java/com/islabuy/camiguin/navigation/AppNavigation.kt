package com.islabuy.camiguin.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islabuy.camiguin.data.repository.ChatRepository
import com.islabuy.camiguin.data.repository.UserRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import com.islabuy.camiguin.ui.components.IslaProduct
import com.islabuy.camiguin.ui.theme.NavyBlue
import com.islabuy.camiguin.ui.components.sampleProducts
import com.islabuy.camiguin.ui.screens.*

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf("welcome") }
    val currentScreen = backStack.last()
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedProduct by remember { mutableStateOf<IslaProduct?>(null) }
    val userRepo = remember { UserRepository() }
    val chatRepo = remember { ChatRepository() }
    val scope = rememberCoroutineScope()
    val uid = userRepo.currentUser()?.uid ?: ""

    fun navigate(screen: String) { backStack.add(screen) }
    fun goBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        else if (currentScreen != "home") backStack.add("home")
    }

    // System back must never exit the app; pop the stack or go home.
    BackHandler(enabled = true) { goBack() }

    val isRoot = currentScreen in setOf("welcome", "home")

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isRoot) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyBlue)
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    IconButton(onClick = { goBack() }) {
                        Text("←", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = screenTitle(currentScreen),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (currentScreen) {
                    "welcome" -> WelcomeScreen(
                        onGetStarted = { navigate("home") },
                        onLogin = { navigate("home") }
                    )
                    "home" -> HomeScreen(
                        selectedIndex = selectedTab,
                        onTabSelected = { idx ->
                            selectedTab = idx
                            val target = when (idx) {
                                1 -> "categories"
                                2 -> "sell"
                                3 -> "orders"
                                4 -> "profile"
                                else -> "home"
                            }
                            // Tab switches reset drill-downs but keep a sensible back path.
                            while (backStack.size > 1 && backStack.last() != "home") backStack.removeAt(backStack.lastIndex)
                            if (target != "home") backStack.add(target)
                        },
                        onProductClick = { p -> selectedProduct = p; navigate("productDetails") },
                        onNotifications = { navigate("notifications") }
                    )
                    "categories" -> CategoriesScreen(onBack = { goBack() })
                    "sell" -> SellScreen(onBack = { goBack() })
                    "orders" -> OrdersHistoryScreen(buyerId = uid, onBack = { goBack() })
                    "profile" -> ProfileScreen(
                        onSettings = { navigate("settings") },
                        onMyProducts = { navigate("sellerDashboard") },
                        onMyOrders = { navigate("orders") },
                        onMyStore = { navigate("store") },
                        onMessages = { navigate("messages") },
                        onAdmin = { navigate("admin") },
                        onLogout = { userRepo.signOut(); backStack.clear(); backStack.add("welcome") }
                    )
                    "productDetails" -> ProductDetailsScreen(
                        product = selectedProduct ?: sampleProducts.first(),
                        onBack = { goBack() },
                        onMessageSeller = {
                            val prod = selectedProduct ?: sampleProducts.first()
                            if (uid.isNotBlank()) {
                                scope.launch {
                                    runCatching {
                                        chatRepo.getOrCreateConversation(
                                            productId = prod.id,
                                            buyerId = uid,
                                            sellerId = prod.sellerId.ifBlank { prod.seller },
                                            productTitle = prod.name,
                                            productImage = null
                                        )
                                    }
                                }
                            }
                            navigate("messages")
                        }
                    )
                    "messages" -> MessagesScreen()
                    "settings" -> SettingsScreen(onBack = { goBack() })
                    "sellerDashboard" -> SellerOrderDashboardScreen(sellerId = uid, onBack = { goBack() })
                    "store" -> StoreScreen(sellerId = uid, onBack = { goBack() })
                    "admin" -> AdminPanelScreen(onBack = { goBack() })
                    "notifications" -> NotificationHistoryScreen(userId = uid, onBack = { goBack() })
                    "cart" -> CartScreen(onBack = { goBack() }, onCheckout = { navigate("checkout") })
                    "checkout" -> CheckoutScreen(onBack = { goBack() }, onConfirm = { _, _, _, _ -> navigate("payment") })
                    "payment" -> PaymentMethodScreen(onBack = { goBack() })
                }
            }
        }
    }
}

private fun screenTitle(screen: String): String = when (screen) {
    "categories" -> "Categories"
    "sell" -> "Sell"
    "orders" -> "Orders"
    "profile" -> "Profile"
    "productDetails" -> "Product Details"
    "settings" -> "Settings"
    "sellerDashboard" -> "Seller Dashboard"
    "store" -> "My Store"
    "admin" -> "Admin Panel"
    "notifications" -> "Notifications"
    "cart" -> "Cart"
    "checkout" -> "Checkout"
    "payment" -> "Payment"
    else -> ""
}