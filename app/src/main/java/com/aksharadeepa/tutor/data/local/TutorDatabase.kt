package com.aksharadeepa.tutor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aksharadeepa.tutor.data.local.dao.TutorDao
import com.aksharadeepa.tutor.data.local.entity.ChapterEntity
import com.aksharadeepa.tutor.data.local.entity.QuestionEntity
import com.aksharadeepa.tutor.data.local.entity.QuizAttemptEntity
import com.aksharadeepa.tutor.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        ChapterEntity::class,
        QuestionEntity::class,
        QuizAttemptEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TutorDatabase : RoomDatabase() {
    abstract fun tutorDao(): TutorDao

    companion object {
        @Volatile private var instance: TutorDatabase? = null

        fun getInstance(context: Context): TutorDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TutorDatabase::class.java,
                    "akshara_deepa.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
