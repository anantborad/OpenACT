package com.anantborad.act.main.data

import com.anantborad.act.main.data.dto.EnglishTestDto
import com.anantborad.act.main.data.dto.MathQuestionDto
import com.anantborad.act.main.data.dto.ReadingItemDto
import android.content.res.AssetManager
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

interface QuestionDataSource {
    fun getEnglishTests(): List<EnglishTestDto>
    fun getMathCategories(): Map<String, Map<String, List<MathQuestionDto>>>
    fun getReadingCategories(): Map<String, Map<String, List<ReadingItemDto>>>
}

class QuestionDataSourceImpl(private val assets: AssetManager) : QuestionDataSource {
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

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private inline fun <reified T> readJson(fileName: String): T? {
        return try {
            val inputStream: InputStream = assets.open("questions/$fileName")
            inputStream.use {
                json.decodeFromStream<T>(it)
            }
        } catch (e: Exception) {
            Log.e("QuestionDataSource", "Error reading $fileName: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
