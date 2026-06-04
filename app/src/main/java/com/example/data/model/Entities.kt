package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val currentStreak: Int = 0,
    val lastPlayDate: Long = 0L,
    val totalGamesPlayed: Int = 0
)

@Entity(tableName = "game_stats")
data class GameStats(
    @PrimaryKey val gameId: String,
    val playedCount: Int = 0,
    val highScore: Int = 0,
    val isFavorite: Boolean = false,
    val lastPlayedTime: Long = 0L
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val xpReward: Int = 50,
    val coinReward: Int = 20,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)

@Entity(tableName = "leaderboards")
data class LocalLeaderboard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameId: String,
    val playerName: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)
