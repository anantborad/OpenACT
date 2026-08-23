package com.anantborad.act.main.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadingCategoriesDto(
    val categories: Map<String, Map<String, List<ReadingItemDto>>>
)

@Serializable
data class ReadingItemDto(
    val metadata: ReadingMetadataDto,
    @SerialName("passage_data")
    val passageData: PassageDataDto
)

@Serializable
data class ReadingMetadataDto(
    val timestamp: String,
    val model: String
)

@Serializable
data class PassageDataDto(
    val id: Int,
    @SerialName("passage_type")
    val passageType: String,
    val difficulty: String,
    val catalyst: String,
    val passage: List<PassageParagraphItemDto>,
    val questions: List<ReadingQuestionDto>
)

@Serializable
data class PassageParagraphItemDto(
    @SerialName("val")
    val value: String,
    val question: Int
)

@Serializable
data class ReadingQuestionDto(
    val number: Int,
    val prompt: String,
    val choices: List<ReadingChoiceDto>
)

@Serializable
data class ReadingChoiceDto(
    val label: String,
    val text: String
)
