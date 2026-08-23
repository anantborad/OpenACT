package com.anantborad.act.main.data

import com.anantborad.act.main.data.dto.EnglishTestDto
import com.anantborad.act.main.data.dto.MathQuestionDto
import com.anantborad.act.main.data.dto.ReadingItemDto
import kotlinx.serialization.json.Json
import java.io.InputStream

interface QuestionDataSource {
    fun getEnglishTests(): List<EnglishTestDto>
    fun getMathCategories(): Map<String, Map<String, List<MathQuestionDto>>>
    fun getReadingCategories(): Map<String, Map<String, List<ReadingItemDto>>>
}

class QuestionDataSourceImpl : QuestionDataSource {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override fun getEnglishTests(): List<EnglishTestDto> {
        return readJson<List<EnglishTestDto>>("english.json") ?: emptyList()
    }

    override fun getMathCategories(): Map<String, Map<String, List<MathQuestionDto>>> {
        return readJson<Map<String, Map<String, List<MathQuestionDto>>>>("math.json") ?: emptyMap()
    }

    override fun getReadingCategories(): Map<String, Map<String, List<ReadingItemDto>>> {
        return readJson<Map<String, Map<String, List<ReadingItemDto>>>>("reading.json") ?: emptyMap()
    }

    private inline fun <reified T> readJson(fileName: String): T? {
        val resourcePath = "/com/anantborad/act/core/assets/questions/$fileName"
        return try {
            val inputStream: InputStream? = javaClass.getResourceAsStream(resourcePath)
            inputStream?.use {
                val jsonString = it.bufferedReader().readText()
                json.decodeFromString<T>(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
