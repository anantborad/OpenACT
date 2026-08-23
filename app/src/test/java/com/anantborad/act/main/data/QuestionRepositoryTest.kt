package com.anantborad.act.main.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class QuestionRepositoryTest {

    private lateinit var repository: QuestionRepositoryImpl
    private lateinit var dataSource: QuestionDataSourceImpl

    @Before
    fun setUp() {
        dataSource = QuestionDataSourceImpl()
        repository = QuestionRepositoryImpl(dataSource)
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
