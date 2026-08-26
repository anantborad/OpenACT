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
import com.anantborad.act.main.presentation.util.MOCK_QUESTIONS

@Composable
fun SessionScreen(
    onFinishClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    
    val question = MOCK_QUESTIONS[currentQuestionIndex % MOCK_QUESTIONS.size]
    val progress = (currentQuestionIndex + 1).toFloat() / MOCK_QUESTIONS.size

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
                        text = "Question ${currentQuestionIndex + 1} of ${MOCK_QUESTIONS.size}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // To center the text
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
                        text = if (currentQuestionIndex == MOCK_QUESTIONS.size - 1) "Finish Session" else "Next Question",
                        onClick = {
                            if (currentQuestionIndex < MOCK_QUESTIONS.size - 1) {
                                currentQuestionIndex++
                                selectedOptionId = null
                            } else {
                                onFinishClick()
                            }
                        },
                        enabled = selectedOptionId != null,
                        containerColor = MathBlue
                    )
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
                        isSelected = selectedOptionId == option.id,
                        onClick = { selectedOptionId = option.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
