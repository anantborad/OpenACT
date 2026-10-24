package com.anantborad.act.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        EnglishTestEntity::class,
        MathQuestionEntity::class,
        ReadingPassageEntity::class,
        UserSessionEntity::class,
        UserAnswerEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class ActDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}
