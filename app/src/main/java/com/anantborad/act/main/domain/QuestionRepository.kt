package com.anantborad.act.main.domain

import com.anantborad.act.main.presentation.models.EnglishTestUiModel
import com.anantborad.act.main.presentation.models.MathTestUiModel
import com.anantborad.act.main.presentation.models.ReadingPassageUiModel

interface QuestionRepository {
    suspend fun getEnglishTests(): List<EnglishTestUiModel>
    suspend fun getMathTests(): List<MathTestUiModel>
    suspend fun getReadingTests(): List<ReadingPassageUiModel>
}
