package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.ui.components.IslaBuyPrimaryButton
import com.islabuy.camiguin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    gcashNumber: String = "09XX-XXX-XXXX",
    onPayCash: () -> Unit = {},
    onPayGcash: (reference: String, proofUrl: String?) -> Unit = { _, _ -> },
    onBack: () -> Unit = {}
) {
    var selected by remember { mutableStateOf("CASH") }
    var reference by remember { mutableStateOf("") }
    var proofUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Payment Method") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Choose Payment Method", fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selected == "CASH", onClick = { selected = "CASH" }, label = { Text("Cash on Pickup") })
                FilterChip(selected = selected == "GCASH", onClick = { selected = "GCASH" }, label = { Text("GCash") })
            }

            Spacer(Modifier.height(16.dp))

            if (selected == "CASH") {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Cash on Pickup", fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Pay in cash when you pick up the item.", color = SecondaryText, fontSize = 13.sp)
                    }
                }
            } else {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("GCash Payment", fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Send payment to: $gcashNumber", color = SecondaryText, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reference,
                            onValueChange = { reference = it },
                            label = { Text("GCash Reference Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Upload payment proof (optional)", color = SecondaryText, fontSize = 12.sp)
                        OutlinedButton(onClick = { /* file picker */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Choose File")
                        }
                        if (proofUrl != null) {
                            Text("Proof attached", color = IslaGreen, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = ErrorRed, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))
            IslaBuyPrimaryButton(
                text = if (selected == "CASH") "Place Order (Cash)" else "Submit GCash Payment",
                onClick = {
                    if (selected == "GCASH" && reference.isBlank()) {
                        error = "Please enter the GCash reference number."
                    } else {
                        error = ""
                        if (selected == "CASH") onPayCash() else onPayGcash(reference, proofUrl)
                    }
                }
            )
        }
    }
}