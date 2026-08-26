package com.anantborad.act.main.domain.model

enum class PracticeSubject {
    ENGLISH, MATH, READING, SCIENCE;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

data class QuestionOption(
    val id: String,
    val label: String,
    val text: String
)

data class PracticeQuestion(
    val id: String,
    val subject: PracticeSubject,
    val skill: String,
    val text: String,
    val passage: String? = null,
    val options: List<QuestionOption>,
    val correctOptionId: String,
    val explanation: String
)

data class SubjectStats(
    val name: String,
    val accuracy: Int,
    val trend: String
)

data class SkillStat(
    val name: String,
    val subject: String,
    val accuracy: Int
)

data class UserStats(
    val name: String,
    val streak: Int,
    val dailyGoal: String,
    val accuracy: Int,
    val totalQuestions: Int,
    val subjects: List<SubjectStats>,
    val weakSkills: List<SkillStat>
)
