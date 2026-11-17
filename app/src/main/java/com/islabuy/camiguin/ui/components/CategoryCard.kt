package com.islabuy.camiguin.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.islabuy.camiguin.ui.theme.*

data class ProductCategory(
    val name: String,
    val icon: ImageVector,
    val background: Color
)

val islaBuyCategories = listOf(
    ProductCategory("All", Icons.Default.ShoppingBasket, Color(0xFFFFE9D7)),
    ProductCategory("Fruits", Icons.Default.Eco, Color(0xFFE5F6DF)),
    ProductCategory("Seafood", Icons.Default.SetMeal, Color(0xFFDDF1FF)),
    ProductCategory("Crafts", Icons.Default.Backpack, Color(0xFFFFE9D7)),
    ProductCategory("Agriculture", Icons.Default.Spa, Color(0xFFE1F4E3)),
    ProductCategory("Fashion", Icons.Default.Checkroom, Color(0xFFFFE4EF)),
    ProductCategory("Home & Living", Icons.Default.Home, Color(0xFFFFF1D9)),
    ProductCategory("More", Icons.Default.MoreHoriz, Color(0xFFEDEFF4))
)

@Composable
fun CategoryCard(
    category: ProductCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier.size(66.dp),
            color = if (selected) SkyBlue else category.background,
            shape = RoundedCornerShape(17.dp),
            border = if (selected)
                BorderStroke(1.5.dp, NavyBlue)
            else null
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = when (category.name) {
                        "All" -> IslaOrange
                        "Fruits" -> IslaGreen
                        "Seafood" -> OceanBlue
                        "Fashion" -> Color(0xFFE85B9C)
                        else -> NavyBlue
                    },
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = category.name,
            fontSize = 10.sp,
            fontWeight = if (selected)
                FontWeight.Bold else FontWeight.Medium,
            color = PrimaryText,
            maxLines = 1
        )
    }
}