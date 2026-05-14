package com.aksharadeepa.tutor.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aksharadeepa.tutor.AppContainer
import com.aksharadeepa.tutor.presentation.screen.OverviewScreen
import com.aksharadeepa.tutor.presentation.screen.QuizScreen
import com.aksharadeepa.tutor.presentation.screen.StrengthMapScreen
import com.aksharadeepa.tutor.presentation.screen.SubjectsScreen
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModel
import com.aksharadeepa.tutor.presentation.viewmodel.TutorViewModelFactory

private sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Overview : BottomNavItem("overview", "Overview", Icons.Default.Home)
    data object StrengthMap : BottomNavItem("strength_map", "Strength", Icons.Default.Insights)
    data object Subjects : BottomNavItem("subjects", "Subjects", Icons.AutoMirrored.Filled.MenuBook)
}

private val bottomNavItems = listOf(
    BottomNavItem.Overview,
    BottomNavItem.StrengthMap,
    BottomNavItem.Subjects
)

@Composable
fun AksharaDeepaNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val viewModel: TutorViewModel = viewModel(factory = TutorViewModelFactory(container))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on the 3 main screens
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
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
                            icon = {
                                Icon(item.icon, contentDescription = item.label)
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "overview",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("overview") {
                OverviewScreen(viewModel = viewModel)
            }
            composable("strength_map") {
                StrengthMapScreen(viewModel = viewModel)
            }
            composable("subjects") {
                SubjectsScreen(
                    viewModel = viewModel,
                    onStartQuiz = { subjectId, chapterId, title ->
                        navController.navigate("quiz/$subjectId/$chapterId/${Uri.encode(title)}")
                    }
                )
            }
            composable(
                route = "quiz/{subjectId}/{chapterId}/{title}",
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.LongType },
                    navArgument("chapterId") { type = NavType.LongType },
                    navArgument("title") { type = NavType.StringType }
                )
            ) { entry ->
                val subjectId = entry.arguments?.getLong("subjectId") ?: 0L
                val chapterId = entry.arguments?.getLong("chapterId") ?: 0L
                val title = entry.arguments?.getString("title").orEmpty()
                QuizScreen(
                    viewModel = viewModel,
                    subjectId = subjectId,
                    chapterId = chapterId,
                    chapterTitle = title,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
