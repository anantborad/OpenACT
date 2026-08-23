package com.anantborad.act.main.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnglishTestResponse(
    val tests: List<EnglishTestDto>
)

@Serializable
data class EnglishTestDto(
    @SerialName("test_id")
    val testId: String,
    val title: String,
    val passage: List<PassageParagraphDto>,
    val questions: List<QuestionDto>
)

@Serializable
data class PassageParagraphDto(
    @SerialName("paragraph_index")
    val paragraphIndex: Int,
    val segments: List<PassageSegmentDto>
)

@Serializable
data class PassageSegmentDto(
    val type: String, // e.g., "text", "highlight"
    val content: String,
    val id: String? = null,
    @SerialName("question_ref")
    val questionRef: Int? = null
)

@Serializable
data class QuestionDto(
    val id: Int,
    @SerialName("linked_highlight_id")
    val linkedHighlightId: String? = null,
    @SerialName("question_text")
    val questionText: String,
    val options: List<QuestionOptionDto>
)

@Serializable
data class QuestionOptionDto(
    val key: String,
    val value: String
)
