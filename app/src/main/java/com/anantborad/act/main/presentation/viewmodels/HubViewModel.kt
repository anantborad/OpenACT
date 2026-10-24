package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.PracticeSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HubState(
    val searchQuery: String = "",
    val allSkills: List<String> = listOf(
        "Coordinate Geometry",
        "Punctuation & Grammar",
        "Algebra & Functions",
        "Passage Analysis",
        "Trigonometry",
        "Sentence Structure"
    ),
    val filteredSkills: List<String> = emptyList(),
    val subjectAccuracies: Map<PracticeSubject, Int> = emptyMap(),
    val isLoading: Boolean = false
)

class HubViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HubState())
    val uiState: StateFlow<HubState> = _uiState.asStateFlow()

    init {
        loadHubData()
    }

    fun loadHubData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val stats = repository.getUserStats()
                val accuracies = stats.subjects.associate { stat ->
                    val subject = when (stat.name.lowercase()) {
                        "english" -> PracticeSubject.ENGLISH
                        "math" -> PracticeSubject.MATH
                        else -> PracticeSubject.READING
                    }
                    subject to stat.accuracy
                }
                val currentSkills = _uiState.value.allSkills
                _uiState.value = _uiState.value.copy(
                    subjectAccuracies = accuracies,
                    filteredSkills = currentSkills,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.allSkills
        } else {
            _uiState.value.allSkills.filter { it.contains(query, ignoreCase = true) }
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredSkills = filtered
        )
    }
}
