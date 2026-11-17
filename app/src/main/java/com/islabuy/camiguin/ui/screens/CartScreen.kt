package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.data.model.CartItem
import com.islabuy.camiguin.ui.components.*
import com.islabuy.camiguin.ui.components.IslaBuyPrimaryButton
import com.islabuy.camiguin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    items: List<CartItem> = emptyList(),
    onRemove: (CartItem) -> Unit = {},
    onQuantityChange: (CartItem, Int) -> Unit = { _, _ -> },
    onToggleSelect: (CartItem) -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    val selectedItems = items.filter { it.selected }
    val subtotal = selectedItems.sumOf { it.subtotal }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Cart") }) }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your cart is empty", color = SecondaryText)
                    Spacer(Modifier.height(8.dp))
                    Text("Add products to get started", color = SecondaryText, fontSize = 12.sp)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items, key = { it.productId }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = item.selected, onCheckedChange = { onToggleSelect(item) })
                                Spacer(Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.productName, fontWeight = FontWeight.SemiBold, color = PrimaryText)
                                    Text(item.sellerName, fontSize = 12.sp, color = SecondaryText)
                                    Text("₱${"%,.2f".format(item.unitPrice)}", color = NavyBlue, fontWeight = FontWeight.Bold)
                                }
                                QuantitySelector(
                                    quantity = item.quantity,
                                    onQuantityChange = { onQuantityChange(item, it) }
                                )
                                IconButton(onClick = { onRemove(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = ErrorRed)
                                }
                            }
                        }
                    }
                }
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = SecondaryText)
                            Text("₱${"%,.2f".format(subtotal)}", fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                        Spacer(Modifier.height(8.dp))
                        IslaBuyPrimaryButton(
                            text = "Proceed to Checkout (${selectedItems.size})",
                            onClick = onCheckout,
                            enabled = selectedItems.isNotEmpty()
                        )
                    }
                }
            }
        }
    }
}
