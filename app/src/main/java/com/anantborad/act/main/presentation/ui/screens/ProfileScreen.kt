package com.anantborad.act.main.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.anantborad.act.core.ui.theme.BlueprintBlue
import com.anantborad.act.main.presentation.ui.components.ActHeader
import com.anantborad.act.main.presentation.viewmodels.ProfileViewModel

import androidx.compose.material.icons.filled.Star

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onRestartOnboarding: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newNameText by remember { mutableStateOf(uiState.userName) }

    var showGoalScoreDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Display Name") },
            text = {
                OutlinedTextField(
                    value = newNameText,
                    onValueChange = { newNameText = it },
                    singleLine = true,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateName(newNameText)
                    showEditNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showGoalScoreDialog) {
        var tempScore by remember(uiState.goalActScore) { mutableIntStateOf(uiState.goalActScore) }
        AlertDialog(
            onDismissRequest = { showGoalScoreDialog = false },
            title = { Text("Target ACT Score") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select your goal ACT score to adjust your target accuracy benchmark:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$tempScore ACT",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = BlueprintBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = tempScore.toFloat(),
                        onValueChange = { tempScore = it.toInt() },
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
                        Text("20", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("28", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("36", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateGoalActScore(tempScore)
                    showGoalScoreDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalScoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDialog = false },
            title = { Text("Reset All Data & Restart Onboarding") },
            text = { Text("Are you sure you want to reset all app settings, clear practice history, and return to onboarding? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetAllDialog = false
                    viewModel.resetAllData {
                        onRestartOnboarding()
                    }
                }) {
                    Text("Reset Everything", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                title = "Profile",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = "https://ui-avatars.com/api/?name=${uiState.userName}&background=random&size=200",
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        newNameText = uiState.userName
                        showEditNameDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = BlueprintBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Goal Score: ${uiState.goalActScore} ACT • Daily: ${uiState.dailyGoal}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Settings sections
            ProfileSection("Preferences") {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Edit Name (${uiState.userName})",
                    onClick = {
                        newNameText = uiState.userName
                        showEditNameDialog = true
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Star,
                    title = "Goal ACT Score (${uiState.goalActScore} ACT)",
                    onClick = { showGoalScoreDialog = true }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Adjust,
                    title = "Daily Practice Goal (${uiState.dailyGoal})",
                    onClick = {
                        val nextGoal = if (uiState.dailyGoal.contains("3")) "5 sessions" else "3 sessions"
                        viewModel.updateDailyGoal(nextGoal)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection("Data & Privacy") {
                ProfileMenuItem(
                    icon = Icons.Default.Refresh,
                    title = "Reset All Data & Restart Onboarding",
                    color = Color.Red,
                    onClick = { showResetAllDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
