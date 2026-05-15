package com.aksharadeepa.tutor.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aksharadeepa.tutor.data.local.LessonSummaries
import com.aksharadeepa.tutor.domain.model.ChapterProgress
import com.aksharadeepa.tutor.domain.model.SubjectProgress
import com.aksharadeepa.tutor.presentation.viewmodel.DashboardUiState
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModel
import com.aksharadeepa.tutor.presentation.util.openLessonPdf
import com.aksharadeepa.tutor.presentation.util.rememberOfflineAudioSummaryPlayer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: TutorViewModel,
    onStartQuiz: (Long, Long, String) -> Unit
) {
    val state by viewModel.dashboardUiState.collectAsState()
    val audioSummaryPlayer = rememberOfflineAudioSummaryPlayer()
    
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Overview", "Strength Map", "Subjects")

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            when (page) {
                0 -> OverviewPage(state)
                1 -> StrengthMapPage(state.subjects)
                2 -> SubjectsPage(
                    state = state,
                    onStartQuiz = onStartQuiz,
                    viewModel = viewModel,
                    audioSummaryPlayer = audioSummaryPlayer
                )
            }
        }
    }
}

@Composable
private fun OverviewPage(state: DashboardUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Header(state) }
        item { GapAreaCard(state) }
    }
}

@Composable
private fun StrengthMapPage(subjects: List<SubjectProgress>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { StrengthMapCard(subjects) }
    }
}

@Composable
private fun SubjectsPage(
    state: DashboardUiState,
    onStartQuiz: (Long, Long, String) -> Unit,
    viewModel: TutorViewModel,
    audioSummaryPlayer: com.aksharadeepa.tutor.presentation.util.OfflineAudioSummaryPlayer
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(state.subjects, key = { _, it -> it.subjectId }) { index, subject ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(index * 150L)
                visible = true
            }
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(),
                exit = fadeOut()
            ) {
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
        }
    }
}

@Composable
private fun Header(state: DashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Akshara-Deepa Tutor",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Mission Map for SSLC self-study",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val progress by animateFloatAsState(
            targetValue = state.overallProgress / 100f,
            animationSpec = tween(1000)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Overall progress: ${state.overallProgress.toInt()}%")
            Surface(
                color = if (state.dailyGoalCompleted) Color(0xFFD8F2DD) else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (state.dailyGoalCompleted) "Daily goal done" else "Daily goal: 1 topic",
                        modifier = Modifier.padding(start = 6.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun StrengthMapCard(subjects: List<SubjectProgress>) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Strength Map", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            RadarChart(subjects = subjects, modifier = Modifier.fillMaxWidth().height(230.dp))
            subjects.forEach { subject ->
                val score = subject.averageQuizScore ?: subject.completionPercent
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(subject.subjectName)
                    Text("${score.toInt()}%")
                }
            }
        }
    }
}

@Composable
private fun RadarChart(subjects: List<SubjectProgress>, modifier: Modifier = Modifier) {
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    val fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    val lineColor = MaterialTheme.colorScheme.primary

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val animProgress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )

    Canvas(modifier = modifier) {
        if (subjects.isEmpty()) return@Canvas
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * 0.38f
        val count = subjects.size

        for (ring in 1..4) {
            val path = Path()
            val ringRadius = radius * ring / 4f
            repeat(count) { index ->
                val angle = -PI / 2 + 2 * PI * index / count
                val point = Offset(
                    x = center.x + cos(angle).toFloat() * ringRadius,
                    y = center.y + sin(angle).toFloat() * ringRadius
                )
                if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            }
            path.close()
            drawPath(path, axisColor, style = Stroke(width = 1.dp.toPx()))
        }

        val strengthPath = Path()
        subjects.forEachIndexed { index, subject ->
            val baseScore = ((subject.averageQuizScore ?: subject.completionPercent) / 100f).coerceIn(0f, 1f)
            val score = baseScore * animProgress
            val angle = -PI / 2 + 2 * PI * index / count
            val point = Offset(
                x = center.x + cos(angle).toFloat() * radius * score,
                y = center.y + sin(angle).toFloat() * radius * score
            )
            val edge = Offset(
                x = center.x + cos(angle).toFloat() * radius,
                y = center.y + sin(angle).toFloat() * radius
            )
            drawLine(axisColor, center, edge, strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round)
            if (index == 0) strengthPath.moveTo(point.x, point.y) else strengthPath.lineTo(point.x, point.y)
        }
        strengthPath.close()
        drawPath(strengthPath, fillColor)
        drawPath(strengthPath, lineColor, style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
private fun GapAreaCard(state: DashboardUiState) {
    val text = when {
        state.subjects.none { it.averageQuizScore != null } -> "Take a quiz to reveal weak subjects."
        state.gapAreas.isEmpty() -> "No weak subjects below 50% right now."
        else -> state.gapAreas.joinToString { it.subjectName }
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Gap Areas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text)
        }
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

    ElevatedCard(modifier = Modifier.animateContentSize()) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { expandedSubjects[subject.subjectId] = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse subject" else "Expand subject"
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(subject.subjectName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${subject.completedChapters}/${subject.totalChapters} chapters complete")
                }
                Text("${subject.completionPercent.toInt()}%")
            }
            val progress by animateFloatAsState(
                targetValue = subject.completionPercent / 100f,
                animationSpec = tween(800)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = chapter.isCompleted,
                    onCheckedChange = { onChapterCompleted(chapter.id, it) }
                )
                Column(Modifier.weight(1f)) {
                    Text(chapter.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                    Text(
                        text = chapter.latestQuizScore?.let { "Latest quiz: ${it.toInt()}%" } ?: "Latest quiz: Not taken",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    imageVector = if (chapter.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (chapter.isCompleted) Color(0xFF287D3C) else MaterialTheme.colorScheme.outline
                )
            }
            Text(summary, style = MaterialTheme.typography.bodySmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { onStartQuiz(subjectId, chapter.id, chapter.title) }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text("5-question quiz")
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
                    Text(if (isAudioPlaying) "Stop audio" else "Audio summary")
                }
                OutlinedButton(onClick = { onChapterCompleted(chapter.id, !chapter.isCompleted) }) {
                    Text(if (chapter.isCompleted) "Mark pending" else "Mark done")
                }
            }
        }
    }
}
