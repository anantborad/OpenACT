package com.anantborad.act.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MathCategoriesDto(
    val categories: Map<String, Map<String, List<MathQuestionDto>>>
)

@Serializable
data class MathQuestionDto(
    val prompt: String,
    val choices: List<MathChoiceDto>,
    val answer: String,
    val explanation: String
)

@Serializable
data class MathChoiceDto(
    val label: String,
    val text: String
)
