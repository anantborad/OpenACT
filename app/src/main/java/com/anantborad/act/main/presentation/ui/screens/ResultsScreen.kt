package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.main.presentation.ui.components.ActButton
import com.anantborad.act.main.presentation.viewmodels.ResultsViewModel

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel,
    subject: String,
    score: Int,
    total: Int,
    timeSeconds: Long,
    onHomeClick: () -> Unit = {},
    onReviewClick: () -> Unit = {}
) {
    androidx.compose.runtime.LaunchedEffect(subject, score, total, timeSeconds) {
        viewModel.setResults(subject, score, total, timeSeconds)
    }

    val timeString = remember(timeSeconds) {
        val mins = timeSeconds / 60
        val secs = timeSeconds % 60
        String.format(Locale.getDefault(), "%d:%02d", mins, secs)
    }
    val accuracy = if (total > 0) (score * 100) / total else 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MathBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MathBlue,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${subject.lowercase().replaceFirstChar { it.uppercase() }} Complete!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Great job! You answered $score out of $total questions correctly.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStat(label = "Accuracy", value = "$accuracy%")
                    ResultStat(label = "Time", value = timeString)
                    ResultStat(label = "XP Gained", value = "+${score * 20}")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            ActButton(
                text = "Review Answers",
                onClick = onReviewClick,
                containerColor = MathBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MathBlue)
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = MathBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Back to Home", color = MathBlue)
            }
        }
    }
}

@Composable
fun ResultStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MathBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}
