package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.PracticeQuestion
import com.anantborad.act.main.domain.model.PracticeSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SessionState(
    val questions: List<PracticeQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionId: String? = null,
    val answers: Map<String, String> = emptyMap(), // QuestionId -> OptionId
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFinished: Boolean = false,
    val startTimeSeconds: Long = 0,
    val timeElapsedSeconds: Long = 0,
    val score: Int = 0
) {
    val currentQuestion: PracticeQuestion?
        get() = questions.getOrNull(currentQuestionIndex)
    
    val progress: Float
        get() = if (questions.isEmpty()) 0f else (currentQuestionIndex + 1).toFloat() / questions.size
}

class SessionViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionState())
    val uiState: StateFlow<SessionState> = _uiState.asStateFlow()

    fun startSession(subject: PracticeSubject, limit: Int = 5) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                startTimeSeconds = System.currentTimeMillis() / 1000
            )
            try {
                val questions = repository.getPracticeQuestions(subject, limit)
                _uiState.value = _uiState.value.copy(
                    questions = questions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load questions",
                    isLoading = false
                )
            }
        }
    }

    fun onOptionSelected(optionId: String) {
        _uiState.value = _uiState.value.copy(selectedOptionId = optionId)
    }

    fun onNextClick() {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        val selectedOption = currentState.selectedOptionId ?: return

        val newAnswers = currentState.answers.toMutableMap().apply {
            put(currentQuestion.id, selectedOption)
        }

        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentState.currentQuestionIndex + 1,
                selectedOptionId = null,
                answers = newAnswers
            )
        } else {
            val finalScore = currentState.questions.count { q ->
                newAnswers[q.id] == q.correctOptionId
            }
            val endTime = System.currentTimeMillis() / 1000
            _uiState.value = currentState.copy(
                isFinished = true,
                answers = newAnswers,
                score = finalScore,
                timeElapsedSeconds = endTime - currentState.startTimeSeconds
            )
        }
    }
}
