package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.data.model.Store
import com.islabuy.camiguin.ui.theme.NavyBlue

@Composable
fun StoreScreen(sellerId: String? = null, onBack: () -> Unit = {}) {
    var store by remember { mutableStateOf<Store?>(null) }
    var products by remember { mutableStateOf<List<Listing>>(emptyList()) }
    var isFollowing by remember { mutableStateOf(false) }

    LaunchedEffect(sellerId) {
        store = Store(
            storeId = "store1",
            sellerId = sellerId ?: "",
            storeName = "Juan's Farm Store",
            storeLogo = null,
            storeBanner = null,
            description = "Fresh farm products from Camiguin. Fruits, vegetables, and more.",
            location = "Mambajao, Camiguin",
            createdAt = System.currentTimeMillis(),
            status = "active"
        )
        products = listOf(
            Listing(id = "1", title = "Fresh Lanzones", price = 150.0, category = "Fruits", isPerishable = true),
            Listing(id = "2", title = "Camiguin Peanuts", price = 80.0, category = "Local Products", isPerishable = false)
        )
    }

    store?.let { s ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Banner
            AsyncImage(
                model = s.storeBanner,
                contentDescription = "Store banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            // Store info
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = s.storeLogo,
                        contentDescription = "Store logo",
                        modifier = Modifier.size(60.dp)
                    )
                    Column {
                        Text(s.storeName, style = MaterialTheme.typography.headlineSmall)
                        Text(s.location, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(s.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { isFollowing = !isFollowing }) {
                        Text(if (isFollowing) "Following" else "Follow Store")
                    }
                    OutlinedButton(onClick = { /* contact seller */ }) {
                        Text("Contact Seller")
                    }
                }
            }

            // Products
            Text(
                "Products (${products.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(product.title, fontWeight = FontWeight.SemiBold)
                            Text("₱${"%,.2f".format(product.price)}", color = NavyBlue)
                        }
                    }
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
