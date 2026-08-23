package com.anantborad.act.main.presentation.models

import com.anantborad.act.main.data.dto.EnglishTestDto
import com.anantborad.act.main.data.dto.PassageParagraphDto
import com.anantborad.act.main.data.dto.PassageSegmentDto
import com.anantborad.act.main.data.dto.QuestionDto
import com.anantborad.act.main.data.dto.QuestionOptionDto

data class EnglishTestUiModel(
    val testId: String,
    val title: String,
    val paragraphs: List<EnglishParagraphUiModel>,
    val questions: List<EnglishQuestionUiModel>
)

data class EnglishParagraphUiModel(
    val paragraphIndex: Int,
    val segments: List<EnglishSegmentUiModel>
)

data class EnglishSegmentUiModel(
    val type: SegmentType,
    val content: String,
    val id: String? = null,
    val questionRef: Int? = null,
    val isHighlighted: Boolean = false
) {
    enum class SegmentType {
        TEXT,
        HIGHLIGHT
    }
}

data class EnglishQuestionUiModel(
    val id: Int,
    val linkedHighlightId: String? = null,
    val questionText: String,
    val options: List<EnglishOptionUiModel>,
    val selectedOptionKey: String? = null
)

data class EnglishOptionUiModel(
    val key: String,
    val value: String,
    val isSelected: Boolean = false
)

// Extension mappers to convert DTOs to UI models
fun EnglishTestDto.toUiModel(): EnglishTestUiModel {
    return EnglishTestUiModel(
        testId = testId,
        title = title,
        paragraphs = passage.map { it.toUiModel() },
        questions = questions.map { it.toUiModel() }
    )
}

fun PassageParagraphDto.toUiModel(): EnglishParagraphUiModel {
    return EnglishParagraphUiModel(
        paragraphIndex = paragraphIndex,
        segments = segments.map { it.toUiModel() }
    )
}

fun PassageSegmentDto.toUiModel(isHighlighted: Boolean = false): EnglishSegmentUiModel {
    val segmentType = when (type.lowercase()) {
        "highlight" -> EnglishSegmentUiModel.SegmentType.HIGHLIGHT
        else -> EnglishSegmentUiModel.SegmentType.TEXT
    }
    return EnglishSegmentUiModel(
        type = segmentType,
        content = content,
        id = id,
        questionRef = questionRef,
        isHighlighted = isHighlighted
    )
}

fun QuestionDto.toUiModel(selectedOptionKey: String? = null): EnglishQuestionUiModel {
    return EnglishQuestionUiModel(
        id = id,
        linkedHighlightId = linkedHighlightId,
        questionText = questionText,
        options = options.map { it.toUiModel(isSelected = it.key == selectedOptionKey) },
        selectedOptionKey = selectedOptionKey
    )
}

fun QuestionOptionDto.toUiModel(isSelected: Boolean = false): EnglishOptionUiModel {
    return EnglishOptionUiModel(
        key = key,
        value = value,
        isSelected = isSelected
    )
}
