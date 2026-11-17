package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.data.repository.ListingRepository
import com.islabuy.camiguin.data.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun SellerDashboardScreen() {
    val scope = rememberCoroutineScope()
    val repository = remember { ListingRepository() }
    val userRepo = remember { UserRepository() }
    val sellerId = userRepo.currentUser()?.uid ?: ""
    var listings by remember { mutableStateOf<List<Listing>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "Expired", "Sold")

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                listings = repository.getListingsBySeller(sellerId)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    val filteredListings = when (selectedTab) {
        0 -> listings.filter { it.status == "active" && !it.isExpired() }
        1 -> listings.filter { it.isExpired() || it.status == "expired" }
        2 -> listings.filter { it.status == "sold" }
        else -> listings
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("My Products", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No ${tabs[selectedTab].lowercase()} products")
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(filteredListings, key = { it.id }) { listing ->
                    SellerProductCard(
                        listing = listing,
                        onMarkSold = {
                            scope.launch {
                                repository.markAsSold(listing.id)
                                listings = repository.getListingsBySeller(sellerId)
                            }
                        },
                        onDelete = {
                            scope.launch {
                                repository.deleteListing(listing.id)
                                listings = repository.getListingsBySeller(sellerId)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SellerProductCard(
    listing: Listing,
    onMarkSold: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = listing.imageUrls.firstOrNull(),
                contentDescription = listing.title,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(listing.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("₱${listing.price}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text("Qty: ${listing.quantity} | ${listing.category}", style = MaterialTheme.typography.bodySmall)
                if (listing.isExpired()) {
                    Text("EXPIRED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }
            Column {
                if (listing.status == "active") {
                    TextButton(onClick = onMarkSold) { Text("Sold") }
                }
                TextButton(onClick = onDelete) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
