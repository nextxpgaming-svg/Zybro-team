package com.example.data.dao

import androidx.room.*
import com.example.data.model.UserProfile
import com.example.data.model.GameStats
import com.example.data.model.Achievement
import com.example.data.model.LocalLeaderboard
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfile(profile: UserProfile)

    // Game Stats
    @Query("SELECT * FROM game_stats")
    fun getAllGameStatsFlow(): Flow<List<GameStats>>

    @Query("SELECT * FROM game_stats WHERE gameId = :gameId LIMIT 1")
    fun getGameStatsFlow(gameId: String): Flow<GameStats?>

    @Query("SELECT * FROM game_stats WHERE gameId = :gameId LIMIT 1")
    suspend fun getGameStatsDirect(gameId: String): GameStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameStats(stats: GameStats)

    @Query("UPDATE game_stats SET isFavorite = :isFavorite WHERE gameId = :gameId")
    suspend fun markFavorite(gameId: String, isFavorite: Boolean)

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAllAchievementsFlow(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long)

    // Leaderboards
    @Query("SELECT * FROM leaderboards WHERE gameId = :gameId ORDER BY score DESC LIMIT 10")
    fun getLeaderboardFlow(gameId: String): Flow<List<LocalLeaderboard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LocalLeaderboard)
}
