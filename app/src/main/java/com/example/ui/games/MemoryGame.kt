package com.example.ui.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
    val id: Int,
    val iconName: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryGame(
    onScoreUpdated: (Int) -> Unit,
    onGameOver: (Int) -> Unit
) {
    val context = LocalContext.current
    val systemIcons = listOf(
        "sports_esports" to Icons.Default.SportsEsports,
        "extension" to Icons.Default.Extension,
        "star" to Icons.Default.Star,
        "pets" to Icons.Default.Pets,
        "favorite" to Icons.Default.Favorite,
        "rocket" to Icons.Default.RocketLaunch,
        "bolt" to Icons.Default.Bolt,
        "music" to Icons.Default.MusicNote
    )

    var cards by remember {
        mutableStateOf(
            (systemIcons + systemIcons)
                .shuffled()
                .mapIndexed { idx, pair -> MemoryCard(idx, pair.first) }
        )
    }

    val selectedIndices = remember { mutableStateListOf<Int>() }
    var score by remember { mutableStateOf(0) }
    var moves by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    fun handleCardClick(index: Int) {
        if (selectedIndices.size >= 2 || cards[index].isFlipped || cards[index].isMatched) return

        GameSoundManager.playClick()
        GameSoundManager.triggerHaptic(context)

        val newCards = cards.toMutableList()
        newCards[index] = newCards[index].copy(isFlipped = true)
        cards = newCards

        selectedIndices.add(index)

        if (selectedIndices.size == 2) {
            moves++
            val firstIdx = selectedIndices[0]
            val secondIdx = selectedIndices[1]

            if (cards[firstIdx].iconName == cards[secondIdx].iconName) {
                // Matched!
                coroutineScope.launch {
                    delay(500)
                    val matchedList = cards.toMutableList()
                    matchedList[firstIdx] = matchedList[firstIdx].copy(isMatched = true)
                    matchedList[secondIdx] = matchedList[secondIdx].copy(isMatched = true)
                    cards = matchedList
                    score += 50
                    onScoreUpdated(score)
                    GameSoundManager.playSuccess()
                    selectedIndices.clear()

                    // Check win
                    if (cards.all { it.isMatched }) {
                        onGameOver(score + 100) // matches finished bonus
                    }
                }
            } else {
                // Not matching, flip back
                coroutineScope.launch {
                    delay(1000)
                    val unflipList = cards.toMutableList()
                    unflipList[firstIdx] = unflipList[firstIdx].copy(isFlipped = false)
                    unflipList[secondIdx] = unflipList[secondIdx].copy(isFlipped = false)
                    cards = unflipList
                    score = (score - 5).coerceAtLeast(0)
                    onScoreUpdated(score)
                    GameSoundManager.playBeep(250f, 150)
                    selectedIndices.clear()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Moves: $moves | Score: $score",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 4x4 Grid for Memory cards
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (row in 0..3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (col in 0..3) {
                        val index = row * 4 + col
                        if (index < cards.size) {
                            val card = cards[index]
                            val rotation by animateFloatAsState(
                                targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
                                label = "card_rotation"
                            )

                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .graphicsLayer {
                                        rotationY = rotation
                                        cameraDistance = 8 * density
                                    }
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (rotation > 90f) {
                                            if (card.isMatched) MaterialTheme.colorScheme.tertiaryContainer
                                            else MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.secondaryContainer
                                        }
                                    )
                                    .clickable { handleCardClick(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (rotation > 90f) {
                                    // Render icon when flipped
                                    val iconData = systemIcons.find { it.first == card.iconName }
                                    if (iconData != null) {
                                        Icon(
                                            imageVector = iconData.second,
                                            contentDescription = card.iconName,
                                            tint = if (card.isMatched) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .graphicsLayer { rotationY = 180f }
                                        )
                                    }
                                } else {
                                    // Question mark card back
                                    Text(
                                        text = "?",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
