package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.PracticeQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReviewState(
    val questions: List<PracticeQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0
) {
    val currentQuestion: PracticeQuestion?
        get() = questions.getOrNull(currentQuestionIndex)
}

class ReviewViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()

    init {
        loadLastSession()
    }

    private fun loadLastSession() {
        val questions = repository.getLastSessionQuestions()
        _uiState.value = _uiState.value.copy(questions = questions)
    }

    fun nextQuestion() {
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size - 1) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = _uiState.value.currentQuestionIndex + 1
            )
        }
    }

    fun previousQuestion() {
        if (_uiState.value.currentQuestionIndex > 0) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = _uiState.value.currentQuestionIndex - 1
            )
        }
    }
}
