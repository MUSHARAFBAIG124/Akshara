package com.aksharadeepa.tutor.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aksharadeepa.tutor.domain.model.SubjectProgress
import com.aksharadeepa.tutor.presentation.viewmodel.DashboardUiState
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModel

@Composable
fun OverviewScreen(viewModel: TutorViewModel) {
    val state by viewModel.dashboardUiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        item { Header(state) }
        item { ProgressCard(state) }
        item { DailyGoalCard(state) }
        item { GapAreaCard(state) }
        item { SubjectSummary(state.subjects) }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun Header(state: DashboardUiState) {
    val totalChapters = state.subjects.sumOf { it.totalChapters }
    val completedChapters = state.subjects.sumOf { it.completedChapters }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Akshara-Deepa Tutor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$completedChapters of $totalChapters chapters completed",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProgressCard(state: DashboardUiState) {
    SimpleCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Overall progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${state.overallProgress.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val progress by animateFloatAsState(
                targetValue = state.overallProgress / 100f,
                animationSpec = tween(500),
                label = "overallProgress"
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DailyGoalCard(state: DashboardUiState) {
    SimpleCard(
        containerColor = if (state.dailyGoalCompleted) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (state.dailyGoalCompleted) "Daily goal completed" else "Daily goal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (state.dailyGoalCompleted) {
                    "You completed a topic today."
                } else {
                    "Complete one topic today."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GapAreaCard(state: DashboardUiState) {
    val text = when {
        state.subjects.none { it.averageQuizScore != null } -> "Take a quiz to find subjects that need attention."
        state.gapAreas.isEmpty() -> "No weak subjects below 50% right now."
        else -> "Focus on ${state.gapAreas.joinToString { it.subjectName }}."
    }

    SimpleCard {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Focus area",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SubjectSummary(subjects: List<SubjectProgress>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Subjects",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        subjects.forEach { subject ->
            SimpleCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            subject.subjectName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("${subject.completionPercent.toInt()}%", color = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(
                        progress = { subject.completionPercent / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "${subject.completedChapters}/${subject.totalChapters} chapters complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleCard(
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Surface(color = containerColor, modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
