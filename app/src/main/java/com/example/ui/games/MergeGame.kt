package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MergeGame(
    onScoreUpdated: (Int) -> Unit,
    onGameOver: (Int) -> Unit
) {
    val context = LocalContext.current
    var grid by remember { mutableStateOf(List(16) { 0 }) }
    var score by remember { mutableStateOf(0) }

    // Spawn first tiles
    LaunchedEffect(Unit) {
        if (grid.all { it == 0 }) {
            var temp = grid.toMutableList()
            // Spawn two random tiles
            val first = Random.nextInt(16)
            var second = Random.nextInt(16)
            while (second == first) { second = Random.nextInt(16) }
            temp[first] = if (Random.nextFloat() < 0.9f) 2 else 4
            temp[second] = if (Random.nextFloat() < 0.9f) 2 else 4
            grid = temp
        }
    }

    fun spawnTile(currentGrid: List<Int>): List<Int> {
        val emptySpots = currentGrid.indices.filter { currentGrid[it] == 0 }
        if (emptySpots.isEmpty()) return currentGrid
        val spawnAt = emptySpots.random()
        val nextVal = if (Random.nextFloat() < 0.9f) 2 else 4
        val result = currentGrid.toMutableList()
        result[spawnAt] = nextVal
        return result
    }

    fun isGameOver(currentGrid: List<Int>): Boolean {
        if (currentGrid.contains(0)) return false
        // Check neighbors
        for (r in 0..3) {
            for (c in 0..3) {
                val idx = r * 4 + c
                val valSelf = currentGrid[idx]
                if (c < 3 && valSelf == currentGrid[idx + 1]) return false
                if (r < 3 && valSelf == currentGrid[idx + 4]) return false
            }
        }
        return true
    }

    fun makeMove(direction: String) {
        var tempGrid = grid.toMutableList()
        var moved = false

        // Translate grid according to direction, combine, slide back
        val columns = List(4) { mutableListOf<Int>() }
        for (r in 0..3) {
            for (c in 0..3) {
                val value = tempGrid[r * 4 + c]
                if (value != 0) {
                    columns[c].add(value)
                }
            }
        }

        fun mergeLine(line: List<Int>): Pair<List<Int>, Boolean> {
            val result = mutableListOf<Int>()
            var i = 0
            var changed = false
            while (i < line.size) {
                if (i + 1 < line.size && line[i] == line[i+1]) {
                    val mergedValue = line[i] * 2
                    result.add(mergedValue)
                    score += mergedValue
                    i += 2
                    changed = true
                } else {
                    result.add(line[i])
                    i++
                }
            }
            while (result.size < 4) {
                result.add(0)
            }
            return Pair(result, changed)
        }

        val processedLines = mutableListOf<List<Int>>()
        when (direction) {
            "LEFT" -> {
                for (r in 0..3) {
                    val rowVals = (0..3).map { tempGrid[r * 4 + it] }.filter { it != 0 }
                    val (merged, changed) = mergeLine(rowVals)
                    processedLines.add(merged)
                    if (merged != (0..3).map { tempGrid[r * 4 + it] }) moved = true
                }
                if (moved) {
                    for (r in 0..3) {
                        for (c in 0..3) {
                            tempGrid[r * 4 + c] = processedLines[r][c]
                        }
                    }
                }
            }
            "RIGHT" -> {
                for (r in 0..3) {
                    val rowVals = (0..3).map { tempGrid[r * 4 + it] }.filter { it != 0 }.reversed()
                    val (merged, changed) = mergeLine(rowVals)
                    val reversedMerged = merged.reversed()
                    processedLines.add(reversedMerged)
                    if (reversedMerged != (0..3).map { tempGrid[r * 4 + it] }) moved = true
                }
                if (moved) {
                    for (r in 0..3) {
                        for (c in 0..3) {
                            tempGrid[r * 4 + c] = processedLines[r][c]
                        }
                    }
                }
            }
            "UP" -> {
                for (c in 0..3) {
                    val colVals = (0..3).map { tempGrid[it * 4 + c] }.filter { it != 0 }
                    val (merged, changed) = mergeLine(colVals)
                    processedLines.add(merged)
                    if (merged != (0..3).map { tempGrid[it * 4 + c] }) moved = true
                }
                if (moved) {
                    for (c in 0..3) {
                        for (r in 0..3) {
                            tempGrid[r * 4 + c] = processedLines[c][r]
                        }
                    }
                }
            }
            "DOWN" -> {
                for (c in 0..3) {
                    val colVals = (0..3).map { tempGrid[it * 4 + c] }.filter { it != 0 }.reversed()
                    val (merged, changed) = mergeLine(colVals)
                    val reversedMerged = merged.reversed()
                    processedLines.add(reversedMerged)
                    if (reversedMerged != (0..3).map { tempGrid[it * 4 + c] }) moved = true
                }
                if (moved) {
                    for (c in 0..3) {
                        for (r in 0..3) {
                            tempGrid[r * 4 + c] = processedLines[c][r]
                        }
                    }
                }
            }
        }

        if (moved) {
            val withNewTile = spawnTile(tempGrid)
            grid = withNewTile
            onScoreUpdated(score)
            GameSoundManager.playPop()
            GameSoundManager.triggerHaptic(context)

            if (isGameOver(withNewTile)) {
                GameSoundManager.playGameOver()
                onGameOver(score)
            }
        }
    }

    var dragAccumulatorX by remember { mutableStateOf(0f) }
    var dragAccumulatorY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Score: $score",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 4x4 Grid Board with Swipe detection
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            dragAccumulatorX += dragAmount.x
                            dragAccumulatorY += dragAmount.y
                        },
                        onDragEnd = {
                            val threshold = 100f
                            if (Math.abs(dragAccumulatorX) > Math.abs(dragAccumulatorY)) {
                                if (dragAccumulatorX > threshold) makeMove("RIGHT")
                                else if (dragAccumulatorX < -threshold) makeMove("LEFT")
                            } else {
                                if (dragAccumulatorY > threshold) makeMove("DOWN")
                                else if (dragAccumulatorY < -threshold) makeMove("UP")
                            }
                            dragAccumulatorX = 0f
                            dragAccumulatorY = 0f
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (r in 0..3) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (c in 0..3) {
                            val valNom = grid[r * 4 + c]
                            val tileColor = when (valNom) {
                                2 -> Color(0xFFEEE4DA)
                                4 -> Color(0xFFEDE0C8)
                                8 -> Color(0xFFF2B179)
                                16 -> Color(0xFFF59563)
                                32 -> Color(0xFFF67C5F)
                                64 -> Color(0xFFF65E3B)
                                128 -> Color(0xFFEDCF72)
                                256 -> Color(0xFFEDCC61)
                                512 -> Color(0xFFEDC850)
                                1024 -> Color(0xFFEDC53F)
                                2048 -> Color(0xFFEDC22E)
                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            }
                            val textColor = if (valNom <= 4) Color.DarkGray else Color.White

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(tileColor)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (valNom > 0) {
                                    Text(
                                        text = valNom.toString(),
                                        fontSize = if (valNom >= 100) 18.sp else 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Arrow Key Controls for accessibility / comfort
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { makeMove("LEFT") },
                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Move Left")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { makeMove("UP") },
                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                }
                Spacer(modifier = Modifier.height(24.dp))
                IconButton(
                    onClick = { makeMove("DOWN") },
                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = { makeMove("RIGHT") },
                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Move Right")
            }
        }
    }
}
