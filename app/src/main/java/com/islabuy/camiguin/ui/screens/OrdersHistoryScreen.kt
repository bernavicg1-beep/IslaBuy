package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.data.model.Order
import com.islabuy.camiguin.data.repository.OrderRepository
import com.islabuy.camiguin.ui.theme.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
    buyerId: String,
    onBack: () -> Unit = {}
) {
    val repo = remember { OrderRepository() }
    val scope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Pending", "Confirmed", "Preparing", "Ready", "Delivery", "Completed", "Cancelled")

    LaunchedEffect(buyerId) {
        scope.launch {
            try {
                val snap = repo.ordersForBuyer(buyerId).get().await()
                orders = snap.documents.mapNotNull { it.toObject(Order::class.java)?.copy(orderId = it.id) }
            } catch (_: Exception) { }
        }
    }

    val filtered = when (selectedTab) {
        1 -> orders.filter { it.orderStatus == "PENDING" }
        2 -> orders.filter { it.orderStatus == "CONFIRMED" }
        3 -> orders.filter { it.orderStatus == "PREPARING" }
        4 -> orders.filter { it.orderStatus == "READY" }
        5 -> orders.filter { it.orderStatus == "OUT_FOR_DELIVERY" }
        6 -> orders.filter { it.orderStatus == "COMPLETED" }
        7 -> orders.filter { it.orderStatus == "CANCELLED" }
        else -> orders
    }

    Scaffold(topBar = { TopAppBar(title = { Text("My Orders") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(t, fontSize = 11.sp) })
                }
            }
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No orders.", color = SecondaryText) }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                    items(filtered, key = { it.orderId }) { order ->
                        OrderCard(order)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("#${order.orderId.take(8)}", fontWeight = FontWeight.Bold, color = PrimaryText)
                Text(order.orderStatus, color = NavyBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(order.productName, color = PrimaryText, fontSize = 14.sp)
            Text("Seller: ${order.sellerId}", color = SecondaryText, fontSize = 12.sp)
            Text("Qty: ${order.quantity}  •  ₱${"%,.2f".format(order.totalAmount)}", color = PrimaryText, fontSize = 13.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.paymentMethod, color = SecondaryText, fontSize = 11.sp)
                Text(order.paymentStatus, color = if (order.paymentStatus == "VERIFIED") IslaGreen else WarningYellow, fontSize = 11.sp)
            }
        }
    }
}