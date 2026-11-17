package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.R
import com.islabuy.camiguin.ui.components.*
import com.islabuy.camiguin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: IslaProduct = sampleProducts.first(),
    onBack: () -> Unit = {},
    onMessageSeller: () -> Unit = {},
    onAddToCart: (Int) -> Unit = {},
    onBuyNow: (Int) -> Unit = {}
) {
    var quantity by remember { mutableStateOf(1) }
    var favorite by remember { mutableStateOf(false) }
    val isPerishable = false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { favorite = !favorite }) {
                        Icon(
                            if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (favorite) Color.Red else PrimaryText
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ProductImageGallery(imageRes = product.imageRes)

            Column(modifier = Modifier.padding(16.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleLarge, color = PrimaryText)
                Spacer(Modifier.height(4.dp))
                Text("₱${"%,.2f".format(product.price)}", color = NavyBlue, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text("Category: ${product.category}", color = SecondaryText, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(if (product.available) IslaGreen else ErrorRed, androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(if (product.available) "In Stock" else "Unavailable", color = if (product.available) IslaGreen else ErrorRed, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                Spacer(Modifier.height(12.dp))
                Text("Description", fontWeight = FontWeight.Bold, color = PrimaryText)
                Text("Fresh local product from Camiguin. Quality guaranteed.", color = SecondaryText, fontSize = 14.sp)

                Spacer(Modifier.height(12.dp))
                SellerInfoCard(sellerName = product.seller, onVisitStore = {})

                if (isPerishable) {
                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = LightOrange)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Perishable Product", fontWeight = FontWeight.Bold, color = IslaOrange)
                            Text("Expires: 2025-12-31 12:00", color = PrimaryText, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Quantity", fontWeight = FontWeight.Bold, color = PrimaryText)
                QuantitySelector(quantity = quantity, onQuantityChange = { quantity = it })

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onMessageSeller,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Message Seller")
                    }
                }
                Spacer(Modifier.height(8.dp))
                AddToCartButton(text = "Add to Cart", onClick = { onAddToCart(quantity) })
                Spacer(Modifier.height(8.dp))
                IslaBuyPrimaryButton(text = "Buy Now", onClick = { onBuyNow(quantity) })
            }
        }
    }
}