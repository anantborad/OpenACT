package com.anantborad.act.main.data

import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.*
import com.anantborad.act.main.presentation.models.EnglishTestUiModel
import com.anantborad.act.main.presentation.models.MathTestUiModel
import com.anantborad.act.main.presentation.models.ReadingPassageUiModel
import com.anantborad.act.main.presentation.models.toUiModel
import com.anantborad.act.main.presentation.util.MOCK_USER_STATS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestionRepositoryImpl(
    private val dataSource: QuestionDataSource
) : QuestionRepository {

    private var lastSessionQuestions: List<PracticeQuestion> = emptyList()

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

    override suspend fun getUserStats(): UserStats = withContext(Dispatchers.IO) {
        MOCK_USER_STATS
    }

    override fun getLastSessionQuestions(): List<PracticeQuestion> = lastSessionQuestions

    override suspend fun getPracticeQuestions(
        subject: PracticeSubject,
        limit: Int
    ): List<PracticeQuestion> = withContext(Dispatchers.IO) {
        val questions = when (subject) {
            PracticeSubject.ENGLISH -> {
                dataSource.getEnglishTests().flatMap { test ->
                    test.questions.map { q ->
                        PracticeQuestion(
                            id = q.id.toString(),
                            subject = PracticeSubject.ENGLISH,
                            skill = "Grammar",
                            text = q.questionText,
                            passage = test.passage.joinToString("\n\n") { p ->
                                p.segments.joinToString("") { it.content }
                            },
                            options = q.options.map { QuestionOption(it.key, it.key, it.value) },
                            correctOptionId = "A", // Placeholder as DTO lacks answer
                            explanation = "Explanation for English question ${q.id}"
                        )
                    }
                }.shuffled().take(limit)
            }
            PracticeSubject.MATH -> {
                dataSource.getMathCategories().flatMap { (category, difficultyMap) ->
                    difficultyMap.flatMap { (_, questions) ->
                        questions.map { q ->
                            PracticeQuestion(
                                id = java.util.UUID.randomUUID().toString(),
                                subject = PracticeSubject.MATH,
                                skill = category,
                                text = q.prompt,
                                options = q.choices.map { QuestionOption(it.label, it.label, it.text) },
                                correctOptionId = q.answer,
                                explanation = q.explanation
                            )
                        }
                    }
                }.shuffled().take(limit)
            }
            PracticeSubject.READING -> {
                dataSource.getReadingCategories().flatMap { (category, difficultyMap) ->
                    difficultyMap.flatMap { (_, items) ->
                        items.flatMap { item ->
                            item.passageData.questions.map { q ->
                                PracticeQuestion(
                                    id = q.number.toString(),
                                    subject = PracticeSubject.READING,
                                    skill = category,
                                    text = q.prompt,
                                    passage = item.passageData.passage.joinToString("\n\n") { it.value },
                                    options = q.choices.map { QuestionOption(it.label, it.label, it.text) },
                                    correctOptionId = "A", // Placeholder as DTO lacks answer
                                    explanation = "Explanation for reading question ${q.number}"
                                )
                            }
                        }
                    }
                }.shuffled().take(limit)
            }
            PracticeSubject.SCIENCE -> emptyList()
        }
        lastSessionQuestions = questions
        questions
    }
}
