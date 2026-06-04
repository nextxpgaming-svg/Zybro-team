package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.GameStats
import com.example.data.model.Achievement
import com.example.data.repository.GAMES_LIST
import com.example.data.repository.MiniGame
import com.example.ui.games.GameSoundManager
import com.example.ui.viewmodel.GameViewModel

// ------------------- 1. PROFILE SCREEN -------------------
@Composable
fun ProfileScreen(viewModel: GameViewModel) {
    val profile by viewModel.profile.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val allStats by viewModel.allGameStats.collectAsState()

    val unlockedCount = achievements.count { it.isUnlocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // Profile Card Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        item {
            Text(
                text = "GAME VAULTER",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Level ${profile.level} Explorer",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        // XP Level Progress Bar
        item {
            val xpReq = viewModel.repository.getXpForNextLevel(profile.level)
            val progress = (profile.xp.toFloat() / xpReq.toFloat()).coerceIn(0f, 1f)

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Profile XP Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${profile.xp} / ${xpReq} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }

        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = "Streak", tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text("${profile.currentStreak} Days", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Achievements Stats count
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Medals", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("MEDALS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text("$unlockedCount / ${achievements.size}", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Wallet
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("COINS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        Text(profile.coins.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Total Plays
        item {
            ListItem(
                headlineContent = { Text("Total Plays Counter", fontWeight = FontWeight.Bold) },
                supportingContent = { Text("Accumulated across all 100+ offline mini-games") },
                trailingContent = { Text("${profile.totalGamesPlayed} Games", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).padding(horizontal = 12.dp)
            )
        }
    }
}

// ------------------- 2. FAVORITES SCREEN -------------------
@Composable
fun FavoritesScreen(
    viewModel: GameViewModel,
    onLaunchGame: (MiniGame) -> Unit
) {
    val allStats by viewModel.allGameStats.collectAsState()

    // Filter dynamic favorites
    val favoriteGames = remember(allStats) {
        allStats.filter { it.isFavorite }
            .mapNotNull { stat -> GAMES_LIST.find { it.id == stat.gameId } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "⚡ YOUR FAVORITES",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Easily access your hand-picked games anytime offline",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteGames.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "No Favorites",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your Favorites Vault is empty!",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tap the heart icon when launching any mini-game to save here.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(favoriteGames) { game ->
                    val stats = allStats.find { it.gameId == game.id }
                    val currentHigh = stats?.highScore ?: 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onLaunchGame(game) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                val vector = getIconVector(game.icon)
                                Icon(vector, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(game.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(game.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("HIGH SCORE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(currentHigh.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            IconButton(onClick = { viewModel.toggleFavorite(game.id) }) {
                                Icon(Icons.Default.Favorite, contentDescription = "Remove Favorite", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- 3. STATISTICS SCREEN -------------------
@Composable
fun StatsScreen(viewModel: GameViewModel) {
    val allStats by viewModel.allGameStats.collectAsState()
    val profile by viewModel.profile.collectAsState()

    val playedGamesStat = allStats.filter { it.playedCount > 0 }
    val highestScore = playedGamesStat.maxOfOrNull { it.highScore } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "📊 VAULT ANALYTICS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Review your offline gameplay and high score records",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // General Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("PERFORMANCE INSIGHTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Offline Games Played", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                Text(profile.totalGamesPlayed.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Dynamic High Score", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                Text(highestScore.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }

            // High Scores Leaderboard List (Local Leaderboard achievements)
            item {
                Text(
                    text = "🏆 TOP MINI-GAME RECORDS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (playedGamesStat.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No scores recorded! Start playing games to populate statistics.",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(playedGamesStat.sortedByDescending { it.highScore }.take(10)) { stat ->
                    val game = GAMES_LIST.find { it.id == stat.gameId }
                    if (game != null) {
                        ListItem(
                            headlineContent = { Text(game.title, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Played ${stat.playedCount} times | ${game.category}") },
                            trailingContent = { Text(stat.highScore.toString(), fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        )
                    }
                }
            }
        }
    }
}

// ------------------- 4. ACHIEVEMENTS SCREEN -------------------
@Composable
fun AchievementsScreen(viewModel: GameViewModel) {
    val achievements by viewModel.achievements.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "🏆 MEDALS & ACHIEVEMENTS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Unlock milestones in the gaming vault to scoop big coins",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(achievements) { ach ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (ach.isUnlocked) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.height(160.dp),
                    border = if (ach.isUnlocked) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (ach.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconVector(ach.iconName),
                                    contentDescription = null,
                                    tint = if (ach.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (ach.isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("UNLOCKED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = ach.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = ach.description,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("+${ach.xpReward} XP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("+${ach.coinReward} Coins", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }
                    }
                }
            }
        }
    }
}

// ------------------- 5. SETTINGS SCREEN -------------------
@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val context = LocalContext.current
    val isMusic by viewModel.isMusicEnabled.collectAsState()
    val isSounds by viewModel.isSoundEnabled.collectAsState()
    val isVibes by viewModel.isVibrationEnabled.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()

    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "⚙️ SETTINGS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column {
                // Dark Theme toggle
                ListItem(
                    headlineContent = { Text("Dark Visual Mode", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Default recommended high-contrast theme") },
                    trailingContent = {
                        Switch(checked = isDark, onCheckedChange = {
                            viewModel.toggleTheme()
                            GameSoundManager.playClick()
                        })
                    }
                )
                Divider()

                // Background music toggle
                ListItem(
                    headlineContent = { Text("Offline Background Music", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Play ambient background tracks in play hubs") },
                    trailingContent = {
                        Switch(checked = isMusic, onCheckedChange = {
                            viewModel.toggleMusic()
                            GameSoundManager.playClick()
                        })
                    }
                )
                Divider()

                // Sound Effects toggle
                ListItem(
                    headlineContent = { Text("8-Bit Retro Sound Effects", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Enable synthesizer gameplay sounds") },
                    trailingContent = {
                        Switch(checked = isSounds, onCheckedChange = {
                            viewModel.toggleSounds()
                            GameSoundManager.playClick()
                        })
                    }
                )
                Divider()

                // Vibrations toggle
                ListItem(
                    headlineContent = { Text("Haptic Vibration Feedback", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Feel physical vibrations on popping bubbles/scoring") },
                    trailingContent = {
                        Switch(checked = isVibes, onCheckedChange = {
                            viewModel.toggleVibration()
                            GameSoundManager.playClick()
                        })
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Reset Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
        ) {
            ListItem(
                headlineContent = { Text("Reset Progress Data", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                supportingContent = { Text("Irreversibly wipe high scores, favorites and profile stats") },
                trailingContent = {
                    Button(
                        onClick = { showResetConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Wipe Data")
                    }
                }
            )
        }

        if (showResetConfirm) {
            AlertDialog(
                onDismissRequest = { showResetConfirm = false },
                title = { Text("Are you absolutely sure?") },
                text = { Text("Wiping data will clear all achievements, level progress, coins balance, and local records. This operation cannot be undone.") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            viewModel.resetAllProgress()
                            showResetConfirm = false
                            GameSoundManager.triggerStrongHaptic(context)
                        }
                    ) {
                        Text("YES, WIPE EVERYTHING")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirm = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}

// ------------------- 6. ABOUT SCREEN (MANDATORY BRANDING) -------------------
@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "GameVault 100+",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Version 1.0.0 Premium Edition",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.width(180.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DEVELOPED BY ZYBRO STUDIOS",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Developed by Zybro Studios",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About GameVault 100+",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "A complete, unified gaming suite designed specifically for offline playability. No subscriptions, logins, or continuous network connections required.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Equipped with custom haptic integrations, sound effect synthesizers, high score persistent tables, streak progress, and dynamic profile levels.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Created with ❤️ by Zybro Studios",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold
        )
    }
}
