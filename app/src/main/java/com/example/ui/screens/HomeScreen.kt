package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameStats
import com.example.data.repository.GAMES_LIST
import com.example.data.repository.MiniGame
import com.example.ui.games.GameSoundManager
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onLaunchGame: (MiniGame) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val allStats by viewModel.allGameStats.collectAsState()
    val dailyChallengeGame by viewModel.dailyChallengeGame.collectAsState()
    val isDailyCompleted by viewModel.isDailyCompleted.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Puzzle Games", "Brain Games", "Arcade Games", "Reflex Games", "Memory Games", "Strategy Games", "Physics Games", "Satisfying Games", "Number Games", "Logic Games")

    // Filter games list based on search and category
    val filteredGames = remember(searchQuery, selectedCategory) {
        GAMES_LIST.filter { game ->
            val matchesSearch = game.title.contains(searchQuery, ignoreCase = true) ||
                    game.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = (selectedCategory == "All") || (game.category == selectedCategory)
            matchesSearch && matchesCategory
        }
    }

    // Recently Played games
    val recentlyPlayed = remember(allStats) {
        allStats.filter { it.playedCount > 0 }
            .sortedByDescending { it.lastPlayedTime }
            .mapNotNull { stat -> GAMES_LIST.find { it.id == stat.gameId } }
            .take(6)
    }

    // Favorite games
    val favoriteGames = remember(allStats) {
        allStats.filter { it.isFavorite }
            .mapNotNull { stat -> GAMES_LIST.find { it.id == stat.gameId } }
    }

    // Popular mini games (highest plays or popular tags)
    val popularGames = remember {
        GAMES_LIST.filter { it.isPlayable }.take(6)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header (Vault banner & user wallets)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "GameVault Logo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "GameVault 100+",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Zybro Studios",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                // Wallets/Level info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Level Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "LVL ${profile.level}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // Coins Wallet
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFB74D).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = profile.coins.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }
        }

        // Home Scrolling Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Search 100+ offline games...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                singleLine = true
            )

            // Dynamic Category Row
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                selectedCategory = category
                                GameSoundManager.playClick()
                                GameSoundManager.triggerHaptic(context)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid or filtered dynamic list
            if (searchQuery.isNotEmpty() || selectedCategory != "All") {
                // Search list
                Text(
                    text = "SEARCH RESULTS (${filteredGames.size} GAMES)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (filteredGames.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, contentDescription = "No results", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No offline match found!", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredGames) { game ->
                            MiniGameGridCard(game, allStats, onLaunch = onLaunchGame)
                        }
                    }
                }
            } else {
                // Standard Home Panels (Categories list details)
                Box(modifier = Modifier.weight(1f)) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. Daily Challenge Banner
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            dailyChallengeGame?.let { daily ->
                                DailyChallengeBanner(
                                    game = daily,
                                    completed = isDailyCompleted,
                                    onPlay = { onLaunchGame(daily) }
                                )
                            }
                        }

                        // 2. Favorites List (Horizontal row encapsulated)
                        if (favoriteGames.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column {
                                    Text(
                                        text = "⚡ YOUR FAVORITES",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(favoriteGames) { game ->
                                            MiniGameScrollCard(game, allStats, onLaunch = onLaunchGame)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }

                        // 3. Recently Played
                        if (recentlyPlayed.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column {
                                    Text(
                                        text = "⏰ RECENTLY PLAYED",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(recentlyPlayed) { game ->
                                            MiniGameScrollCard(game, allStats, onLaunch = onLaunchGame)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }

                        // 4. Popular Games
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "🔥 POPULAR OFFLINE MINI-GAMES",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        items(popularGames) { game ->
                            MiniGameGridCard(game, allStats, onLaunch = onLaunchGame)
                        }

                        // 5. Remaining 100+ Games Library Catalog
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "🎮 THE 100+ VAULT CATALOG",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                            )
                        }

                        val remainingGames = GAMES_LIST.filter { !it.isPlayable }
                        items(remainingGames) { game ->
                            MiniGameGridCard(game, allStats, onLaunch = onLaunchGame)
                        }
                    }
                }
            }
        }
    }
}

