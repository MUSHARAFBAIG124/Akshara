package com.aksharadeepa.tutor.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aksharadeepa.tutor.data.local.LessonSummaries
import com.aksharadeepa.tutor.domain.model.ChapterProgress
import com.aksharadeepa.tutor.domain.model.SubjectProgress
import com.aksharadeepa.tutor.presentation.util.openLessonPdf
import com.aksharadeepa.tutor.presentation.util.rememberOfflineAudioSummaryPlayer
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModel

@Composable
fun SubjectsScreen(
    viewModel: TutorViewModel,
    onStartQuiz: (Long, Long, String) -> Unit
) {
    val state by viewModel.dashboardUiState.collectAsState()
    val audioSummaryPlayer = rememberOfflineAudioSummaryPlayer()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        item {
            Text(
                "Subjects",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        items(state.subjects, key = { it.subjectId }) { subject ->
            SubjectTreeCard(
                subject = subject,
                onChapterCompleted = viewModel::setChapterCompleted,
                onStartQuiz = onStartQuiz,
                speakingChapterId = audioSummaryPlayer.speakingChapterId,
                onPlayAudioSummary = { chapter ->
                    audioSummaryPlayer.speak(
                        chapter.id,
                        chapter.title,
                        LessonSummaries.forChapter(chapter.id)
                    )
                },
                onStopAudioSummary = audioSummaryPlayer::stop
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SubjectTreeCard(
    subject: SubjectProgress,
    onChapterCompleted: (Long, Boolean) -> Unit,
    onStartQuiz: (Long, Long, String) -> Unit,
    speakingChapterId: Long,
    onPlayAudioSummary: (ChapterProgress) -> Unit,
    onStopAudioSummary: () -> Unit
) {
    val expandedSubjects = remember { mutableStateMapOf<Long, Boolean>() }
    val expanded = expandedSubjects[subject.subjectId] ?: true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { expandedSubjects[subject.subjectId] = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        subject.subjectName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${subject.completedChapters}/${subject.totalChapters} chapters complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${subject.completionPercent.toInt()}%",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            val progress by animateFloatAsState(
                targetValue = subject.completionPercent / 100f,
                animationSpec = tween(500),
                label = "subjectProgress"
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    subject.chapters.forEach { chapter ->
                        ChapterRow(
                            subjectId = subject.subjectId,
                            chapter = chapter,
                            onChapterCompleted = onChapterCompleted,
                            onStartQuiz = onStartQuiz,
                            isAudioPlaying = speakingChapterId == chapter.id,
                            onPlayAudioSummary = { onPlayAudioSummary(chapter) },
                            onStopAudioSummary = onStopAudioSummary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChapterRow(
    subjectId: Long,
    chapter: ChapterProgress,
    onChapterCompleted: (Long, Boolean) -> Unit,
    onStartQuiz: (Long, Long, String) -> Unit,
    isAudioPlaying: Boolean,
    onPlayAudioSummary: () -> Unit,
    onStopAudioSummary: () -> Unit
) {
    val context = LocalContext.current
    val summary = LessonSummaries.forChapter(chapter.id)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = chapter.isCompleted,
                    onCheckedChange = { onChapterCompleted(chapter.id, it) }
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        chapter.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Latest quiz: ${chapter.latestQuizScore?.toInt()?.toString() ?: "not taken"}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { onStartQuiz(subjectId, chapter.id, chapter.title) }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text("Quiz")
                }
                OutlinedButton(onClick = { openLessonPdf(context, chapter.id, chapter.title) }) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text("Textbook")
                }
                OutlinedButton(
                    onClick = {
                        if (isAudioPlaying) onStopAudioSummary() else onPlayAudioSummary()
                    }
                ) {
                    Icon(
                        imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(if (isAudioPlaying) "Stop" else "Audio")
                }
                OutlinedButton(onClick = { onChapterCompleted(chapter.id, !chapter.isCompleted) }) {
                    Text(if (chapter.isCompleted) "Pending" else "Done")
                }
            }
        }
    }
}
