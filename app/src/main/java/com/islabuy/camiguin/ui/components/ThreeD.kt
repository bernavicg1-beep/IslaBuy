package com.islabuy.camiguin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 3D look: vertical gradient + drop shadow + rounded corners.
 */
@Composable
fun Modifier.threeD(
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 6.dp,
    baseColor: Color? = null
): Modifier {
    val color = baseColor ?: MaterialTheme.colorScheme.primary
    return this
        .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius), clip = false)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.95f),
                    color.copy(alpha = 0.75f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
        .padding(0.dp)
}

@Composable
fun ThreeDButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ThreeDCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.padding(0.dp)) {
            content()
        }
    }
}
