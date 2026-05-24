package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Fire Flame for Streaks
 */
@Composable
fun StreakFlame(
    streak: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    val translationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameTranslate"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val path = Path().apply {
                moveTo(size.width / 2, size.height * 0.1f)
                cubicTo(
                    size.width * 0.2f, size.height * 0.4f,
                    size.width * 0.1f, size.height * 0.7f,
                    size.width * 0.5f, size.height * 0.95f
                )
                cubicTo(
                    size.width * 0.9f, size.height * 0.7f,
                    size.width * 0.8f, size.height * 0.4f,
                    size.width / 2, size.height * 0.1f
                )
                close()
            }
            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF4500)),
                    center = Offset(size.width / 2, size.height * 0.6f),
                    radius = size.width * 0.5f
                )
            )
            // Hot core
            drawCircle(
                color = Color.White,
                radius = size.width * 0.15f,
                center = Offset(size.width / 2, size.height * 0.65f)
            )
        }
        Text(
            text = "$streak 天连击",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF9F00)
            )
        )
    }
}

/**
 * Regular Hexagon Shape Generator
 */
fun createHexagonPath(size: androidx.compose.ui.geometry.Size): Path {
    val path = Path()
    val radius = size.width / 2
    val centerX = size.width / 2
    val centerY = size.height / 2
    for (i in 0 until 6) {
        val angleRad = Math.toRadians((60 * i + 30).toDouble())
        val x = centerX + radius * cos(angleRad).toFloat()
        val y = centerY + radius * sin(angleRad).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

val HexagonShape = GenericShape { size, _ ->
    val radius = size.width / 2
    val centerX = size.width / 2
    val centerY = size.height / 2
    for (i in 0 until 6) {
        val angleRad = Math.toRadians((60 * i + 30).toDouble())
        val x = (centerX + radius * cos(angleRad)).toFloat()
        val y = (centerY + radius * sin(angleRad)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/**
 * Interactive Radar Chart
 */
@Composable
fun RadarChart(
    labels: List<String>,
    values: List<Float>, // Values must be scaled 0f to 1f
    modifier: Modifier = Modifier,
    fillColor: Color = Color(0x3300F5A0),
    strokeColor: Color = Color(0xFF00F5A0)
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.width.coerceAtMost(size.height) / 2 * 0.82f
        val count = labels.size

        // Draw web rings (3 levels)
        for (ring in 1..4) {
            val ringRadius = radius * (ring / 4f)
            val path = Path()
            for (i in 0 until count) {
                val angle = Math.toRadians((i * 360.0 / count) - 90)
                val x = (centerX + ringRadius * cos(angle)).toFloat()
                val y = (centerY + ringRadius * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.1f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw spokes
        for (i in 0 until count) {
            val angle = Math.toRadians((i * 360.0 / count) - 90)
            val x = (centerX + radius * cos(angle)).toFloat()
            val y = (centerY + radius * sin(angle)).toFloat()
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(centerX, centerY),
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw filled value region
        val valuePath = Path()
        for (i in 0 until count) {
            val valPercent = values.getOrElse(i) { 0.5f }.coerceIn(0f, 1f)
            val ringRadius = radius * valPercent
            val angle = Math.toRadians((i * 360.0 / count) - 90)
            val x = (centerX + ringRadius * cos(angle)).toFloat()
            val y = (centerY + ringRadius * sin(angle)).toFloat()
            if (i == 0) valuePath.moveTo(x, y) else valuePath.lineTo(x, y)
        }
        valuePath.close()

        drawPath(
            path = valuePath,
            color = fillColor
        )
        drawPath(
            path = valuePath,
            color = strokeColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

/**
 * Custom Star Difficulty rating view
 */
@Composable
fun StarRatingBar(
    rating: Int,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    activeColor: Color = Color(0xFFFFB800),
    inactiveColor: Color = Color(0xFF1E293B)
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..maxStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(starSize),
                tint = if (i <= rating) activeColor else inactiveColor
            )
        }
    }
}
