package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    var rotation: Float = 0f
)

@Composable
fun ConfettiEffect(modifier: Modifier = Modifier) {
    val particles = remember {
        List(80) {
            ConfettiParticle(
                x = Random.nextFloat() * 1000f,
                y = -50f - Random.nextFloat() * 500f,
                vx = Random.nextFloat() * 6f - 3f,
                vy = Random.nextFloat() * 8f + 5f,
                size = Random.nextFloat() * 15f + 10f,
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = 0.9f
                ),
                rotationSpeed = Random.nextFloat() * 10f - 5f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val elapsed by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "elapsed"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            // Update positioning
            p.x = (p.x + p.vx)
            if (p.x < -40f) p.x = width + 40f
            if (p.x > width + 40f) p.x = -40f

            p.y = (p.y + p.vy)
            if (p.y > height + 50f) {
                p.y = -50f
                p.x = Random.nextFloat() * width
            }

            p.rotation = (p.rotation + p.rotationSpeed) % 360f

            rotate(p.rotation, Offset(p.x + p.size / 2, p.y + p.size / 2)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(p.x, p.y),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size * 1.6f)
                )
            }
        }
    }
}
