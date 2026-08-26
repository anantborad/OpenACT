package com.anantborad.act.main.presentation.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

@Serializable
object HomeRoute

@Serializable
object HubRoute

@Serializable
data class SetupRoute(val subject: String)

@Serializable
data class SessionRoute(
    val subject: String,
    val questionCount: Int = 10,
    val difficulty: String = "Mixed"
)

@Serializable
data class ResultsRoute(
    val subject: String,
    val score: Int,
    val total: Int,
    val timeSeconds: Long
)

@Serializable
data class ReviewRoute(
    val sessionId: String = ""
)

@Serializable
object ProgressRoute

@Serializable
object StatsRoute

@Serializable
data class SkillDetailRoute(val skillName: String)

@Serializable
object ProfileRoute
