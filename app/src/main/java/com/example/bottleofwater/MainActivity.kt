package com.example.bottleofwater

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bottleofwater.ui.theme.BottleBlue
import com.example.bottleofwater.ui.theme.BottleOfWaterTheme
import com.example.bottleofwater.ui.theme.Grey
import com.example.bottleofwater.ui.theme.LightWater
import com.example.bottleofwater.ui.theme.UltraLightBlue
import com.example.bottleofwater.ui.theme.Water
import kotlinx.coroutines.launch

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
    val fillPercentage = remember { Animatable(0.5f) }
    val lifecycleScope = rememberCoroutineScope()

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
        ) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            val cupHeight = 800f
            val cupWidth = 400f
            val bottomPadding = 150f
            val widthOffset = 50f
            val heightOffset = 40f
            val centerX = (canvasWidth - cupWidth) / 2
            val ovalRect = Rect(
                centerX - widthOffset,
                canvasHeight - bottomPadding - cupHeight - heightOffset,
                centerX + cupWidth + widthOffset,
                canvasHeight - bottomPadding - cupHeight + heightOffset
            )

            val ovalRectShadow = Rect(
                centerX + 30f,
                canvasHeight - 100f,
                centerX + cupWidth - 30f,
                canvasHeight - 20f
            )

            val bottlePath = Path().apply {
                moveTo(
                    centerX,
                    canvasHeight - bottomPadding
                ) // Start at the bottom left corner of the cup
                cubicTo(
                    centerX,
                    canvasHeight - bottomPadding,
                    centerX + cupWidth / 2,
                    canvasHeight - heightOffset,
                    centerX + cupWidth,
                    canvasHeight - bottomPadding
                )
                lineTo(centerX + cupWidth + widthOffset, canvasHeight - cupHeight - bottomPadding)
                arcTo(
                    rect = ovalRect,
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(centerX, canvasHeight - bottomPadding)
            }

            val bottlePath2 = Path().apply {
                moveTo(centerX + cupWidth + widthOffset, canvasHeight - cupHeight - bottomPadding)
                arcTo(
                    rect = ovalRect,
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 359f, // arc can't be equal 360f
                    forceMoveTo = false
                )
            }

            val bottlePath3 = Path().apply {
                moveTo(centerX, canvasHeight - bottomPadding)
                arcTo(
                    rect = ovalRectShadow,
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 359f, // arc can't be equal 360f
                    forceMoveTo = false
                )
            }

            drawPath(
                path = bottlePath2,
                color = UltraLightBlue
            )

            drawBlurredPath(
                path = bottlePath3,
                color = Grey,
                blurRadius = 15f
            )

            // Water fill level (e.g., 50% of the cup)

            val waterLevelHeight =
                canvasHeight - bottomPadding - (cupHeight * fillPercentage.value)

            // Path for water fill
            val waterPath = Path().apply {
                // Bottom-left of the water
                moveTo(centerX, canvasHeight - bottomPadding)
                // Bottom curve of the bottle
                cubicTo(
                    centerX,
                    canvasHeight - bottomPadding,
                    centerX + cupWidth / 2,
                    canvasHeight - heightOffset,
                    centerX + cupWidth,
                    canvasHeight - bottomPadding
                )
                // Right side up to water level
                lineTo(
                    centerX + cupWidth + widthOffset * fillPercentage.value,
                    waterLevelHeight
                )
                cubicTo(
                    centerX + cupWidth + widthOffset * fillPercentage.value,
                    waterLevelHeight,
                    centerX + (cupWidth/2 * (waveOffset.value / 100)) + (widthOffset * fillPercentage.value),
                    waterLevelHeight - (heightOffset * waveOffset.value / 100),
                    centerX - widthOffset * fillPercentage.value,
                    waterLevelHeight
                )
                // Left side at water level
                lineTo(
                    centerX - widthOffset * fillPercentage.value,
                    waterLevelHeight
                )
            }
            val waterLightPath = Path().apply {
                moveTo(centerX, canvasHeight - bottomPadding)
                cubicTo(
                    centerX,
                    canvasHeight - bottomPadding,
                    centerX + cupWidth / 2,
                    canvasHeight - heightOffset,
                    centerX + cupWidth,
                    canvasHeight - bottomPadding
                )
                lineTo(
                    centerX + cupWidth + widthOffset * fillPercentage.value,
                    waterLevelHeight
                )
                cubicTo(
                    centerX + cupWidth + widthOffset * fillPercentage.value,
                    waterLevelHeight,
                    centerX + 70f * (waveLightOffset.value / 100) + (widthOffset * fillPercentage.value),
                    waterLevelHeight - (90f * waveLightOffset.value / 100),
                    centerX - widthOffset * fillPercentage.value,
                    waterLevelHeight - 50f * waveLightOffset.value / 100
                )
                lineTo(centerX - widthOffset * fillPercentage.value, waterLevelHeight)
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
        Spacer(modifier = Modifier.height(20.dp))
            Icon(
                modifier = Modifier
                    .size(44.dp)
                    .background(BottleBlue, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        onClick = {
                            lifecycleScope.launch{
                                fillPercentage.animateTo(
                                    (fillPercentage.value+0.05f).coerceAtMost(maximumValue = 0.8f),
                                    animationSpec = TweenSpec(
                                        durationMillis = 500,
                                        easing = LinearEasing
                                    )
                                )
                            }
                        },
                    ),
                imageVector = Icons.Default.Add,
                tint = Color.White,
                contentDescription = null
            )
    }
}

fun DrawScope.drawBlurredPath(path: Path, color: Color, blurRadius: Float) {
    val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap.asImageBitmap())

    val paint = Paint().asFrameworkPaint().apply {
        this.color = color.toArgb()
        this.style = android.graphics.Paint.Style.FILL
        this.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }

    canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)

    drawImage(bitmap.asImageBitmap())
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainContentPreview() {
    MainContent()
}