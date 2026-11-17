package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.data.model.CartItem
import com.islabuy.camiguin.ui.components.IslaBuyPrimaryButton
import com.islabuy.camiguin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    items: List<CartItem> = emptyList(),
    onConfirm: (name: String, phone: String, address: String, deliveryFee: Double) -> Unit = { _, _, _, _ -> },
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var deliveryFee by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf("") }

    val subtotal = items.sumOf { it.subtotal }
    val fee = deliveryFee.toDoubleOrNull() ?: 0.0
    val total = subtotal + fee

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Buyer Information", fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Delivery Address") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = deliveryFee, onValueChange = { deliveryFee = it }, label = { Text("Delivery Fee") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Text("Order Summary", fontWeight = FontWeight.Bold, color = PrimaryText)
            items.forEach { item ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.productName} x${item.quantity}", color = SecondaryText, fontSize = 13.sp)
                    Text("₱${"%,.2f".format(item.subtotal)}", color = PrimaryText, fontSize = 13.sp)
                }
            }
            Divider(Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = SecondaryText)
                Text("₱${"%,.2f".format(subtotal)}", color = PrimaryText)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery Fee", color = SecondaryText)
                Text("₱${"%,.2f".format(fee)}", color = PrimaryText)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, color = PrimaryText)
                Text("₱${"%,.2f".format(total)}", fontWeight = FontWeight.ExtraBold, color = NavyBlue)
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = ErrorRed, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))
            IslaBuyPrimaryButton(
                text = "Confirm Order",
                onClick = {
                    if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                        error = "Please fill in all required fields."
                    } else {
                        error = ""
                        onConfirm(name, phone, address, fee)
                    }
                }
            )
        }
    }
}