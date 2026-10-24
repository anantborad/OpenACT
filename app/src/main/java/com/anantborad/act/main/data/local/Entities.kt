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

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val score: Int,
    val totalQuestions: Int,
    val timeSeconds: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val difficulty: String = "Mixed"
)

@Entity(
    tableName = "user_answers",
    indices = [Index(value = ["sessionId"]), Index(value = ["skill"])]
)
data class UserAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val questionId: String,
    val subject: String,
    val skill: String,
    val selectedOptionId: String,
    val correctOptionId: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
