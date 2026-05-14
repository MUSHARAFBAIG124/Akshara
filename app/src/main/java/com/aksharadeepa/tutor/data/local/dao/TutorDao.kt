package com.aksharadeepa.tutor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aksharadeepa.tutor.data.local.entity.ChapterEntity
import com.aksharadeepa.tutor.data.local.entity.QuestionEntity
import com.aksharadeepa.tutor.data.local.entity.QuizAttemptEntity
import com.aksharadeepa.tutor.data.local.entity.SubjectEntity
import com.aksharadeepa.tutor.data.local.relation.SubjectWithChapters
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)

    @Transaction
    suspend fun seedInitialDataIfEmpty(
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        questions: List<QuestionEntity>
    ) {
        if (getSubjectCount() == 0) {
            insertSubjects(subjects)
            insertChapters(chapters)
            insertQuestions(questions)
        }
    }

    @Query("UPDATE chapters SET isCompleted = :completed WHERE id = :chapterId")
    suspend fun updateChapterCompletion(chapterId: Long, completed: Boolean)

    @Transaction
    @Query("SELECT * FROM subjects ORDER BY id")
    fun observeSubjectsWithChapters(): Flow<List<SubjectWithChapters>>

    @Query("SELECT * FROM quiz_attempts ORDER BY completedAt DESC")
    fun observeQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM questions WHERE chapterId = :chapterId ORDER BY id")
    suspend fun getQuestionsForChapter(chapterId: Long): List<QuestionEntity>

    @Query("SELECT * FROM chapters WHERE id = :chapterId LIMIT 1")
    suspend fun getChapter(chapterId: Long): ChapterEntity?

    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    suspend fun getSubject(subjectId: Long): SubjectEntity?

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectCount(): Int
}
