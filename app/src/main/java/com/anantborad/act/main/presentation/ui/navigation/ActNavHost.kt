package com.anantborad.act.main.presentation.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.anantborad.act.core.ui.theme.BlueprintBlue
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.screens.*

sealed class BottomNavItem(val route: Any, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(HomeRoute, Icons.Default.Home, "Home")
    object Practice : BottomNavItem(HubRoute, Icons.Default.Explore, "Practice")
    object Stats : BottomNavItem(StatsRoute, Icons.Default.BarChart, "Stats")
    object Profile : BottomNavItem(ProfileRoute, Icons.Default.Person, "Profile")
}

@Composable
fun ActNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Practice,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    val hideBottomNav = currentDestination?.let { dest ->
        listOf(OnboardingRoute::class, SessionRoute::class, ResultsRoute::class, ReviewRoute::class).any { 
            dest.hasRoute(it)
        }
    } ?: false

    Scaffold(
        bottomBar = {
            if (!hideBottomNav) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BlueprintBlue,
                                selectedTextColor = BlueprintBlue,
                                indicatorColor = BlueprintBlue.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<OnboardingRoute> {
                OnboardingScreen(onFinish = {
                    navController.navigate(HomeRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                })
            }
            composable<HomeRoute> {
                HomeScreen(
                    onQuickStartClick = { navController.navigate(SessionRoute) },
                    onSubjectClick = { subject -> 
                        navController.navigate(SetupRoute(subject.name))
                    },
                    onViewAllClick = { navController.navigate(HubRoute) },
                    onProfileClick = { navController.navigate(ProfileRoute) }
                )
            }
            composable<HubRoute> {
                HubScreen(
                    onSubjectClick = { subject ->
                        navController.navigate(SetupRoute(subject.name))
                    },
                    onSkillClick = { skillName ->
                        navController.navigate(SkillDetailRoute(skillName))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<SetupRoute> { backStackEntry ->
                val setup: SetupRoute = backStackEntry.toRoute()
                val subject = PracticeSubject.valueOf(setup.subject)
                SetupScreen(
                    subject = subject,
                    onStartClick = { navController.navigate(SessionRoute) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<SessionRoute> {
                SessionScreen(
                    onFinishClick = { 
                        navController.navigate(ResultsRoute) {
                            popUpTo(HomeRoute) { inclusive = false }
                        }
                    },
                    onCloseClick = { navController.popBackStack() }
                )
            }
            composable<ResultsRoute> {
                ResultsScreen(
                    onHomeClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    },
                    onReviewClick = { navController.navigate(ReviewRoute) }
                )
            }
            composable<ReviewRoute> {
                ReviewScreen(onBackClick = { navController.popBackStack() })
            }
            composable<StatsRoute> {
                StatsScreen(onBackClick = { navController.popBackStack() })
            }
            composable<ProfileRoute> {
                ProfileScreen(onBackClick = { navController.popBackStack() })
            }
            composable<ProgressRoute> {
                ProgressScreen(onBackClick = { navController.popBackStack() })
            }
            composable<SkillDetailRoute> { backStackEntry ->
                val detail: SkillDetailRoute = backStackEntry.toRoute()
                SkillDetailScreen(
                    skillName = detail.skillName,
                    onBackClick = { navController.popBackStack() },
                    onStartPracticeClick = { navController.navigate(SessionRoute) }
                )
            }
        }
    }
}
