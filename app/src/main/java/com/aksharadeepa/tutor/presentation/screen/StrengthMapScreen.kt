package com.aksharadeepa.tutor.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aksharadeepa.tutor.domain.model.SubjectProgress
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun StrengthMapScreen(viewModel: TutorViewModel) {
    val state by viewModel.dashboardUiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Strength Map",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Spider web view of quiz strength by subject.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item { SpiderWebCard(state.subjects) }
        items(state.subjects, key = { it.subjectId }) { subject ->
            SubjectScoreCard(subject)
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SpiderWebCard(subjects: List<SubjectProgress>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Spider web",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            StrengthSpiderWeb(
                subjects = subjects,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
            Text(
                "Higher points near the outer web mean stronger quiz performance.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StrengthSpiderWeb(
    subjects: List<SubjectProgress>,
    modifier: Modifier = Modifier
) {
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.36f)
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        if (subjects.size < 3) return@Canvas

        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * 0.38f
        val count = subjects.size

        for (ring in 1..4) {
            val ringPath = Path()
            val ringRadius = radius * ring / 4f
            repeat(count) { index ->
                val angle = -PI / 2 + 2 * PI * index / count
                val point = Offset(
                    x = center.x + cos(angle).toFloat() * ringRadius,
                    y = center.y + sin(angle).toFloat() * ringRadius
                )
                if (index == 0) {
                    ringPath.moveTo(point.x, point.y)
                } else {
                    ringPath.lineTo(point.x, point.y)
                }
            }
            ringPath.close()
            drawPath(ringPath, gridColor, style = Stroke(width = 1.dp.toPx()))
        }

        val strengthPath = Path()
        subjects.forEachIndexed { index, subject ->
            val score = ((subject.averageQuizScore ?: subject.completionPercent) / 100f)
                .coerceIn(0f, 1f)
            val angle = -PI / 2 + 2 * PI * index / count
            val edge = Offset(
                x = center.x + cos(angle).toFloat() * radius,
                y = center.y + sin(angle).toFloat() * radius
            )
            val point = Offset(
                x = center.x + cos(angle).toFloat() * radius * score,
                y = center.y + sin(angle).toFloat() * radius * score
            )

            drawLine(axisColor, center, edge, strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(lineColor, radius = 4.dp.toPx(), center = point)

            if (index == 0) {
                strengthPath.moveTo(point.x, point.y)
            } else {
                strengthPath.lineTo(point.x, point.y)
            }
        }

        strengthPath.close()
        drawPath(strengthPath, fillColor)
        drawPath(strengthPath, lineColor, style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
private fun SubjectScoreCard(subject: SubjectProgress) {
    val score = subject.averageQuizScore ?: subject.completionPercent
    val progress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(500),
        label = "scoreProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                Text(
                    "${score.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (subject.averageQuizScore != null) {
                    "Quiz average: ${subject.averageQuizScore.toInt()}%"
                } else {
                    "No quiz yet. Showing chapter completion."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
