package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anantborad.act.core.ui.theme.BlueprintBlue
import com.anantborad.act.core.ui.theme.EnglishRose
import com.anantborad.act.core.ui.theme.EnglishRoseBg
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.core.ui.theme.ReadingEmerald
import com.anantborad.act.main.data.local.UserSessionEntity
import com.anantborad.act.main.domain.model.SubjectStats
import com.anantborad.act.main.presentation.ui.components.ActHeader
import com.anantborad.act.main.presentation.viewmodels.StatsTimeframe
import com.anantborad.act.main.presentation.viewmodels.StatsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.userStats
    val recentSessions = uiState.recentSessions

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ActHeader(
                title = "Performance Stats",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Timeframe Scope Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsTimeframe.values().forEach { timeframe ->
                    FilterChip(
                        selected = uiState.selectedTimeframe == timeframe,
                        onClick = { viewModel.setTimeframe(timeframe) },
                        label = { Text(timeframe.label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueprintBlue.copy(alpha = 0.15f),
                            selectedLabelColor = BlueprintBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (stats != null) {
                // Balanced Overall Accuracy Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    ),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = BlueprintBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "GOAL: ${stats.goalActScore} ACT (${stats.targetAccuracy}% TARGET)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BlueprintBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Overall Accuracy",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Based on ${stats.totalQuestions} questions answered (${uiState.selectedTimeframe.label})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Circular Gauge Ring
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(72.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { stats.accuracy.toFloat() / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = if (stats.accuracy >= stats.targetAccuracy) ReadingEmerald else BlueprintBlue,
                                strokeWidth = 7.dp,
                                trackColor = BlueprintBlue.copy(alpha = 0.12f),
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                text = "${stats.accuracy}%",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Accuracy Over Time Chart
                Text(
                    text = "Accuracy Over Time",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AccuracyOverTimeCard(
                    sessions = recentSessions,
                    targetAccuracy = stats.targetAccuracy,
                    goalActScore = stats.goalActScore
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Subject Performance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    stats.subjects.forEach { subjectStat ->
                        val color = when (subjectStat.name) {
                            "English" -> EnglishRose
                            "Math" -> MathBlue
                            else -> ReadingEmerald
                        }
                        SubjectStatCard(subjectStat, color, targetAccuracy = stats.targetAccuracy)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Weakest Skills",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (stats.weakSkills.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No weak skills identified yet. Complete practice sessions to see your focus areas!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stats.weakSkills.forEach { skill ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                                ),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = skill.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = skill.subject,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Surface(
                                        color = EnglishRoseBg,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${skill.accuracy}% Acc",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = EnglishRose,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Recent Practice Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (recentSessions.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No practice sessions recorded yet. Complete a practice test to view detailed session logs!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        recentSessions.forEach { session ->
                            val dateStr = remember(session.timestamp) {
                                SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(session.timestamp))
                            }
                            val acc = if (session.totalQuestions > 0) (session.score * 100) / session.totalQuestions else 0
                            val mins = session.timeSeconds / 60
                            val secs = session.timeSeconds % 60
                            val timeStr = "${mins}m ${if (secs < 10) "0$secs" else "$secs"}s"
                            val subjectColor = when (session.subject.uppercase()) {
                                "ENGLISH" -> EnglishRose
                                "MATH" -> MathBlue
                                else -> ReadingEmerald
                            }
                            val subjectName = session.subject.lowercase().replaceFirstChar { it.uppercase() }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = subjectColor.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = subjectName,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = subjectColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = session.difficulty,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "$dateStr • $timeStr",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${session.score}/${session.totalQuestions} Correct",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$acc% Acc",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (acc >= stats.targetAccuracy) ReadingEmerald else BlueprintBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AccuracyOverTimeCard(
    sessions: List<UserSessionEntity>,
    targetAccuracy: Int,
    goalActScore: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Sessions Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp, 2.dp)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Target: $targetAccuracy% ($goalActScore ACT)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Complete practice sessions to visualize accuracy trends",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val displaySessions = sessions.take(8).reversed()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    // Y-Axis Scale Labels Column
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("100%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("75%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("50%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("25%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("0%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Canvas & X-Axis Row
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val width = size.width
                                val height = size.height

                                // Draw 5 horizontal dashed grid lines
                                val gridLevels = listOf(0f, 0.25f, 0.50f, 0.75f, 1f)
                                gridLevels.forEach { level ->
                                    val y = height * (1f - level)
                                    drawLine(
                                        color = Color.LightGray.copy(alpha = 0.5f),
                                        start = Offset(0f, y),
                                        end = Offset(width, y),
                                        strokeWidth = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                    )
                                }

                                // Red Target Dashed Line
                                val targetY = height * (1f - (targetAccuracy / 100f))
                                drawLine(
                                    color = Color.Red.copy(alpha = 0.8f),
                                    start = Offset(0f, targetY),
                                    end = Offset(width, targetY),
                                    strokeWidth = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                )

                                // Calculate Points for Line Chart
                                val count = displaySessions.size
                                val points = displaySessions.mapIndexed { index, session ->
                                    val acc = if (session.totalQuestions > 0) (session.score * 100f) / session.totalQuestions else 0f
                                    val x = if (count <= 1) width / 2f else (index.toFloat() / (count - 1)) * width
                                    val y = height * (1f - (acc / 100f)).coerceIn(0f, 1f)
                                    Triple(Offset(x, y), acc, session)
                                }

                                if (points.size > 1) {
                                    // Filled Area Under Line
                                    val fillPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(points.first().first.x, height)
                                        points.forEach { lineTo(it.first.x, it.first.y) }
                                        lineTo(points.last().first.x, height)
                                        close()
                                    }
                                    drawPath(
                                        path = fillPath,
                                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(BlueprintBlue.copy(alpha = 0.25f), Color.Transparent),
                                            startY = 0f,
                                            endY = height
                                        )
                                    )

                                    // Line Chart Path
                                    val linePath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(points.first().first.x, points.first().first.y)
                                        for (i in 1 until points.size) {
                                            lineTo(points[i].first.x, points[i].first.y)
                                        }
                                    }
                                    drawPath(
                                        path = linePath,
                                        color = BlueprintBlue,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 3.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }

                                // Draw Dots (Data Points)
                                points.forEach { (point, acc, _) ->
                                    val dotColor = if (acc >= targetAccuracy) ReadingEmerald else BlueprintBlue
                                    drawCircle(
                                        color = Color.White,
                                        radius = 7.dp.toPx(),
                                        center = point
                                    )
                                    drawCircle(
                                        color = dotColor,
                                        radius = 5.dp.toPx(),
                                        center = point
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // X-Axis Labels below chart
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            displaySessions.forEach { session ->
                                val label = when (session.subject.uppercase()) {
                                    "ENGLISH" -> "E"
                                    "MATH" -> "M"
                                    "READING" -> "R"
                                    else -> session.subject.take(1)
                                }
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectStatCard(subjectStat: SubjectStats, color: Color, targetAccuracy: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        ),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = subjectStat.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (subjectStat.hasPracticed) "${subjectStat.accuracy}% Acc" else "-- (Not Practiced)",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { if (subjectStat.hasPracticed) subjectStat.accuracy.toFloat() / 100f else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
