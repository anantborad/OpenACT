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
                    "Coordinate Geometry & Graphing Rules",
                    "Slope, Distance, and Midpoint Formulas",
                    "Circle Equations and Angle Properties",
                    "Area, Perimeter, and Volume Calculations"
                )
                skillName.contains("Punctuation", ignoreCase = true) || skillName.contains("Grammar", ignoreCase = true) -> listOf(
                    "Comma Splices, Run-ons, and Fragments",
                    "Apostrophes, Possessives, and Contractions",
                    "Semicolons, Colons, and Dash Usage",
                    "Subject-Verb and Pronoun Agreement Rules"
                )
                skillName.contains("Algebra", ignoreCase = true) || skillName.contains("Functions", ignoreCase = true) -> listOf(
                    "Linear Equations and Systems of Equations",
                    "Quadratic Functions, Factoring, and Polynomials",
                    "Exponents, Radicals, and Logarithms",
                    "Inequalities and Absolute Value Operations"
                )
                skillName.contains("Reading", ignoreCase = true) || skillName.contains("Passage", ignoreCase = true) || skillName.contains("Narrative", ignoreCase = true) -> listOf(
                    "Main Idea and Paragraph Purpose Identification",
                    "Vocabulary in Context and Tone Analysis",
                    "Inferences and Supporting Evidence Location",
                    "Comparative Passage Analysis Strategies"
                )
                else -> listOf(
                    "Core Principles & Rule Definitions for $skillName",
                    "Key Problem Solving & Step-by-Step Approaches",
                    "Common ACT Question Pitfalls & Traps",
                    "Speed, Pacing, and Accuracy Strategies"
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
