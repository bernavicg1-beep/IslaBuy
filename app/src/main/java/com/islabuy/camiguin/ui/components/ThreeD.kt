package com.islabuy.camiguin.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.threeD(
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 6.dp,
    tiltX: Float = 0f,
    tiltY: Float = 0f,
    baseColor: Color? = null
): Modifier {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val color = baseColor ?: MaterialTheme.colorScheme.primary
    val shadowColor = if (isDark) Color.Black else Color(0x55000000)
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .graphicsLayer {
            rotationX = tiltX
            rotationY = tiltY
            cameraDistance = 12f * density
            shadowElevation = elevation.value
        }
        .shadow(elevation = elevation, shape = shape, ambientColor = shadowColor, spotColor = shadowColor)
        .background(
            brush = Brush.verticalGradient(
                colors = if (isDark) listOf(
                    color.copy(alpha = 0.9f),
                    color.copy(alpha = 0.6f)
                ) else listOf(
                    color.copy(alpha = 0.95f),
                    color.copy(alpha = 0.75f)
                )
            ),
            shape = shape
        )
}

@Composable
fun ThreeDButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    elevation: Dp = 8.dp,
    text: String = "",
    content: @Composable (RowScope.() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "press-scale"
    )
    val lift by animateFloatAsState(
        targetValue = if (pressed) 2f else elevation.value,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "press-lift"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            shadowElevation = lift
            cameraDistance = 12f * density
        }.shadow(lift.dp, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = 2.dp
        )
    ) {
        if (content != null) content() else Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ThreeDCard(
    modifier: Modifier = Modifier,
    tiltX: Float = 0f,
    tiltY: Float = 0f,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.graphicsLayer {
            rotationX = tiltX
            rotationY = tiltY
            cameraDistance = 16f * density
            shadowElevation = elevation.value
        }.shadow(elevation, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.padding(0.dp)) { content() }
    }
}
