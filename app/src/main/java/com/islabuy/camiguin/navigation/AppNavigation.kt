package com.islabuy.camiguin.navigation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.islabuy.camiguin.data.repository.UserRepository
import com.islabuy.camiguin.ui.components.IslaProduct
import com.islabuy.camiguin.ui.components.sampleProducts
import com.islabuy.camiguin.ui.screens.*

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("welcome") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedProduct by remember { mutableStateOf<IslaProduct?>(null) }
    val userRepo = remember { UserRepository() }
    val uid = userRepo.currentUser()?.uid ?: ""

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        when (currentScreen) {
            "welcome" -> WelcomeScreen(
                onGetStarted = { currentScreen = "home" },
                onLogin = { currentScreen = "home" }
            )
            "home" -> HomeScreen(
                selectedIndex = selectedTab,
                onTabSelected = { idx ->
                    selectedTab = idx
                    currentScreen = when (idx) {
                        1 -> "categories"
                        2 -> "sell"
                        3 -> "orders"
                        4 -> "profile"
                        else -> "home"
                    }
                },
                onProductClick = { p -> selectedProduct = p; currentScreen = "productDetails" }
            )
            "categories" -> CategoriesScreen(onBack = { currentScreen = "home"; selectedTab = 0 })
            "sell" -> SellScreen(onBack = { currentScreen = "home"; selectedTab = 0 })
            "orders" -> OrdersHistoryScreen(buyerId = uid, onBack = { currentScreen = "home"; selectedTab = 0 })
            "profile" -> ProfileScreen(
                onSettings = { currentScreen = "settings" },
                onMyProducts = { currentScreen = "sellerDashboard" },
                onMyOrders = { currentScreen = "orders" },
                onMyStore = { currentScreen = "store" },
                onAdmin = { currentScreen = "admin" },
                onLogout = { userRepo.signOut(); currentScreen = "welcome" }
            )
            "productDetails" -> ProductDetailsScreen(
                product = selectedProduct ?: sampleProducts.first(),
                onBack = { currentScreen = "home" }
            )
            "settings" -> SettingsScreen(onBack = { currentScreen = "profile" })
            "sellerDashboard" -> SellerOrderDashboardScreen(sellerId = uid, onBack = { currentScreen = "profile" })
            "store" -> StoreScreen(onBack = { currentScreen = "profile" })
            "admin" -> AdminPanelScreen(onBack = { currentScreen = "profile" })
            "notifications" -> NotificationHistoryScreen(userId = uid, onBack = { currentScreen = "home" })
            "cart" -> CartScreen(onBack = { currentScreen = "home" }, onCheckout = { currentScreen = "checkout" })
            "checkout" -> CheckoutScreen(onBack = { currentScreen = "cart" }, onConfirm = { _, _, _, _ -> currentScreen = "payment" })
            "payment" -> PaymentMethodScreen(onBack = { currentScreen = "checkout" })
        }
    }
}