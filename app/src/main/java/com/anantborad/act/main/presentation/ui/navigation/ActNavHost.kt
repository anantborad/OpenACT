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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.anantborad.act.core.ui.theme.BlueprintBlue
import com.anantborad.act.main.data.QuestionDataSourceImpl
import com.anantborad.act.main.data.QuestionRepositoryImpl
import com.anantborad.act.main.data.local.ActDatabase
import com.anantborad.act.main.domain.QuestionRepository
import com.anantborad.act.main.domain.model.PracticeSubject
import com.anantborad.act.main.presentation.ui.screens.*
import com.anantborad.act.main.presentation.viewmodels.*

sealed class BottomNavItem(val route: Any, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(HomeRoute, Icons.Default.Home, "Home")
    object Practice : BottomNavItem(HubRoute, Icons.Default.Explore, "Practice")
    object Stats : BottomNavItem(StatsRoute, Icons.Default.BarChart, "Stats")
    object Profile : BottomNavItem(ProfileRoute, Icons.Default.Person, "Profile")
}

@Composable
fun ActNavHost() {
    val context = LocalContext.current
    val database = remember {
        Room.databaseBuilder(
            context.applicationContext,
            ActDatabase::class.java,
            "act_database"
        ).fallbackToDestructiveMigration().build()
    }
    val repository: QuestionRepository = remember {
        QuestionRepositoryImpl(QuestionDataSourceImpl(context.assets), database.questionDao(), context.applicationContext)
    }

    val startDestination = if (repository.isOnboardingCompleted()) HomeRoute else OnboardingRoute

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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<OnboardingRoute> {
                OnboardingScreen(onFinish = {
                    repository.setOnboardingCompleted(true)
                    navController.navigate(HomeRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                })
            }
            composable<HomeRoute> {
                val viewModel: HomeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return HomeViewModel(repository) as T
                        }
                    }
                )
                HomeScreen(
                    viewModel = viewModel,
                    onQuickStartClick = { 
                        navController.navigate(SessionRoute(subject = PracticeSubject.MATH.name)) 
                    },
                    onSubjectClick = { subject -> 
                        navController.navigate(SetupRoute(subject.name))
                    },
                    onViewAllClick = { navController.navigate(HubRoute) },
                    onProfileClick = { navController.navigate(ProfileRoute) }
                )
            }
            composable<HubRoute> {
                val viewModel: HubViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return HubViewModel(repository) as T
                        }
                    }
                )
                HubScreen(
                    viewModel = viewModel,
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
                val viewModel: SetupViewModel = viewModel()
                val setup: SetupRoute = backStackEntry.toRoute()
                val subject = PracticeSubject.valueOf(setup.subject)
                SetupScreen(
                    viewModel = viewModel,
                    subject = subject,
                    onStartClick = { count, difficulty ->
                        navController.navigate(
                            SessionRoute(
                                subject = subject.name,
                                questionCount = count,
                                difficulty = difficulty
                            )
                        )
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<SessionRoute> { backStackEntry ->
                val route: SessionRoute = backStackEntry.toRoute()
                val viewModel: SessionViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SessionViewModel(repository) as T
                        }
                    }
                )
                SessionScreen(
                    viewModel = viewModel,
                    subject = PracticeSubject.valueOf(route.subject),
                    questionCount = route.questionCount,
                    difficulty = route.difficulty,
                    skillFilter = route.skillFilter,
                    onFinishClick = { subject, score, total, time ->
                        navController.navigate(
                            ResultsRoute(
                                subject = subject,
                                score = score,
                                total = total,
                                timeSeconds = time
                            )
                        ) {
                            popUpTo(HomeRoute) { inclusive = false }
                        }
                    },
                    onCloseClick = { navController.popBackStack() }
                )
            }
            composable<ResultsRoute> { backStackEntry ->
                val route: ResultsRoute = backStackEntry.toRoute()
                val viewModel: ResultsViewModel = viewModel()
                ResultsScreen(
                    viewModel = viewModel,
                    subject = route.subject,
                    score = route.score,
                    total = route.total,
                    timeSeconds = route.timeSeconds,
                    onHomeClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    },
                    onReviewClick = { 
                        navController.navigate(ReviewRoute(sessionId = "current")) 
                    }
                )
            }
            composable<ReviewRoute> { backStackEntry ->
                val route: ReviewRoute = backStackEntry.toRoute()
                val viewModel: ReviewViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ReviewViewModel(repository) as T
                        }
                    }
                )
                ReviewScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<StatsRoute> {
                val viewModel: StatsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return StatsViewModel(repository) as T
                        }
                    }
                )
                StatsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<ProfileRoute> {
                val viewModel: ProfileViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProfileViewModel(repository) as T
                        }
                    }
                )
                ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<ProgressRoute> {
                ProgressScreen(onBackClick = { navController.popBackStack() })
            }
            composable<SkillDetailRoute> { backStackEntry ->
                val viewModel: SkillDetailViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SkillDetailViewModel(repository) as T
                        }
                    }
                )
                val detail: SkillDetailRoute = backStackEntry.toRoute()
                SkillDetailScreen(
                    viewModel = viewModel,
                    skillName = detail.skillName,
                    onBackClick = { navController.popBackStack() },
                    onStartPracticeClick = {
                        val targetSubject = when {
                            detail.skillName.contains("Punctuation", ignoreCase = true) || detail.skillName.contains("Grammar", ignoreCase = true) || detail.skillName.contains("Sentence", ignoreCase = true) -> PracticeSubject.ENGLISH
                            detail.skillName.contains("Passage", ignoreCase = true) -> PracticeSubject.READING
                            else -> PracticeSubject.MATH
                        }
                        navController.navigate(
                            SessionRoute(
                                subject = targetSubject.name,
                                skillFilter = detail.skillName
                            )
                        )
                    }
                )
            }
        }
    }
}
