package com.aksharadeepa.tutor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aksharadeepa.tutor.AppContainer
import com.aksharadeepa.tutor.data.local.entity.QuestionEntity
import com.aksharadeepa.tutor.data.repository.SettingsRepository
import com.aksharadeepa.tutor.data.repository.TutorRepository
import com.aksharadeepa.tutor.domain.model.ReviewAnswer
import com.aksharadeepa.tutor.domain.model.SubjectProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

private const val QuizSeconds = 120

data class DashboardUiState(
    val subjects: List<SubjectProgress> = emptyList(),
    val dailyGoalCompleted: Boolean = false
) {
    val overallProgress: Float
        get() {
            val total = subjects.sumOf { it.totalChapters }
            return if (total == 0) 0f else subjects.sumOf { it.completedChapters } * 100f / total
        }

    val gapAreas: List<SubjectProgress>
        get() = subjects.filter { it.isWeak }
}

data class QuizUiState(
    val subjectId: Long = 0,
    val chapterId: Long = 0,
    val chapterTitle: String = "",
    val questions: List<QuestionEntity> = emptyList(),
    val selectedAnswers: Map<Long, Int> = emptyMap(),
    val secondsLeft: Int = QuizSeconds,
    val submitted: Boolean = false,
    val score: Int = 0,
    val reviewAnswers: List<ReviewAnswer> = emptyList()
) {
    val isLoaded: Boolean get() = questions.isNotEmpty()
    val totalQuestions: Int get() = questions.size
}

class TutorViewModel(
    private val tutorRepository: TutorRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val dashboardUiState: StateFlow<DashboardUiState> =
        combine(
            tutorRepository.subjectProgress,
            settingsRepository.dailyGoalCompleted
        ) { subjects, dailyGoalCompleted ->
            DashboardUiState(subjects = subjects, dailyGoalCompleted = dailyGoalCompleted)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState: StateFlow<QuizUiState> = _quizUiState

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            tutorRepository.seedInitialData()
        }
    }

    fun setChapterCompleted(chapterId: Long, completed: Boolean) {
        viewModelScope.launch {
            tutorRepository.setChapterCompleted(chapterId, completed)
            if (completed) {
                settingsRepository.recordChapterCompletedToday()
            }
        }
    }

    fun startQuiz(subjectId: Long, chapterId: Long, chapterTitle: String) {
        timerJob?.cancel()
        viewModelScope.launch {
            val questions = tutorRepository.getQuestionsForChapter(chapterId)
            _quizUiState.value = QuizUiState(
                subjectId = subjectId,
                chapterId = chapterId,
                chapterTitle = chapterTitle,
                questions = questions
            )
            startTimer()
        }
    }

    fun selectAnswer(questionId: Long, answerIndex: Int) {
        if (_quizUiState.value.submitted) return
        _quizUiState.update { state ->
            state.copy(selectedAnswers = state.selectedAnswers + (questionId to answerIndex))
        }
    }

    fun submitQuiz() {
        val state = _quizUiState.value
        if (state.submitted || state.questions.isEmpty()) return

        timerJob?.cancel()
        val score = state.questions.count { question ->
            state.selectedAnswers[question.id] == question.correctOptionIndex
        }
        val review = state.questions.map { question ->
            val selectedIndex = state.selectedAnswers[question.id]
            ReviewAnswer(
                questionText = question.questionText,
                selectedOption = selectedIndex?.let { question.optionAt(it) },
                correctOption = question.optionAt(question.correctOptionIndex),
                isCorrect = selectedIndex == question.correctOptionIndex
            )
        }
        _quizUiState.value = state.copy(
            submitted = true,
            score = score,
            reviewAnswers = review
        )
        viewModelScope.launch {
            tutorRepository.saveQuizAttempt(
                subjectId = state.subjectId,
                chapterId = state.chapterId,
                score = score,
                totalQuestions = state.questions.size
            )
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_quizUiState.value.secondsLeft > 0 && !_quizUiState.value.submitted) {
                delay(1_000)
                _quizUiState.update { state -> state.copy(secondsLeft = state.secondsLeft - 1) }
            }
            if (!_quizUiState.value.submitted) {
                submitQuiz()
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

class TutorViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TutorViewModel(
            tutorRepository = container.tutorRepository,
            settingsRepository = container.settingsRepository
        ) as T
    }
}

fun QuestionEntity.optionAt(index: Int): String {
    return when (index) {
        0 -> optionA
        1 -> optionB
        2 -> optionC
        else -> optionD
    }
}
