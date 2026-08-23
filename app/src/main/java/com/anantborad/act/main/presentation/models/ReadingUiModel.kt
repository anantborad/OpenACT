package com.anantborad.act.main.presentation.models

import com.anantborad.act.main.data.dto.PassageDataDto
import com.anantborad.act.main.data.dto.PassageParagraphItemDto
import com.anantborad.act.main.data.dto.ReadingChoiceDto
import com.anantborad.act.main.data.dto.ReadingItemDto
import com.anantborad.act.main.data.dto.ReadingQuestionDto

data class ReadingPassageUiModel(
    val id: Int,
    val passageType: String,
    val difficulty: String,
    val catalyst: String,
    val paragraphs: List<ReadingParagraphUiModel>,
    val questions: List<ReadingQuestionUiModel>
)

data class ReadingParagraphUiModel(
    val paragraphIndex: Int,
    val text: String,
    val linkedQuestionNumber: Int? = null
)

data class ReadingQuestionUiModel(
    val number: Int,
    val prompt: String,
    val choices: List<ReadingChoiceUiModel>,
    val selectedChoiceLabel: String? = null
)

data class ReadingChoiceUiModel(
    val label: String,
    val text: String,
    val isSelected: Boolean = false
)

// Extension mappers to convert DTOs to UI models
fun ReadingItemDto.toUiModel(): ReadingPassageUiModel = passageData.toUiModel()

fun PassageDataDto.toUiModel(): ReadingPassageUiModel {
    return ReadingPassageUiModel(
        id = id,
        passageType = passageType,
        difficulty = difficulty,
        catalyst = catalyst,
        paragraphs = passage.mapIndexed { index, item -> item.toUiModel(index) },
        questions = questions.map { it.toUiModel() }
    )
}

fun PassageParagraphItemDto.toUiModel(index: Int = 0): ReadingParagraphUiModel {
    return ReadingParagraphUiModel(
        paragraphIndex = index,
        text = value,
        linkedQuestionNumber = if (question != -1) question else null
    )
}

fun ReadingQuestionDto.toUiModel(): ReadingQuestionUiModel {
    return ReadingQuestionUiModel(
        number = number,
        prompt = prompt,
        choices = choices.map { it.toUiModel() }
    )
}

fun ReadingChoiceDto.toUiModel(isSelected: Boolean = false): ReadingChoiceUiModel {
    return ReadingChoiceUiModel(
        label = label,
        text = text,
        isSelected = isSelected
    )
}
