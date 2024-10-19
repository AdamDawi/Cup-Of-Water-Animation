package com.example.bottleofwater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.example.bottleofwater.ui.theme.BottleBlue
import com.example.bottleofwater.ui.theme.BottleOfWaterTheme
import com.example.bottleofwater.ui.theme.LightWater
import com.example.bottleofwater.ui.theme.UltraLightBlue
import com.example.bottleofwater.ui.theme.Water

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottleOfWaterTheme {
                MainContent()

            }
        }
    }
}

@Composable
fun MainContent() {
    // Infinite transition for animating wave motion
    val infiniteTransition = rememberInfiniteTransition()

    // Animation for the wave height (it oscillates up and down)
    val waveLightOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val waveOffset = infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val bottleHeight = 500f
        val bottleWidth = 200f
        val centerX = (canvasWidth - bottleWidth) / 2
        val ovalRect = Rect(centerX - 50f, canvasHeight - bottleHeight - 40f, centerX + 250f, canvasHeight - bottleHeight + 40f)

        val bottlePath = Path().apply {
            moveTo(centerX, canvasHeight - 100f) // Start at the bottom left corner of the bottle
            cubicTo(
                centerX,
                canvasHeight - 100f,
                centerX + 100f,
                canvasHeight - 40f,
                centerX + 200f,
                canvasHeight - 100f
            )
            lineTo(centerX + 250f, canvasHeight - bottleHeight)
            arcTo(
                rect = ovalRect,
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(centerX, canvasHeight - 100f)
        }

        val bottlePath2 = Path().apply {
            moveTo(centerX + 250f, canvasHeight - bottleHeight)
            arcTo(
                rect = ovalRect,
                startAngleDegrees = 0f,
                sweepAngleDegrees = 359f,
                forceMoveTo = false
            )
        }

        drawPath(
            path = bottlePath2,
            color = UltraLightBlue
        )

        // Water fill level (e.g., 50% of the bottle)
        val fillPercentage = 0.5f // Change this value to adjust water level (0.0 to 1.0)
        val waterLevelHeight =
            canvasHeight - 100f - (bottleHeight * fillPercentage) // Adjust water height inside the bottle

        // Path for water fill
        val waterPath = Path().apply {
            moveTo(centerX, canvasHeight - 100f) // Bottom-left of the water
            cubicTo(
                centerX,
                canvasHeight - 100f,
                centerX + 100f,
                canvasHeight - 40f,
                centerX + 200f,
                canvasHeight - 100f
            ) // Bottom curve of the bottle
            lineTo(
                centerX + 200f + 60f * fillPercentage,
                waterLevelHeight
            ) // Right side up to water level
            cubicTo(
                centerX + 200f + 60f * fillPercentage,
                waterLevelHeight,
                centerX + (150f * (waveOffset.value/100)) + (50f * fillPercentage),
                waterLevelHeight - (50f * waveOffset.value/100),
                centerX - 60f * fillPercentage,
                waterLevelHeight
            )
            lineTo(centerX - 60f * fillPercentage, waterLevelHeight) // Left side at water level
            close() // Close water shape
        }
        val waterLightPath = Path().apply {
            moveTo(centerX, canvasHeight - 100f)
            cubicTo(
                centerX,
                canvasHeight - 100f,
                centerX + 100f,
                canvasHeight - 40f,
                centerX + 200f,
                canvasHeight - 100f
            )
            lineTo(
                centerX + 200f + 60f * fillPercentage,
                waterLevelHeight
            )
            cubicTo(
                centerX + 200f + 60f * fillPercentage,
                waterLevelHeight,
                centerX + 70f * (waveLightOffset.value/100) + (50f * fillPercentage),
                waterLevelHeight - (90f * waveLightOffset.value/100),
                centerX - 50f * fillPercentage,
                waterLevelHeight - 60f * waveLightOffset.value/100
            )
            lineTo(centerX - 60f * fillPercentage, waterLevelHeight)
            close()
        }
        drawPath(
            path = waterLightPath,
            color = LightWater
        )
        drawPath(
            path = waterPath,
            color = Water,
            alpha = 0.5f
        )

        drawPath(
            path = bottlePath,
            color = BottleBlue,
            alpha = 0.3f
        )

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainContentPreview() {
    MainContent()
}