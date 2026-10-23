package com.anantborad.act.main.domain

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
    suspend fun getUserStats(): UserStats
    suspend fun getPracticeQuestions(subject: PracticeSubject, limit: Int = 10): List<PracticeQuestion>
    fun getLastSessionQuestions(): List<PracticeQuestion>
    fun saveLastSessionAnswers(answers: Map<String, String>)
    fun getLastSessionAnswers(): Map<String, String>
}
