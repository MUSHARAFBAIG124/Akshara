package com.aksharadeepa.tutor.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aksharadeepa.tutor.data.local.entity.QuestionEntity
import com.aksharadeepa.tutor.domain.model.ReviewAnswer
import com.aksharadeepa.tutor.presentation.viewmodel.QuizUiState
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModel
import com.aksharadeepa.tutor.presentation.viewmodel.optionAt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: TutorViewModel,
    subjectId: Long,
    chapterId: Long,
    chapterTitle: String,
    onBack: () -> Unit
) {
    val state by viewModel.quizUiState.collectAsState()

    LaunchedEffect(subjectId, chapterId, chapterTitle) {
        viewModel.startQuiz(subjectId, chapterId, chapterTitle)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Self-Check Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (!state.isLoaded) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading offline quiz...")
            }
        } else if (state.submitted) {
            ReviewContent(
                state = state,
                onBack = onBack,
                modifier = Modifier.padding(padding)
            )
        } else {
            QuizContent(
                state = state,
                onSelectAnswer = viewModel::selectAnswer,
                onSubmit = viewModel::submitQuiz,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun QuizContent(
    state: QuizUiState,
    onSelectAnswer: (Long, Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            QuizHeader(state)
        }
        items(state.questions, key = { it.id }) { question ->
            QuestionCard(
                question = question,
                selectedIndex = state.selectedAnswers[question.id],
                onSelectAnswer = { onSelectAnswer(question.id, it) }
            )
        }
        item {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit and review answers")
            }
        }
    }
}

@Composable
private fun QuizHeader(state: QuizUiState) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(state.chapterTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Text(
                        text = " ${state.secondsLeft / 60}:${(state.secondsLeft % 60).toString().padStart(2, '0')}",
                        fontWeight = FontWeight.Bold
                    )
                }
                Text("${state.selectedAnswers.size}/${state.totalQuestions} answered")
            }
            val progress by animateFloatAsState(
                targetValue = state.secondsLeft / 120f,
                animationSpec = tween(1000)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuestionCard(
    question: QuestionEntity,
    selectedIndex: Int?,
    onSelectAnswer: (Int) -> Unit
) {
    ElevatedCard(modifier = Modifier.animateContentSize()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(question.questionText, fontWeight = FontWeight.Medium)
            (0..3).forEach { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedIndex == index,
                        onClick = { onSelectAnswer(index) }
                    )
                    Text(question.optionAt(index))
                }
            }
        }
    }
}

@Composable
private fun ReviewContent(
    state: QuizUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (state.totalQuestions == 0) 0 else state.score * 100 / state.totalQuestions
    val resultLabel = when {
        percentage >= 80 -> "Excellent"
        percentage >= 60 -> "Good"
        percentage >= 40 -> "Needs practice"
        else -> "Revise and retry"
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ElevatedCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Quiz Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(state.chapterTitle, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "$resultLabel - $percentage%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Score: ${state.score}/${state.totalQuestions} correct")
                    Text("Answered: ${state.selectedAnswers.size}/${state.totalQuestions}")
                    Text("Strength Map updates from this score automatically.")
                }
            }
        }
        items(state.reviewAnswers) { answer ->
            ReviewAnswerCard(answer)
        }
        item {
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Mission Map")
            }
        }
    }
}

@Composable
private fun ReviewAnswerCard(answer: ReviewAnswer) {
    val color = if (answer.isCorrect) Color(0xFFD8F2DD) else Color(0xFFFFE1DE)
    ElevatedCard(colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (answer.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (answer.isCorrect) Color(0xFF287D3C) else Color(0xFFB3261E)
                )
                Text(
                    text = if (answer.isCorrect) "Correct" else "Needs revision",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(answer.questionText, fontWeight = FontWeight.Medium)
            Text("Your answer: ${answer.selectedOption ?: "Not answered"}")
            Text("Correct answer: ${answer.correctOption}")
        }
    }
}
