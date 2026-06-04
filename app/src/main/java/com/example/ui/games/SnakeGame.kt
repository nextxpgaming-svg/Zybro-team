package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeGame(
    onScoreUpdated: (Int) -> Unit,
    onGameOver: (Int) -> Unit
) {
    val context = LocalContext.current
    val gridWidth = 16
    val gridHeight = 16

    var snake by remember { mutableStateOf(listOf(Offset(8f, 8f), Offset(8f, 9f), Offset(8f, 10f))) }
    var direction by remember { mutableStateOf(Direction.UP) }
    var food by remember { mutableStateOf(Offset(5f, 5f)) }
    var score by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    fun generateFood() {
        var newFood = Offset(Random.nextInt(0, gridWidth).toFloat(), Random.nextInt(0, gridHeight).toFloat())
        while (snake.contains(newFood)) {
            newFood = Offset(Random.nextInt(0, gridWidth).toFloat(), Random.nextInt(0, gridHeight).toFloat())
        }
        food = newFood
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(175 - (score / 10).coerceAtMost(100).toLong()) // gets progressively faster
            val head = snake.first()
            val nextHead = when (direction) {
                Direction.UP -> Offset(head.x, head.y - 1)
                Direction.DOWN -> Offset(head.x, head.y + 1)
                Direction.LEFT -> Offset(head.x - 1, head.y)
                Direction.RIGHT -> Offset(head.x + 1, head.y)
            }

            // Self-collision or wall-collision check
            if (nextHead.x < 0 || nextHead.x >= gridWidth || nextHead.y < 0 || nextHead.y >= gridHeight || snake.contains(nextHead)) {
                isRunning = false
                GameSoundManager.playGameOver()
                onGameOver(score)
                break
            }

            val newSnake = mutableListOf(nextHead)
            newSnake.addAll(snake)

            if (nextHead == food) {
                // Ate food
                score += 15
                onScoreUpdated(score)
                GameSoundManager.playScoreUp()
                GameSoundManager.triggerHaptic(context)
                generateFood()
            } else {
                newSnake.removeAt(newSnake.size - 1)
            }
            snake = newSnake
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Snake Board Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF263238))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val blockWidth = size.width / gridWidth
                val blockHeight = size.height / gridHeight

                // Draw food
                drawCircle(
                    color = Color.Red,
                    radius = blockWidth / 2.5f,
                    center = Offset(food.x * blockWidth + blockWidth / 2, food.y * blockHeight + blockHeight / 2)
                )

                // Draw Snake
                snake.forEachIndexed { index, segment ->
                    drawRoundRect(
                        color = if (index == 0) Color(0xFF66BB6A) else Color(0xFF4CAF50),
                        topLeft = Offset(segment.x * blockWidth + 2, segment.y * blockHeight + 2),
                        size = androidx.compose.ui.geometry.Size(blockWidth - 4, blockHeight - 4),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // On-Screen Keypad for Direction control
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Up Arrow
            IconButton(
                onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Left Arrow
                IconButton(
                    onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Spacer(modifier = Modifier.width(48.dp))

                // Right Arrow
                IconButton(
                    onClick = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            // Down Arrow
            IconButton(
                onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
