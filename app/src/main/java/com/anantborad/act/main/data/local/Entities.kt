package com.anantborad.act.main.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anantborad.act.main.data.dto.*

@Entity(tableName = "english_tests")
data class EnglishTestEntity(
    @PrimaryKey val testId: String,
    val title: String,
    val passage: List<PassageParagraphDto>,
    val questions: List<QuestionDto>
)

@Entity(
    tableName = "math_questions",
    indices = [Index(value = ["category", "difficulty"])]
)
data class MathQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val difficulty: String,
    val prompt: String,
    val choices: List<MathChoiceDto>,
    val answer: String,
    val explanation: String
)

@Entity(
    tableName = "reading_passages",
    indices = [Index(value = ["passageType"])]
)
data class ReadingPassageEntity(
    @PrimaryKey val id: Int,
    val passageType: String,
    val difficulty: String,
    val catalyst: String,
    val passage: List<PassageParagraphItemDto>,
    val questions: List<ReadingQuestionDto>
)
