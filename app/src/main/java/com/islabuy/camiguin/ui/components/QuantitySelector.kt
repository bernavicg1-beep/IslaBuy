package com.islabuy.camiguin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islabuy.camiguin.ui.theme.*

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    min: Int = 1,
    max: Int = 99,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = { if (quantity > min) onQuantityChange(quantity - 1) },
            enabled = quantity > min
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease")
        }
        Text(
            text = quantity.toString(),
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        IconButton(
            onClick = { if (quantity < max) onQuantityChange(quantity + 1) },
            enabled = quantity < max
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase")
        }
    }
}