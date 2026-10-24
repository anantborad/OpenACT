package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ResultsState(
    val subject: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val timeSeconds: Long = 0,
    val accuracy: Int = 0,
    val xpGained: Int = 0
)

class ResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsState())
    val uiState: StateFlow<ResultsState> = _uiState.asStateFlow()

    fun setResults(subject: String, score: Int, total: Int, timeSeconds: Long) {
        val accuracy = if (total > 0) (score * 100) / total else 0
        val xp = score * 20
        _uiState.value = ResultsState(
            subject = subject,
            score = score,
            total = total,
            timeSeconds = timeSeconds,
            accuracy = accuracy,
            xpGained = xp
        )
    }
}
