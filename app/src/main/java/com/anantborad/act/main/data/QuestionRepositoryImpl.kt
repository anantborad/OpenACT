package com.anantborad.act.main.data

import com.anantborad.act.main.data.dto.*
import com.anantborad.act.main.data.local.*
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.*
import com.anantborad.act.main.presentation.models.EnglishTestUiModel
import com.anantborad.act.main.presentation.models.MathTestUiModel
import com.anantborad.act.main.presentation.models.ReadingPassageUiModel
import com.anantborad.act.main.presentation.models.toUiModel
import com.anantborad.act.main.presentation.util.MOCK_USER_STATS
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class QuestionRepositoryImpl(
    private val dataSource: QuestionDataSource,
    private val questionDao: QuestionDao,
    context: Context
) : QuestionRepository {

    private val sharedPrefs = context.getSharedPreferences("act_prefs", Context.MODE_PRIVATE)

    private var isPopulated: Boolean
        get() = sharedPrefs.getBoolean("db_populated", false)
        set(value) = sharedPrefs.edit().putBoolean("db_populated", value).apply()

    private var lastSessionQuestions: List<PracticeQuestion> = emptyList()
    private var lastSessionAnswers: Map<String, String> = emptyMap()

    private suspend fun ensureDatabasePopulated() {
        if (!isPopulated || isDatabaseEmpty()) {
            prepopulateDatabase()
        }
    }

    private suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        questionDao.getMathQuestionCount() == 0 &&
                questionDao.getEnglishTestCount() == 0 &&
                questionDao.getReadingPassageCount() == 0
    }

    private suspend fun prepopulateDatabase() = withContext(Dispatchers.IO) {
        coroutineScope {
            val englishJob = async {
                if (questionDao.getEnglishTestCount() == 0) {
                    val englishTests = dataSource.getEnglishTests().map { dto ->
                        EnglishTestEntity(
                            testId = dto.testId,
                            title = dto.title,
                            passage = dto.passage,
                            questions = dto.questions
                        )
                    }
                    questionDao.insertEnglishTests(englishTests)
                }
            }

            val mathJob = async {
                if (questionDao.getMathQuestionCount() == 0) {
                    val mathCategories = dataSource.getMathCategories()
                    val mathQuestions = mathCategories.flatMap { (category, difficultyMap) ->
                        difficultyMap.flatMap { (difficulty, questions) ->
                            questions.map { dto ->
                                MathQuestionEntity(
                                    category = category,
                                    difficulty = difficulty,
                                    prompt = dto.prompt,
                                    choices = dto.choices,
                                    answer = dto.answer,
                                    explanation = dto.explanation
                                )
                            }
                        }
                    }
                    questionDao.insertMathQuestions(mathQuestions)
                }
            }

            val readingJob = async {
                if (questionDao.getReadingPassageCount() == 0) {
                    val readingCategories = dataSource.getReadingCategories()
                    val readingPassages = readingCategories.flatMap { (_, difficultyMap) ->
                        difficultyMap.flatMap { (_, items) ->
                            items.map { dto ->
                                ReadingPassageEntity(
                                    id = dto.passageData.id,
                                    passageType = dto.passageData.passageType,
                                    difficulty = dto.passageData.difficulty,
                                    catalyst = dto.passageData.catalyst,
                                    passage = dto.passageData.passage,
                                    questions = dto.passageData.questions
                                )
                            }
                        }
                    }
                    questionDao.insertReadingPassages(readingPassages)
                }
            }

            awaitAll(englishJob, mathJob, readingJob)
            isPopulated = true
        }
    }

    override suspend fun getEnglishTests(): List<EnglishTestUiModel> = withContext(Dispatchers.IO) {
        ensureDatabasePopulated()
        questionDao.getAllEnglishTests().map { entity ->
            EnglishTestDto(
                testId = entity.testId,
                title = entity.title,
                passage = entity.passage,
                questions = entity.questions
            ).toUiModel()
        }
    }

    override suspend fun getMathTests(): List<MathTestUiModel> = withContext(Dispatchers.IO) {
        ensureDatabasePopulated()
        val categories = questionDao.getMathCategories()
        categories.flatMap { categoryName ->
            val difficulties = questionDao.getMathDifficulties(categoryName)
            difficulties.map { difficulty ->
                val questions = questionDao.getMathQuestions(categoryName, difficulty)
                MathTestUiModel(
                    category = categoryName,
                    difficulty = difficulty,
                    questions = questions.map { entity ->
                        MathQuestionDto(
                            prompt = entity.prompt,
                            choices = entity.choices,
                            answer = entity.answer,
                            explanation = entity.explanation
                        ).toUiModel()
                    }
                )
            }
        }
    }

    override suspend fun getReadingTests(): List<ReadingPassageUiModel> = withContext(Dispatchers.IO) {
        ensureDatabasePopulated()
        questionDao.getAllReadingPassages().map { entity ->
            PassageDataDto(
                id = entity.id,
                passageType = entity.passageType,
                difficulty = entity.difficulty,
                catalyst = entity.catalyst,
                passage = entity.passage,
                questions = entity.questions
            ).toUiModel()
        }
    }

    override suspend fun getUserStats(): UserStats = withContext(Dispatchers.IO) {
        MOCK_USER_STATS
    }

    override fun getLastSessionQuestions(): List<PracticeQuestion> = lastSessionQuestions

    override fun saveLastSessionAnswers(answers: Map<String, String>) {
        lastSessionAnswers = answers
    }

    override fun getLastSessionAnswers(): Map<String, String> = lastSessionAnswers

    override suspend fun getPracticeQuestions(
        subject: PracticeSubject,
        limit: Int
    ): List<PracticeQuestion> = withContext(Dispatchers.IO) {
        var questions: List<PracticeQuestion> = emptyList()
        val time = measureTimeMillis {
            ensureDatabasePopulated()
            questions = when (subject) {
                PracticeSubject.ENGLISH -> {
                    // One test has 75 questions, so usually one test is enough
                    val testIds = questionDao.getEnglishTestIds()
                    if (testIds.isEmpty()) emptyList()
                    else {
                        val randomTestId = testIds.random()
                        val test = questionDao.getEnglishTestsByIds(listOf(randomTestId)).first()
                        val passageStr = test.passage.joinToString("\n\n") { p ->
                            p.segments.joinToString("") { it.content }
                        }
                        test.questions.shuffled().take(limit).map { q ->
                            PracticeQuestion(
                                id = q.id.toString(),
                                subject = PracticeSubject.ENGLISH,
                                skill = "Grammar",
                                text = q.questionText,
                                passage = passageStr,
                                options = q.options.map { QuestionOption(it.key, it.key, it.value) },
                                correctOptionId = q.options.firstOrNull()?.key ?: "A",
                                explanation = "Explanation for English question ${q.id}"
                            )
                        }
                    }
                }
                PracticeSubject.MATH -> {
                    val allIds = questionDao.getMathIds()
                    if (allIds.isEmpty()) emptyList()
                    else {
                        val randomIds = allIds.shuffled().take(limit)
                        questionDao.getMathQuestionsByIds(randomIds).map { q ->
                            PracticeQuestion(
                                id = q.id.toString(),
                                subject = PracticeSubject.MATH,
                                skill = q.category,
                                text = q.prompt,
                                options = q.choices.map { QuestionOption(it.label, it.label, it.text) },
                                correctOptionId = q.answer,
                                explanation = q.explanation
                            )
                        }
                    }
                }
                PracticeSubject.READING -> {
                    // One passage has 10 questions
                    val passageIds = questionDao.getReadingPassageIds()
                    if (passageIds.isEmpty()) emptyList()
                    else {
                        val randomPassageId = passageIds.random()
                        val item = questionDao.getReadingPassagesByIds(listOf(randomPassageId)).first()
                        val passageStr = item.passage.joinToString("\n\n") { it.value }
                        item.questions.shuffled().take(limit).map { q ->
                            PracticeQuestion(
                                id = q.number.toString(),
                                subject = PracticeSubject.READING,
                                skill = item.passageType,
                                text = q.prompt,
                                passage = passageStr,
                                options = q.choices.map { QuestionOption(it.label, it.label, it.text) },
                                correctOptionId = q.answer,
                                explanation = q.explanation
                            )
                        }
                    }
                }
            }
        }
        Log.d("ACT_PERF", "getPracticeQuestions ($subject) took ${time}ms")
        lastSessionQuestions = questions
        questions
    }
}
