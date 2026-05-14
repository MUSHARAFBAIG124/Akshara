package com.aksharadeepa.tutor.data.repository

import com.aksharadeepa.tutor.data.local.SampleData
import com.aksharadeepa.tutor.data.local.dao.TutorDao
import com.aksharadeepa.tutor.data.local.entity.QuestionEntity
import com.aksharadeepa.tutor.data.local.entity.QuizAttemptEntity
import com.aksharadeepa.tutor.domain.model.ChapterProgress
import com.aksharadeepa.tutor.domain.model.SubjectProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TutorRepository(private val dao: TutorDao) {
    val subjectProgress: Flow<List<SubjectProgress>> =
        combine(
            dao.observeSubjectsWithChapters(),
            dao.observeQuizAttempts()
        ) { subjects, attempts ->
            subjects.map { subjectWithChapters ->
                val subject = subjectWithChapters.subject
                val chapters = subjectWithChapters.chapters.sortedBy { it.id }
                val chapterProgress = chapters.map { chapter ->
                    val latestAttempt = attempts.firstOrNull { it.chapterId == chapter.id }
                    ChapterProgress(
                        id = chapter.id,
                        subjectId = chapter.subjectId,
                        title = chapter.title,
                        isCompleted = chapter.isCompleted,
                        latestQuizScore = latestAttempt?.percentScore()
                    )
                }
                val completed = chapterProgress.count { it.isCompleted }
                val subjectAttempts = attempts.filter { it.subjectId == subject.id }
                val averageScore = subjectAttempts
                    .takeIf { it.isNotEmpty() }
                    ?.map { it.percentScore() }
                    ?.average()
                    ?.toFloat()

                SubjectProgress(
                    subjectId = subject.id,
                    subjectName = subject.name,
                    chapters = chapterProgress,
                    completedChapters = completed,
                    totalChapters = chapters.size,
                    completionPercent = if (chapters.isEmpty()) 0f else completed * 100f / chapters.size,
                    averageQuizScore = averageScore
                )
            }
        }

    suspend fun seedInitialData() {
        dao.seedInitialDataIfEmpty(
            subjects = SampleData.subjects,
            chapters = SampleData.chapters,
            questions = SampleData.questions
        )
    }

    suspend fun setChapterCompleted(chapterId: Long, completed: Boolean) {
        dao.updateChapterCompletion(chapterId, completed)
    }

    suspend fun getQuestionsForChapter(chapterId: Long): List<QuestionEntity> {
        return dao.getQuestionsForChapter(chapterId).take(5)
    }

    suspend fun saveQuizAttempt(subjectId: Long, chapterId: Long, score: Int, totalQuestions: Int) {
        dao.insertQuizAttempt(
            QuizAttemptEntity(
                subjectId = subjectId,
                chapterId = chapterId,
                score = score,
                totalQuestions = totalQuestions,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    private fun QuizAttemptEntity.percentScore(): Float {
        return if (totalQuestions == 0) 0f else score * 100f / totalQuestions
    }
}
