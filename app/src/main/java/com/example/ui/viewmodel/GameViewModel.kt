package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.GameDatabase
import com.example.data.model.UserProfile
import com.example.data.model.GameStats
import com.example.data.model.Achievement
import com.example.data.model.LocalLeaderboard
import com.example.data.repository.GameRepository
import com.example.data.repository.GAMES_LIST
import com.example.data.repository.MiniGame
import com.example.ui.games.GameSoundManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val database = GameDatabase.getDatabase(application)
    val repository = GameRepository(database.gameDao())

    // UI States
    val profile: StateFlow<UserProfile> = repository.profileFlow
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val allGameStats: StateFlow<List<GameStats>> = repository.allGameStatsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<Achievement>> = repository.allAchievementsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sounds / Vibration Prefs
    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(true)
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true) // Default to dark theme as GameVault styling
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Daily Challenge local tracking (random game per day)
    private val _dailyChallengeGame = MutableStateFlow<MiniGame?>(null)
    val dailyChallengeGame: StateFlow<MiniGame?> = _dailyChallengeGame.asStateFlow()

    private val _isDailyCompleted = MutableStateFlow(false)
    val isDailyCompleted: StateFlow<Boolean> = _isDailyCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initDefaultData()
            // Pick a daily challenge game
            _dailyChallengeGame.value = GAMES_LIST.random()
        }
    }

    fun toggleMusic() {
        _isMusicEnabled.value = !_isMusicEnabled.value
    }

    fun toggleSounds() {
        _isSoundEnabled.value = !_isSoundEnabled.value
        GameSoundManager.setSoundEnabled(_isSoundEnabled.value)
    }

    fun toggleVibration() {
        _isVibrationEnabled.value = !_isVibrationEnabled.value
        GameSoundManager.setVibrationEnabled(_isVibrationEnabled.value)
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun toggleFavorite(gameId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(gameId)
        }
    }

    fun recordGamePlayed(gameId: String, score: Int, category: String) {
        viewModelScope.launch {
            repository.recordGamePlayed(gameId, score, category)
            // If this is the daily challenge game, complete it!
            if (gameId == _dailyChallengeGame.value?.id && !_isDailyCompleted.value) {
                _isDailyCompleted.value = true
                repository.awardXpAndCoins(150, 75) // Bonus daily rewards!
            }
        }
    }

    fun getLeaderboardForGame(gameId: String): Flow<List<LocalLeaderboard>> {
        return repository.getLeaderboard(gameId)
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _isDailyCompleted.value = false
            _dailyChallengeGame.value = GAMES_LIST.random()
        }
    }
}
