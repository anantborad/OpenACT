package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.components.*
import com.anantborad.act.main.presentation.viewmodels.SessionViewModel

@Composable
fun SessionScreen(
    viewModel: SessionViewModel,
    subject: PracticeSubject,
    questionCount: Int = 10,
    difficulty: String = "Mixed",
    onFinishClick: (subject: String, score: Int, total: Int, timeSeconds: Long) -> Unit = { _, _, _, _ -> },
    onCloseClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(subject) {
        viewModel.startSession(subject, questionCount)
    }

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onFinishClick(
                subject.name,
                uiState.score,
                uiState.questions.size,
                uiState.timeElapsedSeconds
            )
        }
    }

    val question = uiState.currentQuestion
    val progress = uiState.progress

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = if (uiState.questions.isNotEmpty()) {
                            "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}"
                        } else "Loading...",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MathBlue,
                    trackColor = MathBlue.copy(alpha = 0.1f)
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    ActButton(
                        text = if (uiState.currentQuestionIndex == uiState.questions.size - 1) "Finish Session" else "Next Question",
                        onClick = { viewModel.onNextClick() },
                        enabled = uiState.selectedOptionId != null,
                        containerColor = MathBlue
                    )
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (question != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (question.passage != null) {
                    ActPassageCard(passage = question.passage)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (question.subject == PracticeSubject.MATH) {
                    ActMathText(latex = question.text)
                } else {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    question.options.forEach { option ->
                        ActChoiceChip(
                            text = option.text,
                            label = option.label,
                            isSelected = uiState.selectedOptionId == option.id,
                            onClick = { viewModel.onOptionSelected(option.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