// ------------------- SUB COMPONENT CARDS -------------------

@Composable
fun DailyChallengeBanner(
    game: MiniGame,
    completed: Boolean,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (completed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.tertiary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (completed) Icons.Default.Check else Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (completed) "DAILY CHALLENGE COMPLETED!" else "DAILY MINI-CHALLENGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (completed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
                )
                Text(
                    text = game.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (completed) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Play now & harvest bonus +150 XP & +75 Coins!",
                    fontSize = 12.sp,
                    color = if (completed) Color(0xFF388E3C) else MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (completed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (completed) "Replay" else "PLAY")
            }
        }
    }
}

@Composable
fun MiniGameScrollCard(
    game: MiniGame,
    allStats: List<GameStats>,
    onLaunch: (MiniGame) -> Unit
) {
    val context = LocalContext.current
    val stats = allStats.find { it.gameId == game.id }
    val bestScore = stats?.highScore ?: 0

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(115.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                GameSoundManager.playClick()
                GameSoundManager.triggerHaptic(context)
                onLaunch(game)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconVector(game.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (stats?.isFavorite == true) {
                    Icon(Icons.Default.Favorite, contentDescription = "Liked", tint = Color.Red, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = game.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Best Score: $bestScore",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MiniGameGridCard(
    game: MiniGame,
    allStats: List<GameStats>,
    onLaunch: (MiniGame) -> Unit
) {
    val context = LocalContext.current
    val stats = allStats.find { it.gameId == game.id }
    val bestScore = stats?.highScore ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                GameSoundManager.playClick()
                GameSoundManager.triggerHaptic(context)
                onLaunch(game)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = if (game.isPlayable) androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconVector(game.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (game.isPlayable) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "PLAYABLE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    if (stats?.isFavorite == true) {
                        Icon(Icons.Default.Favorite, contentDescription = "Liked", tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = game.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = game.category,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (bestScore > 0) "Best: $bestScore" else "Unplayed",
                    fontSize = 11.sp,
                    color = if (bestScore > 0) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Launch",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Map database strings to icons seamlessly
@Composable
fun getIconVector(name: String) = when (name) {
    "colorize" -> Icons.Default.Colorize
    "grid_on" -> Icons.Default.GridOn
    "category" -> Icons.Default.Category
    "search" -> Icons.Default.Search
    "hardware" -> Icons.Default.Hardware
    "hearing" -> Icons.Default.Hearing
    "contrast" -> Icons.Default.Contrast
    "deck" -> Icons.Default.Deck
    "hive" -> Icons.Default.Hive
    "swap_horiz" -> Icons.Default.SwapHoriz
    "dialpad" -> Icons.Default.Dialpad
    "calculate" -> Icons.Default.Calculate
    "help_outline" -> Icons.Default.HelpOutline
    "psychology" -> Icons.Default.Psychology
    "rotate_right" -> Icons.Default.RotateRight
    "shuffle" -> Icons.Default.Shuffle
    "trending_up" -> Icons.Default.TrendingUp
    "lightbulb" -> Icons.Default.Lightbulb
    "format_list_numbered" -> Icons.Default.FormatListNumbered
    "lock_open" -> Icons.Default.LockOpen
    "pest_control_rodent" -> Icons.Default.PestControlRodent
    "flight" -> Icons.Default.Flight
    "grid_view" -> Icons.Default.GridView
    "layers" -> Icons.Default.Layers
    "rocket_launch" -> Icons.Default.RocketLaunch
    "storm" -> Icons.Default.Storm
    "sports_motorsports" -> Icons.Default.SportsMotorsports
    "directions_car" -> Icons.Default.DirectionsCar
    "pets" -> Icons.Default.Pets
    "sports_tennis" -> Icons.Default.SportsTennis
    "timer" -> Icons.Default.Timer
    "touch_app" -> Icons.Default.TouchApp
    "apps" -> Icons.Default.Apps
    "bolt" -> Icons.Default.Bolt
    "audiotrack" -> Icons.Default.Audiotrack
    "adjust" -> Icons.Default.Adjust
    "gavel" -> Icons.Default.Gavel
    "swap_calls" -> Icons.Default.SwapCalls
    "download" -> Icons.Default.Download
    "palette" -> Icons.Default.Palette
    "extension" -> Icons.Default.Extension
    "graphic_eq" -> Icons.Default.GraphicEq
    "view_quilt" -> Icons.Default.ViewQuilt
    "password" -> Icons.Default.Password
    "dashboard_customize" -> Icons.Default.DashboardCustomize
    "face" -> Icons.Default.Face
    "volume_up" -> Icons.Default.VolumeUp
    "insights" -> Icons.Default.Insights
    "shopping_cart" -> Icons.Default.ShoppingCart
    "dynamic_feed" -> Icons.Default.DynamicFeed
    "close" -> Icons.Default.Close
    "dangerous" -> Icons.Default.Dangerous
    "radio_button_checked" -> Icons.Default.RadioButtonChecked
    "grid_goldenratio" -> Icons.Default.GridGoldenratio
    "emoji_people" -> Icons.Default.EmojiPeople
    "directions_boat" -> Icons.Default.DirectionsBoat
    "fort" -> Icons.Default.Fort
    "border_all" -> Icons.Default.BorderAll
    "casino" -> Icons.Default.Casino
    "blur_circular" -> Icons.Default.BlurCircular
    "wifi_tethering" -> Icons.Default.WifiTethering
    "scale" -> Icons.Default.Scale
    "accessibility_new" -> Icons.Default.AccessibilityNew
    "grain" -> Icons.Default.Grain
    "leak_add" -> Icons.Default.LeakAdd
    "air" -> Icons.Default.Air
    "toll" -> Icons.Default.Toll
    "water" -> Icons.Default.Water
    "explore" -> Icons.Default.Explore
    "blur_on" -> Icons.Default.BlurOn
    "waves" -> Icons.Default.Waves
    "grass" -> Icons.Default.Grass
    "carpenter" -> Icons.Default.Carpenter
    "stacked_bar_chart" -> Icons.Default.StackedBarChart
    "inventory" -> Icons.Default.Inventory
    "broken_image" -> Icons.Default.BrokenImage
    "unfold_more" -> Icons.Default.UnfoldMore
    "cleaning_services" -> Icons.Default.CleaningServices
    "wallpaper" -> Icons.Default.Wallpaper
    "apps_outage" -> Icons.Default.AppsOutage
    "join_full" -> Icons.Default.JoinFull
    "add_circle_outline" -> Icons.Default.AddCircleOutline
    "looks_one" -> Icons.Default.LooksOne
    "compare_arrows" -> Icons.Default.CompareArrows
    "plus_one" -> Icons.Default.PlusOne
    "timeline" -> Icons.Default.Timeline
    "border_inner" -> Icons.Default.BorderInner
    "arrow_downward" -> Icons.Default.ArrowDownward
    "electrical_services" -> Icons.Default.ElectricalServices
    "density_large" -> Icons.Default.DensityLarge
    "format_color_fill" -> Icons.Default.FormatColorFill
    "question_mark" -> Icons.Default.QuestionMark
    "crop_portrait" -> Icons.Default.CropPortrait
    "local_drink" -> Icons.Default.LocalDrink
    "checklist" -> Icons.Default.Checklist
    "toggle_on" -> Icons.Default.ToggleOn
    "filter_tilt_shift" -> Icons.Default.FilterTiltShift
    "local_post_office" -> Icons.Default.LocalPostOffice
    "filter_frames" -> Icons.Default.FilterFrames
    "square" -> Icons.Default.Square
    "my_location" -> Icons.Default.MyLocation
    "grid_3x3" -> Icons.Default.Grid3x3
    "gradient" -> Icons.Default.Gradient
    "code" -> Icons.Default.Code
    "blur_linear" -> Icons.Default.BlurLinear
    "sports_esports" -> Icons.Default.SportsEsports
    else -> Icons.Default.SportsEsports
}
