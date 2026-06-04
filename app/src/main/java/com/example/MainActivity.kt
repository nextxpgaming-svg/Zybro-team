package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.MiniGame
import com.example.ui.games.GameContainer
import com.example.ui.games.GameSoundManager
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel

// Navigation targets
sealed interface Screen {
    object Home : Screen
    object Favorites : Screen
    object Stats : Screen
    object Achievements : Screen
    object Profile : Screen
    object Settings : Screen
    object About : Screen
    data class PlayGame(val game: MiniGame) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val gameViewModel: GameViewModel = viewModel()
            val isDarkTheme by gameViewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainAppNavigator(gameViewModel)
            }
        }
    }
}

@Composable
fun MainAppNavigator(viewModel: GameViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    // Floating or fixed bottom navigation bar based on active screen
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen !is Screen.PlayGame) {
                // Display M3 Standard Navigation bar
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Home,
                        label = { Text("Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Games Vault") },
                        colors = navItemColors,
                        onClick = {
                            GameSoundManager.playClick()
                            GameSoundManager.triggerHaptic(context)
                            currentScreen = Screen.Home
                        }
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Favorites,
                        label = { Text("Favorites", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                        colors = navItemColors,
                        onClick = {
                            GameSoundManager.playClick()
                            GameSoundManager.triggerHaptic(context)
                            currentScreen = Screen.Favorites
                        }
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Stats,
                        label = { Text("Stats", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Analytics") },
                        colors = navItemColors,
                        onClick = {
                            GameSoundManager.playClick()
                            GameSoundManager.triggerHaptic(context)
                            currentScreen = Screen.Stats
                        }
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Achievements,
                        label = { Text("Medals", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements") },
                        colors = navItemColors,
                        onClick = {
                            GameSoundManager.playClick()
                            GameSoundManager.triggerHaptic(context)
                            currentScreen = Screen.Achievements
                        }
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Profile || currentScreen is Screen.Settings || currentScreen is Screen.About,
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        colors = navItemColors,
                        onClick = {
                            GameSoundManager.playClick()
                            GameSoundManager.triggerHaptic(context)
                            currentScreen = Screen.Profile
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen transition animation
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "screen_movement"
            ) { targetScreen ->
                when (targetScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onLaunchGame = { miniGame ->
                                currentScreen = Screen.PlayGame(miniGame)
                            }
                        )
                    }

                    is Screen.Favorites -> {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onLaunchGame = { miniGame ->
                                currentScreen = Screen.PlayGame(miniGame)
                            }
                        )
                    }

                    is Screen.Stats -> {
                        StatsScreen(viewModel = viewModel)
                    }

                    is Screen.Achievements -> {
                        AchievementsScreen(viewModel = viewModel)
                    }

                    is Screen.Profile -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Embed a header switch targeting Settings and About page inside Profile tab
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { currentScreen = Screen.Profile },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.Profile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.Profile) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Profile")
                                }

                                Button(
                                    onClick = { currentScreen = Screen.Settings },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.Settings) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.Settings) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Settings")
                                }

                                Button(
                                    onClick = { currentScreen = Screen.About },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.About) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.About) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("About")
                                }
                            }
                            ProfileScreen(viewModel = viewModel)
                        }
                    }

                    is Screen.Settings -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { currentScreen = Screen.Profile },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.Profile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.Profile) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Profile")
                                }

                                Button(
                                    onClick = { currentScreen = Screen.Settings },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.Settings) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.Settings) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Settings")
                                }

                                Button(
                                    onClick = { currentScreen = Screen.About },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.About) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.About) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("About")
                                }
                            }
                            SettingsScreen(viewModel = viewModel)
                        }
                    }

                    is Screen.About -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { currentScreen = Screen.Profile },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.Profile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.Profile) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Profile")
                                }

                                Button(
                                    onClick = { currentScreen = Screen.Settings },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.Settings) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.Settings) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Settings")
                                }

                                Button(
                                    onClick = { currentScreen = Screen.About },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen is Screen.About) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentScreen is Screen.About) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("About")
                                }
                            }
                            AboutScreen()
                        }
                    }

                    is Screen.PlayGame -> {
                        GameContainer(
                            game = targetScreen.game,
                            viewModel = viewModel,
                            onBackToHome = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                }
            }
        }
    }
}
