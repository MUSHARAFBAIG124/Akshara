package com.aksharadeepa.tutor.domain.model

data class ChapterProgress(
    val id: Long,
    val subjectId: Long,
    val title: String,
    val isCompleted: Boolean,
    val latestQuizScore: Float?
)

data class SubjectProgress(
    val subjectId: Long,
    val subjectName: String,
    val chapters: List<ChapterProgress>,
    val completedChapters: Int,
    val totalChapters: Int,
    val completionPercent: Float,
    val averageQuizScore: Float?
) {
    val isWeak: Boolean
        get() = averageQuizScore?.let { it < 50f } ?: false
}

data class ReviewAnswer(
    val questionText: String,
    val selectedOption: String?,
    val correctOption: String,
    val isCorrect: Boolean
)
