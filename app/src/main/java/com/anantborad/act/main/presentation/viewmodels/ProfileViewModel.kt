package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anantborad.act.main.domain.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val userName: String = "Alex",
    val dailyGoal: String = "3 sessions",
    val isEditingName: Boolean = false,
    val isEditingGoal: Boolean = false
)

class ProfileViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val name = repository.getUserName()
        val goal = repository.getDailyGoal()
        _uiState.value = ProfileState(userName = name, dailyGoal = goal)
    }

    fun updateName(newName: String) {
        if (newName.isNotBlank()) {
            repository.setUserName(newName)
            _uiState.value = _uiState.value.copy(userName = newName, isEditingName = false)
        }
    }

    fun updateDailyGoal(newGoal: String) {
        repository.setDailyGoal(newGoal)
        _uiState.value = _uiState.value.copy(dailyGoal = newGoal, isEditingGoal = false)
    }

    fun clearHistory(onCleared: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearUserData()
            loadProfile()
            onCleared()
        }
    }
}
