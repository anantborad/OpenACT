package com.anantborad.act.main.data.local

import androidx.room.TypeConverter
import com.anantborad.act.main.data.dto.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromPassageParagraphList(value: List<PassageParagraphDto>): String = json.encodeToString(value)
    @TypeConverter
    fun toPassageParagraphList(value: String): List<PassageParagraphDto> = json.decodeFromString(value)

    @TypeConverter
    fun fromQuestionDtoList(value: List<QuestionDto>): String = json.encodeToString(value)
    @TypeConverter
    fun toQuestionDtoList(value: String): List<QuestionDto> = json.decodeFromString(value)

    @TypeConverter
    fun fromMathChoiceList(value: List<MathChoiceDto>): String = json.encodeToString(value)
    @TypeConverter
    fun toMathChoiceList(value: String): List<MathChoiceDto> = json.decodeFromString(value)

    @TypeConverter
    fun fromPassageParagraphItemList(value: List<PassageParagraphItemDto>): String = json.encodeToString(value)
    @TypeConverter
    fun toPassageParagraphItemList(value: String): List<PassageParagraphItemDto> = json.decodeFromString(value)

    @TypeConverter
    fun fromReadingQuestionList(value: List<ReadingQuestionDto>): String = json.encodeToString(value)
    @TypeConverter
    fun toReadingQuestionList(value: String): List<ReadingQuestionDto> = json.decodeFromString(value)
}
