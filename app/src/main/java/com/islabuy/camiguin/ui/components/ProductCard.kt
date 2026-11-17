package com.islabuy.camiguin.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.islabuy.camiguin.R
import com.islabuy.camiguin.ui.theme.*

data class IslaProduct(
    val id: String,
    val name: String,
    val price: Double,
    val seller: String,
    val imageRes: Int,
    val category: String,
    val available: Boolean = true
)

val sampleProducts = listOf(
    IslaProduct(
        "1", "Fresh Mangoes (1 Kilo)",
        120.0, "Maria Santos",
        R.drawable.mango_product, "Fruits"
    ),
    IslaProduct(
        "2", "Fresh Fish",
        200.0, "Juan Dela Cruz",
        R.drawable.fish_product, "Seafood"
    ),
    IslaProduct(
        "3", "Organic Vegetables",
        50.0, "Pedro Farm",
        R.drawable.vegetable_product, "Agriculture"
    ),
    IslaProduct(
        "4", "Local Handicrafts",
        150.0, "Camiguin Crafts",
        R.drawable.crafts_product, "Crafts"
    )
)

@Composable
fun ProductCard(
    product: IslaProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var favorite by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {

                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable {
                            favorite = !favorite
                        },
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.95f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (favorite)
                                Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (favorite)
                                Color.Red else NavyBlue,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                if (!product.available) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(7.dp),
                        color = ErrorRed,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Sold out",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {

                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "₱${"%,.2f".format(product.price)}",
                    color = NavyBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = product.seller,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (product.available)
                                    IslaGreen else ErrorRed
                            )
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = if (product.available)
                            "In Stock" else "Unavailable",
                        fontSize = 10.sp,
                        color = if (product.available)
                            IslaGreen else ErrorRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}