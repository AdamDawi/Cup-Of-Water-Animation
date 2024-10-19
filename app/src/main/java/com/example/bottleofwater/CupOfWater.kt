package com.example.bottleofwater

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.bottleofwater.ui.theme.BottleBlue
import com.example.bottleofwater.ui.theme.Grey
import com.example.bottleofwater.ui.theme.LightWater
import com.example.bottleofwater.ui.theme.UltraLightBlue
import com.example.bottleofwater.ui.theme.Water

@Composable
fun CupOfWater(
    fillPercentage: () -> Float,
    cupHeight: Float = 800f,
    cupWidth: Float = 400f,
    bottomPadding: Float = 150f,
    cupWidthDifference: Float = 60f, // difference between top and bottom of the cup
    cupHeightDifference: Float = 40f, // difference between bottom and circle of the cup
    dropYOffset: Animatable<Float, AnimationVector1D>
) {
    val infiniteTransition = rememberInfiniteTransition()

    val waveLightOffset = infiniteTransition.animateFloat(
        initialValue = 70f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val waveOffset = infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val centerX = (canvasWidth - cupWidth) / 2
        val cupBottomY = canvasHeight - bottomPadding
        val cupUpperY = cupBottomY - cupHeight
        val cupRightX = centerX + cupWidth
        val waterLevelHeight = cupBottomY - (cupHeight * fillPercentage())
        val ovalRect = Rect(
            left = centerX - cupWidthDifference,
            top = cupUpperY - cupHeightDifference,
            right = cupRightX + cupWidthDifference,
            bottom = cupUpperY + cupHeightDifference
        )


        drawPath(
            path = createCupTopPath(
                cupWidthDifference = cupWidthDifference,
                cupUpperY = cupUpperY,
                cupRightX = cupRightX,
                ovalRect = ovalRect
            ),
            color = UltraLightBlue
        )

        drawBlurredPath(
            path = createCupShadowPath(
                centerX = centerX,
                canvasHeight = canvasHeight,
                bottomPadding = bottomPadding,
                cupRightX = cupRightX
            ),
            color = Grey,
            blurRadius = 15f
        )
        drawLine(
            color = BottleBlue,
            start = androidx.compose.ui.geometry.Offset(x = size.width/2, y = -200f),
            end = androidx.compose.ui.geometry.Offset(x = size.width/2, y = dropYOffset.value+100f),
            strokeWidth = 50f,
            cap = StrokeCap.Round,
            alpha = 0.5f
        )

        drawPath(
            path = createWaterPath(
                centerX = centerX,
                cupBottomY = cupBottomY,
                cupRightX = cupRightX,
                canvasHeight = canvasHeight,
                cupHeightDifference = cupHeightDifference,
                fillPercentage = fillPercentage,
                waterLevelHeight = waterLevelHeight,
                cupWidth = cupWidth,
                cupWidthDifference = cupWidthDifference,
                waveAmplitude = waveOffset.value
            ),
            color = LightWater
        )
        drawPath(
            path = createWaterPath(
                centerX = centerX,
                cupBottomY = cupBottomY,
                cupRightX = cupRightX,
                canvasHeight = canvasHeight,
                cupHeightDifference = cupHeightDifference,
                fillPercentage = fillPercentage,
                waterLevelHeight = waterLevelHeight,
                cupWidth = cupWidth,
                cupWidthDifference = cupWidthDifference,
                waveAmplitude = waveLightOffset.value
            ),
            color = Water,
            alpha = 0.5f
        )


        drawPath(
            path = createCupBodyPath(
                centerX = centerX,
                cupBottomY = cupBottomY,
                cupRightX = cupRightX,
                canvasHeight = canvasHeight,
                cupHeightDifference = cupHeightDifference,
                bottomPadding = bottomPadding,
                ovalRect = ovalRect,
                cupWidth = cupWidth,
                cupWidthDifference = cupWidthDifference,
                cupUpperY = cupUpperY
            ),
            color = BottleBlue,
            alpha = 0.3f
        )
    }
}

