package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TicTacToeGame(
    onScoreUpdated: (Int) -> Unit,
    onGameOver: (Int) -> Unit
) {
    val context = LocalContext.current
    var board by remember { mutableStateOf(List(9) { "" }) }
    var isUserTurn by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }

    fun checkWinner(b: List<String>): String {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cols
            listOf(0, 4, 8), listOf(2, 4, 6) // diags
        )
        for (line in lines) {
            if (b[line[0]].isNotEmpty() && b[line[0]] == b[line[1]] && b[line[0]] == b[line[2]]) {
                return b[line[0]]
            }
        }
        if (b.all { it.isNotEmpty() }) {
            return "Draw"
        }
        return ""
    }

    fun makeAiMove() {
        val emptyIndices = board.indices.filter { board[it].isEmpty() }
        if (emptyIndices.isEmpty()) return

        // 1. Check if AI can win in this turn
        for (idx in emptyIndices) {
            val testBoard = board.toMutableList()
            testBoard[idx] = "O"
            if (checkWinner(testBoard) == "O") {
                board = testBoard
                isUserTurn = true
                return
            }
        }

        // 2. Check if player would win in next turn, defend!
        for (idx in emptyIndices) {
            val testBoard = board.toMutableList()
            testBoard[idx] = "X"
            if (checkWinner(testBoard) == "X") {
                val newBoard = board.toMutableList()
                newBoard[idx] = "O"
                board = newBoard
                isUserTurn = true
                return
            }
        }

        // 3. Make random move
        val aiMoveIndex = emptyIndices.random()
        val newBoard = board.toMutableList()
        newBoard[aiMoveIndex] = "O"
        board = newBoard
        isUserTurn = true
    }

    LaunchedEffect(board, isUserTurn) {
        val w = checkWinner(board)
        if (w.isNotEmpty()) {
            winner = w
            if (w == "X") {
                GameSoundManager.playSuccess()
                score += 100
                onScoreUpdated(score)
                onGameOver(score)
            } else if (w == "O") {
                GameSoundManager.playGameOver()
                onGameOver(score)
            } else {
                GameSoundManager.playBeep(440f, 150)
                score += 30
                onScoreUpdated(score)
                onGameOver(score)
            }
        } else if (!isUserTurn) {
            kotlinx.coroutines.delay(500)
            makeAiMove()
        }
    }

    fun onCellClick(index: Int) {
        if (!isUserTurn || board[index].isNotEmpty() || winner.isNotEmpty()) return
        GameSoundManager.playClick()
        GameSoundManager.triggerHaptic(context)

        val newBoard = board.toMutableList()
        newBoard[index] = "X"
        board = newBoard
        isUserTurn = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isUserTurn) "Your Turn (X)" else "AI Thinking (O)...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 3x3 Grid of TicTacToe
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (r in 0..2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (c in 0..2) {
                        val index = r * 3 + c
                        val label = board[index]
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onCellClick(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (label == "X") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
