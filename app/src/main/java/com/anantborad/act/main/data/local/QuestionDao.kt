package com.anantborad.act.main.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnglishTests(tests: List<EnglishTestEntity>)

    @Query("SELECT * FROM english_tests")
    suspend fun getAllEnglishTests(): List<EnglishTestEntity>

    @Query("SELECT COUNT(*) FROM english_tests")
    suspend fun getEnglishTestCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMathQuestions(questions: List<MathQuestionEntity>)

    @Query("SELECT * FROM math_questions WHERE category = :category AND difficulty = :difficulty")
    suspend fun getMathQuestions(category: String, difficulty: String): List<MathQuestionEntity>

    @Query("SELECT DISTINCT category FROM math_questions")
    suspend fun getMathCategories(): List<String>

    @Query("SELECT DISTINCT difficulty FROM math_questions WHERE category = :category")
    suspend fun getMathDifficulties(category: String): List<String>

    @Query("SELECT * FROM math_questions")
    suspend fun getAllMathQuestions(): List<MathQuestionEntity>

    @Query("SELECT COUNT(*) FROM math_questions")
    suspend fun getMathQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingPassages(passages: List<ReadingPassageEntity>)

    @Query("SELECT * FROM reading_passages")
    suspend fun getAllReadingPassages(): List<ReadingPassageEntity>

    @Query("SELECT COUNT(*) FROM reading_passages")
    suspend fun getReadingPassageCount(): Int

    @Query("SELECT id FROM math_questions")
    suspend fun getMathIds(): List<Long>

    @Query("SELECT * FROM math_questions WHERE id IN (:ids)")
    suspend fun getMathQuestionsByIds(ids: List<Long>): List<MathQuestionEntity>

    @Query("SELECT testId FROM english_tests")
    suspend fun getEnglishTestIds(): List<String>

    @Query("SELECT * FROM english_tests WHERE testId IN (:ids)")
    suspend fun getEnglishTestsByIds(ids: List<String>): List<EnglishTestEntity>

    @Query("SELECT id FROM reading_passages")
    suspend fun getReadingPassageIds(): List<Int>

    @Query("SELECT * FROM reading_passages WHERE id IN (:ids)")
    suspend fun getReadingPassagesByIds(ids: List<Int>): List<ReadingPassageEntity>
    
    @Query("SELECT * FROM english_tests ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomEnglishTests(limit: Int): List<EnglishTestEntity>

    @Query("SELECT * FROM math_questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomMathQuestions(limit: Int): List<MathQuestionEntity>

    @Query("SELECT * FROM reading_passages ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomReadingPassages(limit: Int): List<ReadingPassageEntity>
}