private fun createCupBodyPath(
    centerX: Float,
    cupBottomY: Float,
    cupRightX: Float,
    canvasHeight: Float,
    cupHeightDifference: Float,
    bottomPadding: Float,
    ovalRect: Rect,
    cupWidth: Float,
    cupWidthDifference: Float,
    cupUpperY: Float
): Path {
    return Path().apply {
        moveTo(x = centerX, y = cupBottomY)
        cubicTo(
            x1 = centerX,
            y1 = cupBottomY,
            x2 = centerX + cupWidth / 2,
            y2 = canvasHeight - cupHeightDifference,
            x3 = cupRightX,
            y3 = cupBottomY
        )
        lineTo(x = cupRightX + cupWidthDifference, y = cupUpperY)
        arcTo(
            rect = ovalRect,
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        lineTo(x = centerX, y = canvasHeight - bottomPadding)
    }
}

private fun createCupTopPath(
    cupWidthDifference: Float,
    cupUpperY: Float,
    cupRightX: Float,
    ovalRect: Rect,
): Path{
    return Path().apply {
        moveTo(
            x = cupRightX + cupWidthDifference,
            y = cupUpperY
        )
        arcTo(
            rect = ovalRect,
            startAngleDegrees = 0f,
            sweepAngleDegrees = 359f, // arc can't be equal 360f
            forceMoveTo = false
        )
    }
}

private fun createCupShadowPath(
    centerX: Float,
    canvasHeight: Float,
    bottomPadding: Float,
    cupRightX: Float,
): Path{
    val ovalRectShadow = Rect(
        left = centerX + 30f,
        top = canvasHeight - 100f,
        right = cupRightX - 30f,
        bottom = canvasHeight - 20f
    )
    return Path().apply {
        moveTo(x = centerX, y = canvasHeight - bottomPadding)
        arcTo(
            rect = ovalRectShadow,
            startAngleDegrees = 0f,
            sweepAngleDegrees = 359f, // arc can't be equal 360f
            forceMoveTo = false
        )
    }
}

private fun createWaterPath(
    centerX: Float,
    cupBottomY: Float,
    cupRightX: Float,
    canvasHeight: Float,
    cupHeightDifference: Float,
    fillPercentage: () -> Float,
    waterLevelHeight: Float,
    cupWidth: Float,
    cupWidthDifference: Float,
    waveAmplitude: Float,
): Path{
    return Path().apply {
        // Bottom-left of the water
        moveTo(x = centerX, y = cupBottomY)
        // Bottom curve of the bottle
        cubicTo(
            x1 = centerX,
            y1 = cupBottomY,
            x2 = centerX + cupWidth / 2,
            y2 = canvasHeight - cupHeightDifference,
            x3 = cupRightX,
            y3 = cupBottomY
        )
        // Right side up to water level
        lineTo(
            x = cupRightX + cupWidthDifference * fillPercentage(),
            y = waterLevelHeight
        )
        cubicTo(
            x1 = cupRightX + cupWidthDifference * fillPercentage(),
            y1 = waterLevelHeight,
            x2 = centerX + (cupWidth / 2) + (cupWidthDifference * fillPercentage()),
            y2 = waterLevelHeight - waveAmplitude,
            x3 = centerX - cupWidthDifference * fillPercentage(),
            y3 = waterLevelHeight
        )
        // Left side at water level
        lineTo(
            x = centerX - cupWidthDifference * fillPercentage(),
            y = waterLevelHeight
        )
    }
}
private fun DrawScope.drawBlurredPath(path: Path, color: Color, blurRadius: Float) {
    val bitmap =
        Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap.asImageBitmap())

    val paint = Paint().asFrameworkPaint().apply {
        this.color = color.toArgb()
        this.style = android.graphics.Paint.Style.FILL
        this.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }

    canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)

    drawImage(bitmap.asImageBitmap())
}