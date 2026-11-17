package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.islabuy.camiguin.R
import com.islabuy.camiguin.ui.components.*
import com.islabuy.camiguin.ui.theme.*

@Composable
fun HomeScreen(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    onProductClick: (IslaProduct) -> Unit = {},
    onNotifications: () -> Unit = {}
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val filteredProducts = sampleProducts.filter { product ->

        val matchesSearch =
            product.name.contains(
                searchQuery,
                ignoreCase = true
            ) || product.seller.contains(
                searchQuery,
                ignoreCase = true
            )

        val matchesCategory =
            selectedCategory == "All" ||
            product.category == selectedCategory

        matchesSearch && matchesCategory
    }

    Scaffold(
        containerColor = ScreenBackground,

        bottomBar = {
            IslaBuyBottomNavigation(
                selectedIndex = selectedIndex,
                onSelect = onTabSelected
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // TOP NAVIGATION HEADER

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(NavyBlue)
                    .padding(
                        horizontal = 18.dp,
                        vertical = 15.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "IslaBuy",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Camiguin Edition",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onNotifications
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = ErrorRed
                            ) {
                                Text("3")
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            }

            // SEARCH BAR

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue)
                    .padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 15.dp
                    )
            ) {
                IslaBuySearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                    }
                )
            }

            // MAIN CONTENT

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {

                // CAMIGUIN PROMOTIONAL BANNER

                Image(
                    painter = painterResource(
                        R.drawable.camiguin_banner
                    ),
                    contentDescription = "Discover Camiguin",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .height(132.dp)
                        .background(
                            NavyBlue,
                            RoundedCornerShape(18.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(
                    modifier = Modifier.height(17.dp)
                )

                // CATEGORY TITLE

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Categories",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )

                    TextButton(
                        onClick = {
                            onTabSelected(1)
                        }
                    ) {
                        Text(
                            "See All →",
                            color = NavyBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // CATEGORY CARDS

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 5.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {

                    islaBuyCategories.forEach { category ->

                        CategoryCard(
                            category = category,
                            selected =
                                selectedCategory == category.name,
                            onClick = {
                                selectedCategory = category.name
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // FEATURED PRODUCTS TITLE

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Featured Products",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )

                    TextButton(
                        onClick = {
                            selectedCategory = "All"
                            searchQuery = ""
                        }
                    ) {
                        Text(
                            "See All →",
                            color = NavyBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // TWO-COLUMN PRODUCT GRID

            if (filteredProducts.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No products found.",
                        color = SecondaryText
                    )
                }

            } else {

                ProductGrid(
                    products = filteredProducts,
                    onProductClick = { product ->
                        onProductClick(product)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}