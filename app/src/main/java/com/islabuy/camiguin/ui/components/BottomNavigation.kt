package com.islabuy.camiguin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.ui.theme.*

data class NavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun IslaBuyBottomNavigation(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Categories", Icons.Default.GridView),
        NavItem("Sell", Icons.Default.AddCircle),
        NavItem("Orders", Icons.Default.ShoppingCart),
        NavItem("Profile", Icons.Default.Person)
    )

    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->

                val selected = selectedIndex == index
                val color = if (selected) NavyBlue
                    else SecondaryText

                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(index) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = color
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (selected)
                                FontWeight.Bold
                            else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyBlue,
                        selectedTextColor = NavyBlue,
                        unselectedIconColor = SecondaryText,
                        unselectedTextColor = SecondaryText,
                        indicatorColor = SkyBlue
                    )
                )
            }
        }
    }
}