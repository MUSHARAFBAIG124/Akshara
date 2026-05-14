package com.aksharadeepa.tutor.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Glassmorphism color tokens
object GlassTokens {
    val cardBackground = Color.White.copy(alpha = 0.18f)
    val cardBackgroundLight = Color.White.copy(alpha = 0.30f)
    val cardBorder = Color.White.copy(alpha = 0.35f)
    val cardBorderSubtle = Color.White.copy(alpha = 0.20f)
    val overlayWhite = Color.White.copy(alpha = 0.08f)

    val gradientStart = Color(0xFF312E81) // deep indigo
    val gradientMid = Color(0xFF4F46E5)   // indigo
    val gradientEnd = Color(0xFF0D9488)    // teal

    val screenGradient = Brush.linearGradient(
        colors = listOf(gradientStart, gradientMid, gradientEnd),
        start = Offset(0f, 0f),
        end = Offset(1000f, 2000f)
    )

    val accentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF818CF8), Color(0xFF14B8A6)),
        start = Offset(0f, 0f),
        end = Offset(500f, 500f)
    )
}

/**
 * A gradient background that fills the screen behind all content.
 */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GlassTokens.screenGradient)
    ) {
        content()
    }
}

/**
 * A frosted-glass card surface.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = GlassTokens.cardBackground,
    borderColor: Color = GlassTokens.cardBorder,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        content()
    }
}
