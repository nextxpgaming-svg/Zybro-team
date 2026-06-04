package com.example.data.repository

import com.example.data.dao.GameDao
import com.example.data.model.UserProfile
import com.example.data.model.GameStats
import com.example.data.model.Achievement
import com.example.data.model.LocalLeaderboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class GameRepository(private val gameDao: GameDao) {

    val profileFlow: Flow<UserProfile?> = gameDao.getProfileFlow()
    val allGameStatsFlow: Flow<List<GameStats>> = gameDao.getAllGameStatsFlow()
    val allAchievementsFlow: Flow<List<Achievement>> = gameDao.getAllAchievementsFlow()

    fun getStatsForGame(gameId: String): Flow<GameStats?> = gameDao.getGameStatsFlow(gameId)
    fun getLeaderboard(gameId: String): Flow<List<LocalLeaderboard>> = gameDao.getLeaderboardFlow(gameId)

    suspend fun initDefaultData() {
        // Init profile
        val existingProfile = gameDao.getProfileDirect()
        if (existingProfile == null) {
            gameDao.updateProfile(UserProfile())
        }

        // Init achievements
        val achievements = listOf(
            Achievement("first_blood", "Game On!", "Play your very first mini-game", "sports_esports", 50, 20),
            Achievement("xp_grinder", "XP Gatherer", "Reach Level 2 in your Vault profile", "military_tech", 100, 50),
            Achievement("coin_hunter", "Coin Hoarder", "Accumulate more than 250 coins in your wallet", "savings", 100, 50),
            Achievement("enthusiast", "Super Fan", "Favorite at least 3 high-quality mini-games", "grade", 75, 30),
            Achievement("score_master", "Score Master", "Set a high score of 100+ on any single game", "emoji_events", 150, 75),
            Achievement("grandmaster", "Vault Legend", "Reach profile Level 5 and conquer challenges", "workspace_premium", 300, 150),
            Achievement("arcade_king", "Arcade Legend", "Play Arcade mini-games 5 or more times", "videogame_asset", 100, 40),
            Achievement("brain_burst", "Big Brain", "Play Brain or Puzzle categories 5 or more times", "psychology", 100, 40)
        )
        gameDao.insertAchievements(achievements)
    }

    suspend fun awardXpAndCoins(xpAmount: Int, coinAmount: Int) {
        val currentProfile = gameDao.getProfileDirect() ?: UserProfile()
        var newXp = currentProfile.xp + xpAmount
        var newLevel = currentProfile.level
        var newCoins = currentProfile.coins + coinAmount

        // 100 XP * Level required to level up
        var xpRequired = getXpForNextLevel(newLevel)
        while (newXp >= xpRequired) {
            newXp -= xpRequired
            newLevel++
            newCoins += newLevel * 50 // Bonus coins per level
            xpRequired = getXpForNextLevel(newLevel)
        }

        val updatedProfile = currentProfile.copy(
            level = newLevel,
            xp = newXp,
            coins = newCoins
        )
        gameDao.updateProfile(updatedProfile)

        // Check level achievement
        if (newLevel >= 2) {
            unlockAchievementDirect("xp_grinder")
        }
        if (newLevel >= 5) {
            unlockAchievementDirect("grandmaster")
        }
        if (newCoins >= 250) {
            unlockAchievementDirect("coin_hunter")
        }
    }

    fun getXpForNextLevel(level: Int): Int {
        return level * 100
    }

    suspend fun recordGamePlayed(gameId: String, score: Int, category: String) {
        // Update stats
        val currentStats = gameDao.getGameStatsDirect(gameId) ?: GameStats(gameId)
        val newCount = currentStats.playedCount + 1
        val isNewHigh = score > currentStats.highScore
        val newHighScore = if (isNewHigh) score else currentStats.highScore

        val updatedStats = currentStats.copy(
            playedCount = newCount,
            highScore = newHighScore,
            lastPlayedTime = System.currentTimeMillis()
        )
        gameDao.insertGameStats(updatedStats)

        // Update overall profile counts
        val profile = gameDao.getProfileDirect() ?: UserProfile()
        
        // Simple daily streak calculator (local)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val yesterday = today - 86400000L
        var streak = profile.currentStreak
        if (profile.lastPlayDate == yesterday) {
            streak++
        } else if (profile.lastPlayDate != today) {
            streak = 1
        }

        gameDao.updateProfile(
            profile.copy(
                totalGamesPlayed = profile.totalGamesPlayed + 1,
                lastPlayDate = today,
                currentStreak = streak
            )
        )

        // Add to leaderboards if score > 0
        if (score > 0) {
            gameDao.insertLeaderboardEntry(
                LocalLeaderboard(
                    gameId = gameId,
                    playerName = "You",
                    score = score
                )
            )
        }

        // Award base play rewards: 20 XP and 10 Coins
        // For score, award bonus of: (score / 10) coins and same for XP
        val bonusCoins = (score / 10).coerceAtMost(50)
        val bonusXp = (score / 5).coerceAtMost(100)
        awardXpAndCoins(20 + bonusXp, 10 + bonusCoins)

        // Trigger / Evaluate achievements
        unlockAchievementDirect("first_blood")

        if (newHighScore >= 100) {
            unlockAchievementDirect("score_master")
        }

        // Category achievements checks
        if (category == "Arcade Games" || category == "Reflex Games") {
            val allStats = gameDao.getAllGameStatsFlow().firstOrNull() ?: emptyList()
            // arcade counts
            val sumPlayed = allStats.filter { stats ->
                val g = GAMES_LIST.find { it.id == stats.gameId }
                g != null && (g.category == "Arcade Games" || g.category == "Reflex Games")
            }.sumOf { it.playedCount }
            if (sumPlayed >= 5) {
                unlockAchievementDirect("arcade_king")
            }
        }

        if (category == "Puzzle Games" || category == "Brain Games") {
            val allStats = gameDao.getAllGameStatsFlow().firstOrNull() ?: emptyList()
            val sumPlayed = allStats.filter { stats ->
                val g = GAMES_LIST.find { it.id == stats.gameId }
                g != null && (g.category == "Puzzle Games" || g.category == "Brain Games")
            }.sumOf { it.playedCount }
            if (sumPlayed >= 5) {
                unlockAchievementDirect("brain_burst")
            }
        }
    }

    suspend fun toggleFavorite(gameId: String) {
        val stats = gameDao.getGameStatsDirect(gameId) ?: GameStats(gameId)
        val newFav = !stats.isFavorite
        gameDao.insertGameStats(stats.copy(isFavorite = newFav))

        // Re-evaluate favorite achievement
        val allStats = gameDao.getAllGameStatsFlow().firstOrNull() ?: emptyList()
        val favCount = allStats.count { it.isFavorite }
        if (favCount >= 3) {
            unlockAchievementDirect("enthusiast")
        }
    }

    private suspend fun unlockAchievementDirect(id: String) {
        val achievements = gameDao.getAllAchievementsFlow().firstOrNull() ?: emptyList()
        val ach = achievements.find { it.id == id }
        if (ach != null && !ach.isUnlocked) {
            gameDao.unlockAchievement(id, System.currentTimeMillis())
            // Award rewards that come with achievement
            awardXpAndCoins(ach.xpReward, ach.coinReward)
        }
    }

    suspend fun resetAllProgress() {
        val achievements = gameDao.getAllAchievementsFlow().firstOrNull() ?: emptyList()
        for (a in achievements) {
            gameDao.insertAchievements(listOf(a.copy(isUnlocked = false, unlockedAt = 0L)))
        }
        gameDao.updateProfile(UserProfile())
        val allStats = gameDao.getAllGameStatsFlow().firstOrNull() ?: emptyList()
        for (st in allStats) {
            gameDao.insertGameStats(GameStats(st.gameId))
        }
    }
}
