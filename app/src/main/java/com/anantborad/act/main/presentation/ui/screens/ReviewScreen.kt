package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anantborad.act.core.ui.theme.EnglishRose
import com.anantborad.act.core.ui.theme.EnglishRoseBg
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.core.ui.theme.ReadingEmerald
import com.anantborad.act.core.ui.theme.ReadingEmeraldBg
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.components.ActMathText
import com.anantborad.act.main.presentation.ui.components.ActPassageCard
import com.anantborad.act.main.presentation.viewmodels.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val question = uiState.currentQuestion
    val userAnswer = uiState.userAnswer
    val isCorrect = uiState.isCorrect

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState.questions.isNotEmpty()) {
                            "Review ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}"
                        } else "Review",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.questions.size > 1) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { viewModel.previousQuestion() },
                            enabled = uiState.currentQuestionIndex > 0
                        ) {
                            Text("Previous", style = MaterialTheme.typography.labelLarge)
                        }
                        TextButton(
                            onClick = { viewModel.nextQuestion() },
                            enabled = uiState.currentQuestionIndex < uiState.questions.size - 1
                        ) {
                            Text("Next", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
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
            if (question != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // Correct / Incorrect Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect) ReadingEmeraldBg else EnglishRoseBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCorrect) ReadingEmerald.copy(alpha = 0.3f) else EnglishRose.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) ReadingEmerald else EnglishRose,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCorrect) "Correct" else "Incorrect (Your answer: ${userAnswer ?: "None"})",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isCorrect) ReadingEmerald else EnglishRose
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (question.passage != null) {
                    ActPassageCard(
                        passage = question.passage,
                        useMath = question.subject == PracticeSubject.MATH
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (question.subject == PracticeSubject.MATH) {
                    ActMathText(
                        latex = question.text,
                    )
                } else {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    question.options.forEach { option ->
                        val isCorrectOption = option.id == question.correctOptionId
                        val isUserSelection = option.id == userAnswer

                        val containerColor = when {
                            isCorrectOption -> ReadingEmeraldBg
                            isUserSelection -> EnglishRoseBg
                            else -> Color.White
                        }
                        val borderColor = when {
                            isCorrectOption -> ReadingEmerald
                            isUserSelection -> EnglishRose
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        }
                        val labelBgColor = when {
                            isCorrectOption -> ReadingEmerald
                            isUserSelection -> EnglishRose
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val labelTextColor = when {
                            isCorrectOption || isUserSelection -> Color.White
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = containerColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(labelBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCorrectOption) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Correct",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isUserSelection) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Incorrect",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = option.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = labelTextColor
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                if (question.subject == PracticeSubject.MATH) {
                                    ActMathText(
                                        latex = option.text,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 2.dp),
                                        textColor = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(
                                        text = option.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MathBlue.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MathBlue.copy(alpha = 0.12f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MathBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Explanation",
                                style = MaterialTheme.typography.titleMedium,
                                color = MathBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (question.subject == PracticeSubject.MATH) {
                            ActMathText(
                                latex = question.explanation,
                                fontSize = 14
                            )
                        } else {
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No questions to review", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
