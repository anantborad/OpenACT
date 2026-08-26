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
object SessionRoute

@Serializable
object ResultsRoute

@Serializable
object ReviewRoute

@Serializable
object ProgressRoute

@Serializable
object StatsRoute

@Serializable
data class SkillDetailRoute(val skillName: String)

@Serializable
object ProfileRoute
