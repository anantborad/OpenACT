package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anantborad.act.core.ui.theme.EnglishRose
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.core.ui.theme.ReadingEmerald
import com.anantborad.act.core.ui.theme.SciencePurple
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.components.ActButton
import com.anantborad.act.main.presentation.ui.components.ActHeader
import com.anantborad.act.main.presentation.viewmodels.SetupViewModel

@Composable
fun SetupScreen(
    viewModel: SetupViewModel,
    subject: PracticeSubject,
    onStartClick: (count: Int, difficulty: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    var questionCount by remember { mutableStateOf(10f) }
    var difficulty by remember { mutableStateOf("Mixed") }

    val subjectColor = when (subject) {
        PracticeSubject.ENGLISH -> EnglishRose
        PracticeSubject.MATH -> MathBlue
        PracticeSubject.READING -> ReadingEmerald
        PracticeSubject.SCIENCE -> SciencePurple
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                ActButton(
                    text = "Start Practice",
                    onClick = { onStartClick(questionCount.toInt(), difficulty) },
                    containerColor = subjectColor
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ActHeader(
                title = "${subject.displayName} Setup",
                subtitle = "Practice Configuration",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Number of Questions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${questionCount.toInt()} questions",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = questionCount,
                onValueChange = { questionCount = it },
                valueRange = 5f..30f,
                steps = 4,
                colors = SliderDefaults.colors(
                    thumbColor = subjectColor,
                    activeTrackColor = subjectColor
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Difficulty Level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Easy", "Mixed", "Hard").forEach { level ->
                    FilterChip(
                        selected = difficulty == level,
                        onClick = { difficulty = level },
                        label = { Text(level) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = subjectColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Topics included",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "All sub-topics in ${subject.displayName} will be included in this session for balanced practice.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
