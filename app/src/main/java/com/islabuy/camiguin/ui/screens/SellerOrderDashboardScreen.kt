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
import com.islabuy.camiguin.data.model.Order
import com.islabuy.camiguin.data.repository.OrderRepository
import com.islabuy.camiguin.ui.theme.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrderDashboardScreen(
    sellerId: String,
    onBack: () -> Unit = {}
) {
    val repo = remember { OrderRepository() }
    val scope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }

    LaunchedEffect(sellerId) {
        scope.launch {
            try {
                val snap = repo.ordersForSeller(sellerId).get().await()
                orders = snap.documents.mapNotNull { it.toObject(Order::class.java)?.copy(orderId = it.id) }
            } catch (_: Exception) { }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Seller Orders") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) }) { padding ->
        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No orders yet.", color = SecondaryText) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
                items(orders, key = { it.orderId }) { order ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(14.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(order.productName, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text("Buyer: ${order.buyerId}", color = SecondaryText, fontSize = 12.sp)
                            Text("Qty: ${order.quantity}  •  ₱${"%,.2f".format(order.totalAmount)}", color = PrimaryText, fontSize = 13.sp)
                            Text("Status: ${order.orderStatus}  •  Payment: ${order.paymentStatus}", color = NavyBlue, fontSize = 12.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (order.orderStatus == "PENDING") {
                                    Button(onClick = { scope.launch { repo.updateOrderStatus(order.orderId, "CONFIRMED") } }) { Text("Accept") }
                                    OutlinedButton(onClick = { scope.launch { repo.updateOrderStatus(order.orderId, "CANCELLED") } }) { Text("Reject") }
                                } else if (order.orderStatus == "CONFIRMED") {
                                    Button(onClick = { scope.launch { repo.updateOrderStatus(order.orderId, "PREPARING") } }) { Text("Prepare") }
                                } else if (order.orderStatus == "PREPARING") {
                                    Button(onClick = { scope.launch { repo.updateOrderStatus(order.orderId, "READY") } }) { Text("Ready") }
                                } else if (order.orderStatus == "READY") {
                                    Button(onClick = { scope.launch { repo.updateOrderStatus(order.orderId, "OUT_FOR_DELIVERY") } }) { Text("Out for Delivery") }
                                } else if (order.orderStatus == "OUT_FOR_DELIVERY") {
                                    Button(onClick = { scope.launch { repo.updateOrderStatus(order.orderId, "COMPLETED") } }) { Text("Complete") }
                                }
                                if (order.paymentMethod == "GCASH" && order.paymentStatus == "PENDING_VERIFICATION") {
                                    Button(onClick = { scope.launch { repo.verifyGcashPayment(order.orderId, true) } }) { Text("Verify GCash") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}