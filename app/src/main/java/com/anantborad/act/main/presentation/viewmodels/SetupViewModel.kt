package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SetupState(
    val questionCount: Int = 10,
    val difficulty: String = "Mixed",
    val selectedSkill: String? = null
)

class SetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SetupState())
    val uiState: StateFlow<SetupState> = _uiState.asStateFlow()

    fun setQuestionCount(count: Int) {
        _uiState.value = _uiState.value.copy(questionCount = count)
    }

    fun setDifficulty(difficulty: String) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun setSelectedSkill(skill: String?) {
        _uiState.value = _uiState.value.copy(selectedSkill = skill)
    }
}
