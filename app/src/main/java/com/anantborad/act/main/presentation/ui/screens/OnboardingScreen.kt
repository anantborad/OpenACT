package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anantborad.act.core.ui.theme.BlueprintBlue
import com.anantborad.act.main.presentation.ui.components.ActButton
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: String
)

val ONBOARDING_PAGES = listOf(
    OnboardingPage(
        "Master the ACT",
        "Personalized practice sessions designed to help you crush your score goals.",
        "🚀"
    ),
    OnboardingPage(
        "Track Your Progress",
        "Detailed analytics to show you exactly where you need to focus.",
        "📊"
    ),
    OnboardingPage(
        "Daily Motivation",
        "Build a streak and reach your daily goals with bite-sized practice.",
        "🔥"
    ),
    OnboardingPage(
        "Set Your Target Score",
        "Choose your target ACT Score to benchmark your progress and required accuracy.",
        "🎯"
    )
)

@Composable
fun OnboardingScreen(
    onFinish: (goalActScore: Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGES.size })
    val scope = rememberCoroutineScope()
    var selectedGoalScore by remember { mutableIntStateOf(32) }

    val targetAcc = remember(selectedGoalScore) {
        when {
            selectedGoalScore >= 36 -> 96
            selectedGoalScore >= 35 -> 94
            selectedGoalScore >= 34 -> 92
            selectedGoalScore >= 33 -> 90
            selectedGoalScore >= 32 -> 88
            selectedGoalScore >= 31 -> 85
            selectedGoalScore >= 30 -> 83
            selectedGoalScore >= 28 -> 78
            selectedGoalScore >= 25 -> 70
            selectedGoalScore >= 22 -> 62
            else -> 55
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = ONBOARDING_PAGES[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = page.icon,
                        fontSize = 80.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 44.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (pageIndex == 3) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Select Goal ACT Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$selectedGoalScore ACT",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BlueprintBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Slider(
                                value = selectedGoalScore.toFloat(),
                                onValueChange = { selectedGoalScore = it.toInt() },
                                valueRange = 20f..36f,
                                steps = 15,
                                colors = SliderDefaults.colors(
                                    thumbColor = BlueprintBlue,
                                    activeTrackColor = BlueprintBlue,
                                    inactiveTrackColor = BlueprintBlue.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("20", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("28", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("36", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = BlueprintBlue.copy(alpha = 0.08f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Goal: $selectedGoalScore ACT ≈ $targetAcc% Target Accuracy",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = BlueprintBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(ONBOARDING_PAGES.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) BlueprintBlue else BlueprintBlue.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                ActButton(
                    text = if (pagerState.currentPage == ONBOARDING_PAGES.size - 1) "Get Started" else "Next",
                    onClick = {
                        if (pagerState.currentPage < ONBOARDING_PAGES.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish(selectedGoalScore)
                        }
                    },
                    containerColor = BlueprintBlue
                )
            }
        }
    }
}
