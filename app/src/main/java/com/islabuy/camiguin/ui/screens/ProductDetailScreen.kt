package com.islabuy.camiguin.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.ui.screens.openExternalNavigation

@Composable
fun ProductDetailScreen(id: String?) {
    var listing by remember { mutableStateOf<Listing?>(null) }
    var currentImageIndex by remember { mutableStateOf(0) }
    var showReportDialog by remember { mutableStateOf(false) }

    // Demo data
    LaunchedEffect(id) {
        listing = Listing(
            id = id ?: "",
            sellerId = "seller1",
            sellerName = "Juan's Farm",
            title = "Fresh Camiguin Lanzones",
            description = "Sweet and fresh lanzones from Camiguin. Harvested today. Perfect for snacking or making lanzones wine.",
            price = 150.0,
            category = "Fruits",
            quantity = 10,
            isPerishable = true,
            expirationDate = "2026-11-20",
            expirationTime = "18:00",
            latitude = 9.1731,
            longitude = 124.7284,
            pickupAddress = "Mambajao, Camiguin",
            imageUrls = listOf("https://example.com/img1.jpg")
        )
    }

    listing?.let { product ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image
            AsyncImage(
                model = product.imageUrls.getOrElse(currentImageIndex) { null },
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Title and price
                Text(product.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("₱${product.price}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text("Category: ${product.category}", style = MaterialTheme.typography.bodyMedium)
                Text("Quantity: ${product.quantity}", style = MaterialTheme.typography.bodyMedium)

                if (product.isPerishable) {
                    Text(
                        "Expires: ${product.expirationDate} ${product.expirationTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text("Description", style = MaterialTheme.typography.titleMedium)
                Text(product.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(12.dp))

                // Seller info
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Seller: ${product.sellerName}", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { /* navigate to store */ }) {
                            Text("Visit Store")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pickup location
                if (product.latitude != null && product.longitude != null) {
                    Text("Pickup Location", style = MaterialTheme.typography.titleMedium)
                    Text(product.pickupAddress ?: "No address", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                        PickupLocationSelector(
                            initialLat = product.latitude,
                            initialLng = product.longitude,
                            onLocationSelected = { _, _ -> }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // openExternalNavigation needs context - handled by caller
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get Directions")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* navigate to chat */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Contact Seller")
                    }
                    OutlinedButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Report")
                    }
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    if (showReportDialog) {
        var reportReason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Product") },
            text = {
                Column {
                    Text("Why are you reporting this product?")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Submit report to Firestore
                    showReportDialog = false
                }) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
