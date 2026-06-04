package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.GAMES_LIST
import com.example.data.repository.MiniGame
import com.example.ui.viewmodel.GameViewModel
import kotlinx.coroutines.launch

enum class GameState { INSTRUCTIONS, PLAYING, PAUSED, GAME_OVER }

@Composable
fun GameContainer(
    game: MiniGame,
    viewModel: GameViewModel,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allStats by viewModel.allGameStats.collectAsState()
    val isMuted by viewModel.isSoundEnabled.collectAsState()

    var gameState by remember { mutableStateOf(GameState.INSTRUCTIONS) }
    var currentScore by remember { mutableStateOf(0) }

    val gameStat = allStats.find { it.gameId == game.id }
    val bestScore = gameStat?.highScore ?: 0

    fun changeState(nextState: GameState) {
        gameState = nextState
        GameSoundManager.triggerHaptic(context)
    }

    fun handleBackToHome() {
        GameSoundManager.playClick()
        onBackToHome()
    }

    fun restartGame() {
        currentScore = 0
        changeState(GameState.PLAYING)
        GameSoundManager.playBeep(600f, 150)
    }

    fun finishGame(finalScore: Int) {
        currentScore = finalScore
        // Update stats in DB
        viewModel.recordGamePlayed(game.id, finalScore, game.category)
        changeState(GameState.GAME_OVER)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        when (gameState) {
            GameState.INSTRUCTIONS -> {
                InstructionsScreen(
                    game = game,
                    bestScore = bestScore,
                    onStart = { changeState(GameState.PLAYING) },
                    onBack = { handleBackToHome() }
                )
            }

            GameState.PLAYING -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Game top HUD
                    GameHud(
                        game = game,
                        currentScore = currentScore,
                        bestScore = bestScore,
                        isMuted = !isMuted,
                        onPause = { changeState(GameState.PAUSED) },
                        onToggleMute = { viewModel.toggleSounds() },
                        onShowHelp = { changeState(GameState.INSTRUCTIONS) }
                    )

                    // Active game boards
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (game.isPlayable) {
                            when (game.id) {
                                "color_sort" -> PuzzleCategoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) },
                                    blockColor = Color(0xFF9C27B0)
                                )
                                "tic_tac_toe" -> TicTacToeGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "snake" -> SnakeGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "memory_match" -> MemoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "two_zero_four_eight" -> MergeGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "bubble_pop" -> SatisfyingCategoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "tap_challenge" -> ReflexCategoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "reaction_timer" -> ReflexCategoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "simon_says" -> MemoryCategoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                                "minesweeper" -> PuzzleCategoryGame(
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) },
                                    blockColor = Color(0xFF607D8B)
                                )
                                else -> ProceduralGameEngine(
                                    game = game,
                                    isMuted = !isMuted,
                                    onScoreUpdated = { currentScore = it },
                                    onGameOver = { finishGame(it) }
                                )
                            }
                        } else {
                            // Run the Thematic procedural puzzle creator for other 91 games!
                            ProceduralGameEngine(
                                game = game,
                                isMuted = !isMuted,
                                onScoreUpdated = { currentScore = it },
                                onGameOver = { finishGame(it) }
                            )
                        }
                    }
                }
            }

            GameState.PAUSED -> {
                PauseOverlay(
                    onResume = { changeState(GameState.PLAYING) },
                    onRestart = { restartGame() },
                    onHome = { handleBackToHome() }
                )
            }

            GameState.GAME_OVER -> {
                GameOverScreen(
                    game = game,
                    finalScore = currentScore,
                    bestScore = maxOf(bestScore, currentScore),
                    onPlayAgain = { restartGame() },
                    onNextGame = {
                        val currentIdx = GAMES_LIST.indexOfFirst { it.id == game.id }
                        val nextIdx = (currentIdx + 1) % GAMES_LIST.size
                        val nextGame = GAMES_LIST[nextIdx]
                        // Load next game instructions
                        currentScore = 0
                        gameState = GameState.INSTRUCTIONS
                    },
                    onHome = { handleBackToHome() }
                )
            }
        }
    }
}

// ------------------- 1. HUD COMPONENT -------------------
@Composable
fun GameHud(
    game: MiniGame,
    currentScore: Int,
    bestScore: Int,
    isMuted: Boolean,
    onPause: () -> Unit,
    onToggleMute: () -> Unit,
    onShowHelp: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPause) {
                    Icon(Icons.Default.PauseCircle, contentDescription = "Pause", modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = game.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Score Box
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = currentScore.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black)
                }

                HorizontalDivider(modifier = Modifier.height(28.dp).width(1.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "BEST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    Text(text = bestScore.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onShowHelp) {
                    Icon(Icons.Default.HelpOutline, contentDescription = "Instructions", modifier = Modifier.size(24.dp))
                }

                IconButton(onClick = onToggleMute) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute Toggle",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// ------------------- 2. INSTRUCTIONS COMPONENT -------------------
@Composable
fun InstructionsScreen(
    game: MiniGame,
    bestScore: Int,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = game.category,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "BEST VAULT SCORE: $bestScore",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "HOW TO PLAY",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("START GAME", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("BACK TO HOME", fontSize = 16.sp)
                }
            }
        }
    }
}

// ------------------- 3. PAUSE OVERLAY COMPONENT -------------------
@Composable
fun PauseOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "GAME PAUSED",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onResume,
                modifier = Modifier.width(200.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                Spacer(modifier = Modifier.width(8.dp))
                Text("RESUME")
            }

            Button(
                onClick = onRestart,
                modifier = Modifier.width(200.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Restart")
                Spacer(modifier = Modifier.width(8.dp))
                Text("RESTART")
            }

            Button(
                onClick = onHome,
                modifier = Modifier.width(200.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home")
                Spacer(modifier = Modifier.width(8.dp))
                Text("EXIT GAME")
            }
        }
    }
}

// ------------------- 4. END GAME SCREEN (MANDATORY REQUIREMENT) -------------------
@Composable
fun GameOverScreen(
    game: MiniGame,
    finalScore: Int,
    bestScore: Int,
    onPlayAgain: () -> Unit,
    onNextGame: () -> Unit,
    onHome: () -> Unit
) {
    val scale = remember { Animatable(0.2f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // If earned points are high, celebrate!
        if (finalScore > 0) {
            ConfettiEffect()
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "🎮 Thanks for Playing!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Created with ❤️ by Zybro Studios",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FINAL SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text(finalScore.toString(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BEST SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text(bestScore.toString(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Play Store style interactive buttons
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(0.85f).height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Again")
                Spacer(modifier = Modifier.width(8.dp))
                Text("PLAY AGAIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNextGame,
                modifier = Modifier.fillMaxWidth(0.85f).height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next Game")
                Spacer(modifier = Modifier.width(8.dp))
                Text("NEXT MINI-GAME", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(0.85f).height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home")
                Spacer(modifier = Modifier.width(8.dp))
                Text("GO TO GAMES VAULT")
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Thanks for Playing! – Zybro Studios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.82f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
