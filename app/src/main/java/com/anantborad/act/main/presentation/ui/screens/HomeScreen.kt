package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anantborad.act.core.ui.theme.EnglishRose
import com.anantborad.act.core.ui.theme.MathBlue
import com.anantborad.act.core.ui.theme.ReadingEmerald
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.components.ActGradientButton
import com.anantborad.act.main.presentation.ui.components.ActHeader
import com.anantborad.act.main.presentation.ui.components.ActStatCard
import com.anantborad.act.main.presentation.ui.components.ActSubjectCard
import com.anantborad.act.main.presentation.util.MOCK_USER_STATS

@Composable
fun HomeScreen(
    onQuickStartClick: () -> Unit = {},
    onSubjectClick: (PracticeSubject) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
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
                title = MOCK_USER_STATS.name,
                subtitle = "Welcome back",
                avatarUrl = "https://ui-avatars.com/api/?name=${MOCK_USER_STATS.name}&background=random",
                onAvatarClick = onProfileClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActStatCard(
                    label = "Day Streak",
                    value = MOCK_USER_STATS.streak.toString(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    iconBgColor = Color(0xFFFFEDD5),
                    modifier = Modifier.weight(1f)
                )
                ActStatCard(
                    label = "Daily Goal",
                    value = MOCK_USER_STATS.dailyGoal,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Adjust,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    iconBgColor = Color(0xFFDBEAFE),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            ActGradientButton(
                onClick = onQuickStartClick,
                title = "Quick Start",
                subtitle = "3-minute mixed practice",
                icon = {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Focus Areas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllClick) {
                    Text(text = "View All")
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MOCK_USER_STATS.subjects.forEach { subjectStat ->
                        val (subject, color) = when (subjectStat.name) {
                            "English" -> PracticeSubject.ENGLISH to EnglishRose
                            "Math" -> PracticeSubject.MATH to MathBlue
                            else -> PracticeSubject.READING to ReadingEmerald
                        }

                        ActSubjectCard(
                            subjectName = subjectStat.name,
                            accuracy = subjectStat.accuracy,
                            subjectColor = color,
                            onClick = { onSubjectClick(subject) }
                        )
                    }
                }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
