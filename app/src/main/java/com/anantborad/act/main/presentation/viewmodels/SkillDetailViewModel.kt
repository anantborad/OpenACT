package com.anantborad.act.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anantborad.act.main.domain.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SkillDetailState(
    val skillName: String = "",
    val masteryPercentage: Int = 0,
    val totalAttempts: Int = 0,
    val statusText: String = "Not Practiced Yet",
    val keyConcepts: List<String> = emptyList(),
    val isLoading: Boolean = false
)

class SkillDetailViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillDetailState())
    val uiState: StateFlow<SkillDetailState> = _uiState.asStateFlow()

    fun loadSkill(skillName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, skillName = skillName)
            val (mastery, attempts) = repository.getSkillMastery(skillName)
            val status = when {
                attempts == 0 -> "Not Practiced Yet"
                mastery >= 80 -> "Mastered"
                mastery >= 60 -> "Moderate Proficiency"
                else -> "Needs Improvement"
            }

            val concepts = when {
                skillName.contains("Geometry", ignoreCase = true) -> listOf(
                    "Coordinate Geometry Principles",
                    "Slope and Intercept Calculations",
                    "Distance and Midpoint Formulas",
                    "Circle Equations and Properties"
                )
                skillName.contains("Punctuation", ignoreCase = true) || skillName.contains("Grammar", ignoreCase = true) -> listOf(
                    "Comma Splices and Run-ons",
                    "Apostrophes and Possessives",
                    "Semicolons and Colons Usage",
                    "Subject-Verb Agreement Rules"
                )
                skillName.contains("Algebra", ignoreCase = true) -> listOf(
                    "Linear Equations and Systems",
                    "Quadratic Functions and Factoring",
                    "Exponents and Radicals Rules",
                    "Inequalities and Absolute Value"
                )
                else -> listOf(
                    "Core Principles & Rules",
                    "Key Problem Solving Steps",
                    "Common ACT Question Pitfalls",
                    "Speed and Accuracy Strategies"
                )
            }

            _uiState.value = SkillDetailState(
                skillName = skillName,
                masteryPercentage = mastery,
                totalAttempts = attempts,
                statusText = status,
                keyConcepts = concepts,
                isLoading = false
            )
        }
    }
}
