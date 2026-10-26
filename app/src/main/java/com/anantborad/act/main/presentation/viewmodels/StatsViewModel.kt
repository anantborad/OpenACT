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

data class StatsState(
    val userStats: UserStats? = null,
    val recentSessions: List<UserSessionEntity> = emptyList(),
    val goalActScore: Int = 32,
    val targetAccuracy: Int = 88,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StatsViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsState())
    val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val stats = repository.getUserStats()
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
