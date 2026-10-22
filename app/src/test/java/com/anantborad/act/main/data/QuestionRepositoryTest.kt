package com.anantborad.act.main.data

import android.content.SharedPreferences
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

    private val fakeContext = object : android.content.ContextWrapper(null) {
        private val prefs = mutableMapOf<String, Any>()
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
            return object : SharedPreferences {
                override fun edit(): SharedPreferences.Editor {
                    return object : SharedPreferences.Editor {
                        override fun putString(key: String, value: String?): SharedPreferences.Editor {
                            value?.let { prefs[key] = it }
                            return this
                        }
                        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor {
                            values?.let { prefs[key] = it }
                            return this
                        }
                        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
                            prefs[key] = value
                            return this
                        }
                        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
                            prefs[key] = value
                            return this
                        }
                        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
                            prefs[key] = value
                            return this
                        }
                        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
                            prefs[key] = value
                            return this
                        }
                        override fun remove(key: String): SharedPreferences.Editor {
                            prefs.remove(key)
                            return this
                        }
                        override fun clear(): SharedPreferences.Editor {
                            prefs.clear()
                            return this
                        }
                        override fun commit(): Boolean = true
                        override fun apply() {}
                    }
                }
                override fun getAll(): MutableMap<String, *> = prefs
                override fun getString(key: String, defValue: String?): String? = prefs[key] as? String ?: defValue
                @Suppress("UNCHECKED_CAST")
                override fun getStringSet(key: String, defValue: MutableSet<String>?): MutableSet<String>? = prefs[key] as? MutableSet<String> ?: defValue
                override fun getInt(key: String, defValue: Int): Int = prefs[key] as? Int ?: defValue
                override fun getLong(key: String, defValue: Long): Long = prefs[key] as? Long ?: defValue
                override fun getFloat(key: String, defValue: Float): Float = prefs[key] as? Float ?: defValue
                override fun getBoolean(key: String, defValue: Boolean): Boolean = prefs[key] as? Boolean ?: defValue
                override fun contains(key: String): Boolean = prefs.containsKey(key)
                override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
                override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
            }
        }
    }
    
    private val fakeDataSource = object : QuestionDataSource {
        override fun getEnglishTests(): List<EnglishTestDto> = listOf(
            EnglishTestDto("1", "Title", emptyList(), emptyList()),
        )
        override fun getMathCategories(): Map<String, Map<String, List<MathQuestionDto>>> = mapOf(
            "Algebra" to mapOf("Easy" to listOf(MathQuestionDto("Prompt", emptyList(), "A", "Expl")))
        )
        override fun getReadingCategories(): Map<String, Map<String, List<ReadingItemDto>>> = mapOf(
            "Natural Science" to mapOf(
                "Medium" to listOf(
                    ReadingItemDto(
                        ReadingMetadataDto("", ""),
                        PassageDataDto(1, "Natural Science", "Medium", "Cat", emptyList(), emptyList())
                    )
                )
            )
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
            mathQuestions.filter { (it.category == category) && (it.difficulty == difficulty) }
        override suspend fun getMathCategories(): List<String> = mathQuestions.asSequence().map { it.category }.distinct().toList()
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
        override suspend fun getMathIds(): List<Long> = mathQuestions.map { it.id }
        override suspend fun getMathQuestionsByIds(ids: List<Long>): List<MathQuestionEntity> = mathQuestions.filter { it.id in ids }
        override suspend fun getEnglishTestIds(): List<String> = englishTests.map { it.testId }
        override suspend fun getEnglishTestsByIds(ids: List<String>): List<EnglishTestEntity> = englishTests.filter { it.testId in ids }
        override suspend fun getReadingPassageIds(): List<Int> = readingPassages.map { it.id }
        override suspend fun getReadingPassagesByIds(ids: List<Int>): List<ReadingPassageEntity> = readingPassages.filter { it.id in ids }
    }

    @Before
    fun setUp() {
        repository = QuestionRepositoryImpl(fakeDataSource, fakeDao, fakeContext)
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
