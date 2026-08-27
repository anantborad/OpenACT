package com.anantborad.act.main.data

import com.anantborad.act.main.data.dto.*
import com.anantborad.act.main.data.local.EnglishTestEntity
import com.anantborad.act.main.data.local.MathQuestionEntity
import com.anantborad.act.main.data.local.QuestionDao
import com.anantborad.act.main.data.local.ReadingPassageEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class QuestionRepositoryTest {

    private lateinit var repository: QuestionRepositoryImpl
    
    private val fakeDataSource = object : QuestionDataSource {
        override fun getEnglishTests(): List<EnglishTestDto> = listOf(
            EnglishTestDto("1", "Title", emptyList(), emptyList())
        )
        override fun getMathCategories(): Map<String, Map<String, List<MathQuestionDto>>> = mapOf(
            "Algebra" to mapOf("Easy" to listOf(MathQuestionDto("Prompt", emptyList(), "A", "Expl")))
        )
        override fun getReadingCategories(): Map<String, Map<String, List<ReadingItemDto>>> = mapOf(
            "Science" to mapOf("Medium" to listOf(ReadingItemDto(
                ReadingMetadataDto("", ""),
                PassageDataDto(1, "Science", "Medium", "Cat", emptyList(), emptyList())
            )))
        )
    }
    
    private val fakeDao = object : QuestionDao {
        val englishTests = mutableListOf<EnglishTestEntity>()
        val mathQuestions = mutableListOf<MathQuestionEntity>()
        val readingPassages = mutableListOf<ReadingPassageEntity>()

        override suspend fun insertEnglishTests(tests: List<EnglishTestEntity>) { englishTests.addAll(tests) }
        override suspend fun getAllEnglishTests(): List<EnglishTestEntity> = englishTests
        override suspend fun getEnglishTestCount(): Int = englishTests.size
        override suspend fun insertMathQuestions(questions: List<MathQuestionEntity>) { mathQuestions.addAll(questions) }
        override suspend fun getMathQuestions(category: String, difficulty: String): List<MathQuestionEntity> = 
            mathQuestions.filter { it.category == category && it.difficulty == difficulty }
        override suspend fun getMathCategories(): List<String> = mathQuestions.map { it.category }.distinct()
        override suspend fun getMathDifficulties(category: String): List<String> = 
            mathQuestions.filter { it.category == category }.map { it.difficulty }.distinct()
        override suspend fun getAllMathQuestions(): List<MathQuestionEntity> = mathQuestions
        override suspend fun getMathQuestionCount(): Int = mathQuestions.size
        override suspend fun insertReadingPassages(passages: List<ReadingPassageEntity>) { readingPassages.addAll(passages) }
        override suspend fun getAllReadingPassages(): List<ReadingPassageEntity> = readingPassages
        override suspend fun getReadingPassageCount(): Int = readingPassages.size
        override suspend fun getRandomEnglishTests(limit: Int): List<EnglishTestEntity> = englishTests.take(limit)
        override suspend fun getRandomMathQuestions(limit: Int): List<MathQuestionEntity> = mathQuestions.take(limit)
        override suspend fun getRandomReadingPassages(limit: Int): List<ReadingPassageEntity> = readingPassages.take(limit)
    }

    @Before
    fun setUp() {
        repository = QuestionRepositoryImpl(fakeDataSource, fakeDao)
    }

    @Test
    fun `test loading english questions`() = runBlocking {
        val tests = repository.getEnglishTests()
        assertNotNull(tests)
        assertFalse("English tests should not be empty", tests.isEmpty())
        println("Loaded ${tests.size} English tests")
    }

    @Test
    fun `test loading math questions`() = runBlocking {
        val tests = repository.getMathTests()
        assertNotNull(tests)
        assertFalse("Math tests should not be empty", tests.isEmpty())
        println("Loaded ${tests.size} Math tests")
    }

    @Test
    fun `test loading reading questions`() = runBlocking {
        val tests = repository.getReadingTests()
        assertNotNull(tests)
        assertFalse("Reading tests should not be empty", tests.isEmpty())
        println("Loaded ${tests.size} Reading tests")
    }
}
