package com.anantborad.act.main.data

import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.presentation.models.EnglishTestUiModel
import com.anantborad.act.main.presentation.models.MathTestUiModel
import com.anantborad.act.main.presentation.models.ReadingPassageUiModel
import com.anantborad.act.main.presentation.models.toUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestionRepositoryImpl(
    private val dataSource: QuestionDataSource
) : QuestionRepository {

    override suspend fun getEnglishTests(): List<EnglishTestUiModel> = withContext(Dispatchers.IO) {
        dataSource.getEnglishTests().map { it.toUiModel() }
    }

    override suspend fun getMathTests(): List<MathTestUiModel> = withContext(Dispatchers.IO) {
        val categories = dataSource.getMathCategories()
        categories.flatMap { (categoryName, difficultyMap) ->
            difficultyMap.map { (difficulty, questions) ->
                MathTestUiModel(
                    category = categoryName,
                    difficulty = difficulty,
                    questions = questions.map { it.toUiModel() }
                )
            }
        }
    }

    override suspend fun getReadingTests(): List<ReadingPassageUiModel> = withContext(Dispatchers.IO) {
        val categories = dataSource.getReadingCategories()
        categories.flatMap { (_, difficultyMap) ->
            difficultyMap.flatMap { (_, items) ->
                items.map { it.toUiModel() }
            }
        }
    }
}
