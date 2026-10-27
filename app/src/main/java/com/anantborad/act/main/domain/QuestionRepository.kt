package com.anantborad.act.main.domain

import com.anantborad.act.main.data.local.UserAnswerEntity
import com.anantborad.act.main.data.local.UserSessionEntity
import com.anantborad.act.main.domain.model.PracticeQuestion
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.domain.model.UserStats
import com.anantborad.act.main.presentation.models.EnglishTestUiModel
import com.anantborad.act.main.presentation.models.MathTestUiModel
import com.anantborad.act.main.presentation.models.ReadingPassageUiModel

interface QuestionRepository {
    suspend fun getEnglishTests(): List<EnglishTestUiModel>
    suspend fun getMathTests(): List<MathTestUiModel>
    suspend fun getReadingTests(): List<ReadingPassageUiModel>
    suspend fun getUserStats(sessionLimit: Int? = null): UserStats
    suspend fun getPracticeQuestions(
        subject: PracticeSubject,
        limit: Int = 10,
        difficulty: String = "Mixed",
        skillFilter: String? = null
    ): List<PracticeQuestion>
    fun getLastSessionQuestions(): List<PracticeQuestion>
    fun saveLastSessionAnswers(answers: Map<String, String>)
    fun getLastSessionAnswers(): Map<String, String>

    // Session Persistence & Analytics
    suspend fun saveSessionResult(
        subject: PracticeSubject,
        questions: List<PracticeQuestion>,
        answers: Map<String, String>,
        timeSeconds: Long,
        difficulty: String = "Mixed"
    ): Long

    suspend fun getRecentSessions(limit: Int = 10): List<UserSessionEntity>
    suspend fun getSessionAnswers(sessionId: Long): List<UserAnswerEntity>
    suspend fun getSkillMastery(skillName: String): Pair<Int, Int> // (accuracyPercent, totalAttempts)
    suspend fun getAllSkills(): List<String>
    suspend fun clearUserData()
    suspend fun resetAllData()
    suspend fun populateDummyData()

    // User Profile Preferences
    fun getUserName(): String
    fun setUserName(name: String)
    fun getDailyGoal(): String
    fun setDailyGoal(goal: String)
    fun getGoalActScore(): Int
    fun setGoalActScore(score: Int)
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}
