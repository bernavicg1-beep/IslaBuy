package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.islabuy.camiguin.R
import com.islabuy.camiguin.ui.components.ThreeDButton
import com.islabuy.camiguin.ui.components.ThreeDCard
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(padding: PaddingValues) {
    val viewModel: HomeViewModel = viewModel()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val welcomeText = stringResource(R.string.welcome_ticker)
    var announcements by remember { mutableStateOf(listOf(welcomeText)) }

    Column(modifier = Modifier.padding(padding)) {
        // Running news ticker
        NewsTicker(messages = announcements)

        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.search(it) },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Product grid - 2 columns, paginated
        if (isLoading && products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No products available yet. Be the first to sell!")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(product = product)
                }
                // Load more on scroll
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else {
                        ThreeDButton(
                            text = "Load More",
                            onClick = { viewModel.loadProducts() },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsTicker(messages: List<String>) {
    // Repeat so the text always overflows and keeps scrolling.
    val text = (messages + messages + messages).joinToString("   •   ")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        tonalElevation = 6.dp
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .basicMarquee(iterations = Int.MAX_VALUE, initialDelayMillis = 0),
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true
    )
}

@Composable
fun ProductCard(product: Listing) {
    ThreeDCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "₱${product.price}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (product.isPerishable) {
                    Text(
                        text = "Expires: ${product.expirationDate} ${product.expirationTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
