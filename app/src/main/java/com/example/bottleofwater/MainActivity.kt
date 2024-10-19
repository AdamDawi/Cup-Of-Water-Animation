package com.example.bottleofwater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bottleofwater.ui.theme.BottleBlue
import com.example.bottleofwater.ui.theme.BottleOfWaterTheme
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CupOfWater(
            fillPercentage = {
                fillPercentage.value
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        AddWaterButton(fillPercentage = fillPercentage)
    }
}


@Composable
private fun AddWaterButton(
    fillPercentage: Animatable<Float, AnimationVector1D>
) {
    val lifecycleScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Icon(
            modifier = Modifier
                .size(44.dp)
                .background(BottleBlue, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    onClick = {
                        lifecycleScope.launch {
                            fillPercentage.animateTo(
                                (fillPercentage.value + 0.05f).coerceAtMost(maximumValue = 0.8f),
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainContentPreview() {
    MainContent()
}