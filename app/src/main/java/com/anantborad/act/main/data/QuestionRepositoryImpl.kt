package com.anantborad.act.main.data

import android.content.Context
import android.util.Log
import com.anantborad.act.main.data.dto.*
import com.anantborad.act.main.data.local.*
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.*
import com.anantborad.act.main.presentation.models.EnglishTestUiModel
import com.anantborad.act.main.presentation.models.MathTestUiModel
import com.anantborad.act.main.presentation.models.ReadingPassageUiModel
import com.anantborad.act.main.presentation.models.toUiModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureDatabasePopulated()
            } catch (e: Exception) {
                Log.e("QuestionRepositoryImpl", "Background database prepopulation error: ${e.message}")
            }
        }
    }

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

    override suspend fun getUserStats(sessionLimit: Int?): UserStats = withContext(Dispatchers.IO) {
        val allSessions = questionDao.getAllSessions()
        val sessions = if (sessionLimit != null && sessionLimit > 0) {
            allSessions.take(sessionLimit)
        } else {
            allSessions
        }

        val answers = if (sessions.isEmpty()) {
            emptyList()
        } else if (sessionLimit != null && sessionLimit > 0) {
            val sessionIds = sessions.map { it.id }.toSet()
            questionDao.getAllUserAnswers().filter { it.sessionId in sessionIds }
        } else {
            questionDao.getAllUserAnswers()
        }

        val userName = getUserName()
        val totalQuestions = answers.size
        val goalScore = getGoalActScore()
        val targetAcc = calculateTargetAccuracy(goalScore)

        val overallAccuracy = if (totalQuestions > 0) {
            (answers.count { it.isCorrect } * 100) / totalQuestions
        } else 0

        val subjectStats = PracticeSubject.values().map { subject ->
            val subjectAnswers = answers.filter { it.subject.equals(subject.name, ignoreCase = true) }
            if (subjectAnswers.isNotEmpty()) {
                val acc = (subjectAnswers.count { it.isCorrect } * 100) / subjectAnswers.size
                SubjectStats(subject.displayName, acc, "Overall", hasPracticed = true)
            } else {
                val recentSubjectSession = sessions.firstOrNull { it.subject.equals(subject.name, ignoreCase = true) }
                if (recentSubjectSession != null && recentSubjectSession.totalQuestions > 0) {
                    val acc = (recentSubjectSession.score * 100) / recentSubjectSession.totalQuestions
                    SubjectStats(subject.displayName, acc, "Latest", hasPracticed = true)
                } else {
                    SubjectStats(subject.displayName, 0, "--", hasPracticed = false)
                }
            }
        }

        // Weakest skills calculation - return empty if no answer history exists
        val weakSkillsList = if (answers.isNotEmpty()) {
            val skillGroups = answers.groupBy { it.skill }
            skillGroups.map { (skill, list) ->
                val acc = if (list.isNotEmpty()) (list.count { it.isCorrect } * 100) / list.size else 0
                val rawSubject = list.firstOrNull()?.subject ?: "General"
                val formattedSubject = when (rawSubject.uppercase()) {
                    "ENGLISH" -> "English"
                    "MATH" -> "Math"
                    "READING" -> "Reading"
                    else -> rawSubject.lowercase().replaceFirstChar { it.uppercase() }
                }
                SkillStat(skill, formattedSubject, acc)
            }.sortedBy { it.accuracy }.take(2)
        } else {
            emptyList()
        }

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todaySessions = allSessions.count {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp)) == todayStr
        }
        val targetGoal = sharedPrefs.getInt("daily_goal_target", 3)

        UserStats(
            name = userName,
            streak = calculateStreak(allSessions),
            dailyGoal = "$todaySessions/$targetGoal sessions",
            accuracy = overallAccuracy,
            totalQuestions = totalQuestions,
            subjects = subjectStats,
            weakSkills = weakSkillsList,
            goalActScore = goalScore,
            targetAccuracy = targetAcc
        )
    }

    private fun calculateTargetAccuracy(score: Int): Int {
        return when {
            score >= 36 -> 96
            score >= 35 -> 94
            score >= 34 -> 92
            score >= 33 -> 90
            score >= 32 -> 88
            score >= 31 -> 85
            score >= 30 -> 83
            score >= 28 -> 78
            score >= 25 -> 70
            else -> ((score / 36.0) * 100).toInt().coerceIn(40, 100)
        }
    }

    private fun calculateStreak(sessions: List<UserSessionEntity>): Int {
        if (sessions.isEmpty()) return 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sessionDates = sessions.map { dateFormat.format(Date(it.timestamp)) }.distinct().sortedDescending()
        if (sessionDates.isEmpty()) return 0

        val today = dateFormat.format(Date())
        var streak = 0
        var checkDate = Date()

        for (i in 0..30) {
            val dateStr = dateFormat.format(checkDate)
            if (sessionDates.contains(dateStr)) {
                streak++
            } else if (i == 0 && !sessionDates.contains(today)) {
                // Today has no session yet, check if yesterday had
            } else {
                break
            }
            checkDate = Date(checkDate.time - 24 * 60 * 60 * 1000)
        }
        return streak
    }

    override fun getLastSessionQuestions(): List<PracticeQuestion> = lastSessionQuestions

    override fun saveLastSessionAnswers(answers: Map<String, String>) {
        lastSessionAnswers = answers
    }

    override fun getLastSessionAnswers(): Map<String, String> = lastSessionAnswers

    override suspend fun saveSessionResult(
        subject: PracticeSubject,
        questions: List<PracticeQuestion>,
        answers: Map<String, String>,
        timeSeconds: Long,
        difficulty: String
    ): Long = withContext(Dispatchers.IO) {
        val score = questions.count { q -> answers[q.id] == q.correctOptionId }
        val sessionEntity = UserSessionEntity(
            subject = subject.name,
            score = score,
            totalQuestions = questions.size,
            timeSeconds = timeSeconds,
            difficulty = difficulty,
            timestamp = System.currentTimeMillis()
        )
        val sessionId = questionDao.insertUserSession(sessionEntity)

        val answerEntities = questions.map { q ->
            val userSelected = answers[q.id] ?: ""
            UserAnswerEntity(
                sessionId = sessionId,
                questionId = q.id,
                subject = subject.name,
                skill = q.skill,
                selectedOptionId = userSelected,
                correctOptionId = q.correctOptionId,
                isCorrect = userSelected == q.correctOptionId,
                timestamp = System.currentTimeMillis()
            )
        }
        questionDao.insertUserAnswers(answerEntities)
        saveLastSessionAnswers(answers)
        lastSessionQuestions = questions
        sessionId
    }

    override suspend fun getRecentSessions(limit: Int): List<UserSessionEntity> = withContext(Dispatchers.IO) {
        questionDao.getRecentSessions(limit)
    }

    override suspend fun getSessionAnswers(sessionId: Long): List<UserAnswerEntity> = withContext(Dispatchers.IO) {
        questionDao.getAnswersForSession(sessionId)
    }

    override suspend fun getSkillMastery(skillName: String): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val attempts = questionDao.getUserAnswersForSkill(skillName)
        if (attempts.isEmpty()) {
            Pair(0, 0)
        } else {
            val accuracy = (attempts.count { it.isCorrect } * 100) / attempts.size
            Pair(accuracy, attempts.size)
        }
    }

    override suspend fun getAllSkills(): List<String> = withContext(Dispatchers.IO) {
        ensureDatabasePopulated()
        val mathCategories = questionDao.getMathCategories()
        val readingPassages = questionDao.getAllReadingPassages().map { it.passageType }
        val englishTopics = listOf(
            "Grammar & Punctuation",
            "Sentence Structure",
            "Passage Analysis",
            "Usage & Style"
        )
        (mathCategories + readingPassages + englishTopics).distinct().sorted()
    }

    override suspend fun clearUserData() = withContext(Dispatchers.IO) {
        questionDao.clearSessions()
        questionDao.clearAnswers()
        lastSessionQuestions = emptyList()
        lastSessionAnswers = emptyMap()
    }

    override suspend fun populateDummyData() = withContext(Dispatchers.IO) {
        clearUserData()
        setGoalActScore(32)

        val currentTime = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        val dummySessionsData = listOf(
            Triple(PracticeSubject.ENGLISH, 7, "Grammar & Punctuation"),
            Triple(PracticeSubject.MATH, 8, "Algebra"),
            Triple(PracticeSubject.READING, 8, "Literary Narrative"),
            Triple(PracticeSubject.ENGLISH, 9, "Sentence Structure"),
            Triple(PracticeSubject.MATH, 9, "Geometry"),
            Triple(PracticeSubject.READING, 10, "Social Science"),
            Triple(PracticeSubject.ENGLISH, 10, "Grammar & Punctuation")
        )

        dummySessionsData.forEachIndexed { index, (subject, score, skill) ->
            val timestamp = currentTime - ((dummySessionsData.size - 1 - index) * dayMillis)
            val sessionEntity = UserSessionEntity(
                subject = subject.name,
                score = score,
                totalQuestions = 10,
                timeSeconds = 300L + (index * 15L),
                difficulty = if (index < 3) "Medium" else "Hard",
                timestamp = timestamp
            )
            val sessionId = questionDao.insertUserSession(sessionEntity)

            val answerEntities = (1..10).map { qIndex ->
                val isCorrect = qIndex <= score
                UserAnswerEntity(
                    sessionId = sessionId,
                    questionId = "dummy_${index}_$qIndex",
                    subject = subject.name,
                    skill = skill,
                    selectedOptionId = if (isCorrect) "A" else "B",
                    correctOptionId = "A",
                    isCorrect = isCorrect,
                    timestamp = timestamp
                )
            }
            questionDao.insertUserAnswers(answerEntities)
        }
    }

    override suspend fun resetAllData() = withContext(Dispatchers.IO) {
        clearUserData()
        sharedPrefs.edit().clear().commit()
        isPopulated = false
        ensureDatabasePopulated()
    }

    override fun getUserName(): String {
        return sharedPrefs.getString("user_name", "Alex") ?: "Alex"
    }

    override fun setUserName(name: String) {
        sharedPrefs.edit().putString("user_name", name).apply()
    }

    override fun getDailyGoal(): String {
        val target = sharedPrefs.getInt("daily_goal_target", 3)
        return "$target sessions"
    }

    override fun setDailyGoal(goal: String) {
        val target = goal.filter { it.isDigit() }.toIntOrNull() ?: 3
        sharedPrefs.edit().putInt("daily_goal_target", target).apply()
    }

    override fun getGoalActScore(): Int {
        return sharedPrefs.getInt("goal_act_score", 32)
    }

    override fun setGoalActScore(score: Int) {
        sharedPrefs.edit().putInt("goal_act_score", score).apply()
    }

    override fun isOnboardingCompleted(): Boolean {
        return sharedPrefs.getBoolean("onboarding_completed", false)
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        sharedPrefs.edit().putBoolean("onboarding_completed", completed).apply()
    }

    override suspend fun getPracticeQuestions(
        subject: PracticeSubject,
        limit: Int,
        difficulty: String,
        skillFilter: String?
    ): List<PracticeQuestion> = withContext(Dispatchers.IO) {
        var questions: List<PracticeQuestion> = emptyList()
        val time = measureTimeMillis {
            ensureDatabasePopulated()
            questions = when (subject) {
                PracticeSubject.ENGLISH -> {
                    val testIds = questionDao.getEnglishTestIds()
                    if (testIds.isEmpty()) emptyList()
                    else {
                        val randomTestId = testIds.random()
                        val test = questionDao.getEnglishTestsByIds(listOf(randomTestId)).first()
                        val passageStr = test.passage.joinToString("\n\n") { p ->
                            p.segments.joinToString("") { it.content }
                        }
                        var filteredQs = test.questions
                        if (!skillFilter.isNullOrBlank()) {
                            val filterText = skillFilter
                            filteredQs = filteredQs.filter { it.questionText.contains(filterText, ignoreCase = true) }
                                .ifEmpty { test.questions }
                        }
                        filteredQs.shuffled().take(limit).map { q ->
                            PracticeQuestion(
                                id = q.id.toString(),
                                subject = PracticeSubject.ENGLISH,
                                skill = skillFilter ?: "Grammar & Punctuation",
                                text = q.questionText,
                                passage = passageStr,
                                options = q.options.map { QuestionOption(it.key, it.key, it.value) },
                                correctOptionId = q.answer ?: q.options.firstOrNull()?.key ?: "A",
                                explanation = q.explanation ?: "Select the option that adheres to ACT English standards for standard written conventions, conciseness, and clarity."
                            )
                        }
                    }
                }
                PracticeSubject.MATH -> {
                    val mathQuestions = if (!skillFilter.isNullOrBlank()) {
                        val categoryFilter = skillFilter
                        questionDao.getMathQuestionsByCategory(categoryFilter)
                            .ifEmpty { questionDao.getAllMathQuestions() }
                    } else {
                        questionDao.getAllMathQuestions()
                    }

                    var filtered = mathQuestions
                    if (difficulty != "Mixed") {
                        filtered = mathQuestions.filter { it.difficulty.equals(difficulty, ignoreCase = true) }
                            .ifEmpty { mathQuestions }
                    }

                    filtered.shuffled().take(limit).map { q ->
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
                PracticeSubject.READING -> {
                    val passageEntities = if (!skillFilter.isNullOrBlank()) {
                        val passageTypeFilter = skillFilter
                        questionDao.getReadingPassagesByType(passageTypeFilter)
                            .ifEmpty { questionDao.getAllReadingPassages() }
                    } else {
                        questionDao.getAllReadingPassages()
                    }

                    if (passageEntities.isEmpty()) emptyList()
                    else {
                        val randomPassage = passageEntities.random()
                        val passageStr = randomPassage.passage.joinToString("\n\n") { it.value }
                        randomPassage.questions.shuffled().take(limit).map { q ->
                            PracticeQuestion(
                                id = q.number.toString(),
                                subject = PracticeSubject.READING,
                                skill = randomPassage.passageType,
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
        Log.d("ACT_PERF", "getPracticeQuestions ($subject, diff=$difficulty, skill=$skillFilter) took ${time}ms")
        lastSessionQuestions = questions
        questions
    }

}
