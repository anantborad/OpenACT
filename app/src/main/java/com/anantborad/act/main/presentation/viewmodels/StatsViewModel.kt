package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anantborad.act.main.data.local.UserSessionEntity
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StatsTimeframe(val label: String, val sessionLimit: Int?) {
    LAST_5("Last 5 Sessions", 5),
    LAST_10("Last 10 Sessions", 10),
    ALL_TIME("All Time", null)
}

data class StatsState(
    val userStats: UserStats? = null,
    val recentSessions: List<UserSessionEntity> = emptyList(),
    val goalActScore: Int = 32,
    val targetAccuracy: Int = 88,
    val selectedTimeframe: StatsTimeframe = StatsTimeframe.ALL_TIME,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StatsViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsState())
    val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun setTimeframe(timeframe: StatsTimeframe) {
        _uiState.value = _uiState.value.copy(selectedTimeframe = timeframe)
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val timeframe = _uiState.value.selectedTimeframe
                val stats = repository.getUserStats(timeframe.sessionLimit)
                val recent = repository.getRecentSessions(10)
                val goalScore = repository.getGoalActScore()
                _uiState.value = _uiState.value.copy(
                    userStats = stats,
                    recentSessions = recent,
                    goalActScore = goalScore,
                    targetAccuracy = stats.targetAccuracy,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load stats",
                    isLoading = false
                )
            }
        }
    }
}
