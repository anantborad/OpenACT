package com.anantborad.act.main.presentation.models

import com.anantborad.act.main.data.dto.MathChoiceDto
import com.anantborad.act.main.data.dto.MathQuestionDto

data class MathTestUiModel(
    val category: String,
    val difficulty: String,
    val questions: List<MathQuestionUiModel>
)

data class MathQuestionUiModel(
    val prompt: String,
    val choices: List<MathChoiceUiModel>,
    val answer: String,
    val explanation: String,
    val selectedChoiceLabel: String? = null,
    val isSubmitted: Boolean = false
) {
    val isCorrect: Boolean?
        get() = if (selectedChoiceLabel != null) selectedChoiceLabel == answer else null
}

data class MathChoiceUiModel(
    val label: String,
    val text: String,
    val isSelected: Boolean = false
)

// Extension mappers to convert DTOs to UI models
fun MathQuestionDto.toUiModel(selectedChoiceLabel: String? = null): MathQuestionUiModel {
    return MathQuestionUiModel(
        prompt = prompt,
        choices = choices.map { it.toUiModel(isSelected = it.label == selectedChoiceLabel) },
        answer = answer,
        explanation = explanation,
        selectedChoiceLabel = selectedChoiceLabel,
        isSubmitted = selectedChoiceLabel != null
    )
}

fun MathChoiceDto.toUiModel(isSelected: Boolean = false): MathChoiceUiModel {
    return MathChoiceUiModel(
        label = label,
        text = text,
        isSelected = isSelected
    )
}
