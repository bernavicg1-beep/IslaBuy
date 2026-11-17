package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.ui.theme.NavyBlue
import com.islabuy.camiguin.util.Categories

@Composable
fun CategoriesScreen(onBack: () -> Unit = {}) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<Listing>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedCategory == null) {
            // Category grid
            Text("Categories", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Categories.ALL) { category ->
                    Card(
                        onClick = { selectedCategory = category },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            // Products in category
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedCategory = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(selectedCategory!!, style = MaterialTheme.typography.titleLarge)
            }
            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products in this category yet.")
                }
            } else {
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
        }
    }
}
