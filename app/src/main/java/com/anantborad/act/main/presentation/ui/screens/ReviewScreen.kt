package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.components.ActChoiceChip
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState.questions.isNotEmpty()) {
                            "Review ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}"
                        } else "Review",
                        fontWeight = FontWeight.Bold
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
                            Text("Previous")
                        }
                        TextButton(
                            onClick = { viewModel.nextQuestion() },
                            enabled = uiState.currentQuestionIndex < uiState.questions.size - 1
                        ) {
                            Text("Next")
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
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    question.options.forEach { option ->
                        val isCorrect = option.id == question.correctOptionId
                        // We don't have the user's specific choice saved in the review state yet for simplicity,
                        // but we can at least show the correct one.
                        ActChoiceChip(
                            text = option.text,
                            label = option.label,
                            isSelected = isCorrect,
                            onClick = {},
                            useMath = question.subject == PracticeSubject.MATH,
                            modifier = Modifier.graphicsLayer(alpha = if (isCorrect) 1f else 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MathBlue.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MathBlue.copy(alpha = 0.1f)
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
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
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
                    Text("No questions to review")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
