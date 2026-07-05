package com.lumastride.holopulsefit.navigation

import android.content.Intent
import android.net.Uri
import android.provider.Settings as AndroidSettings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.lumastride.holopulsefit.data.ExerciseType
import com.lumastride.holopulsefit.ui.components.HoloTopBar
import com.lumastride.holopulsefit.ui.screens.DashboardScreen
import com.lumastride.holopulsefit.ui.screens.GhostTrainerScreen
import com.lumastride.holopulsefit.ui.screens.HistoryDetailScreen
import com.lumastride.holopulsefit.ui.screens.HistoryScreen
import com.lumastride.holopulsefit.ui.screens.LaunchScreen
import com.lumastride.holopulsefit.ui.screens.LibraryScreen
import com.lumastride.holopulsefit.ui.screens.SettingsScreen
import com.lumastride.holopulsefit.ui.screens.SummaryScreen
import com.lumastride.holopulsefit.ui.screens.WorkoutScreen
import com.lumastride.holopulsefit.ui.viewmodel.DashboardViewModel
import com.lumastride.holopulsefit.ui.viewmodel.GhostTrainerViewModel
import com.lumastride.holopulsefit.ui.viewmodel.HistoryDetailViewModel
import com.lumastride.holopulsefit.ui.viewmodel.HistoryViewModel
import com.lumastride.holopulsefit.ui.viewmodel.SettingsViewModel
import com.lumastride.holopulsefit.ui.viewmodel.SummaryViewModel
import com.lumastride.holopulsefit.ui.viewmodel.WorkoutGesture
import com.lumastride.holopulsefit.ui.viewmodel.WorkoutViewModel
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.DeepSpaceBase

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(HoloDestinations.DASHBOARD, "Home", Icons.Filled.Home),
    BottomItem(HoloDestinations.LIBRARY, "Workouts", Icons.AutoMirrored.Filled.List),
    BottomItem(HoloDestinations.HISTORY, "History", Icons.Filled.History),
    BottomItem(HoloDestinations.SETTINGS, "Settings", Icons.Filled.Settings),
)

/**
 * Root of the single activity app: a Scaffold with a bottom navigation bar for the primary
 * destinations and the Navigation Compose host for all 8 screen areas (appflow.md section 1).
 */
@Composable
fun HoloApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomItems.map { it.route }.toSet()

    Scaffold(
        containerColor = DeepSpaceBase,
        bottomBar = {
            if (showBottomBar) {
                HoloBottomBar(currentRoute = currentRoute, navController = navController)
            }
        },
    ) { innerPadding ->
        HoloNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun HoloBottomBar(currentRoute: String?, navController: NavHostController) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = CyanPulse,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun HoloNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = HoloDestinations.LAUNCH,
        modifier = modifier,
    ) {
        composable(HoloDestinations.LAUNCH) {
            LaunchScreen(onFinished = {
                navController.navigate(HoloDestinations.DASHBOARD) {
                    popUpTo(HoloDestinations.LAUNCH) { inclusive = true }
                }
            })
        }

        composable(HoloDestinations.DASHBOARD) {
            val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
            val state by vm.state.collectAsStateWithLifecycle()
            ScreenScaffold(title = "HoloPulse Fit") {
                DashboardScreen(
                    state = state,
                    onQuickStart = { navController.navigate(HoloDestinations.LIBRARY) },
                    onOpenSession = { session ->
                        navController.navigate(HoloDestinations.HistoryDetail.build(session.sessionId))
                    },
                    onOpenGhost = {
                        navController.navigate(HoloDestinations.Ghost.build(ExerciseType.SQUATS.id))
                    },
                    modifier = it,
                )
            }
        }

        composable(HoloDestinations.LIBRARY) {
            ScreenScaffold(title = "Workout Library") {
                LibraryScreen(
                    onStart = { exercise, target ->
                        navController.navigate(HoloDestinations.Workout.build(exercise.id, target))
                    },
                    modifier = it,
                )
            }
        }

        composable(HoloDestinations.HISTORY) {
            val vm: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
            val state by vm.state.collectAsStateWithLifecycle()
            ScreenScaffold(title = "History") {
                HistoryScreen(
                    state = state,
                    onOpenSession = { session ->
                        navController.navigate(HoloDestinations.HistoryDetail.build(session.sessionId))
                    },
                    modifier = it,
                )
            }
        }

        composable(HoloDestinations.SETTINGS) {
            val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
            val state by vm.state.collectAsStateWithLifecycle()
            ScreenScaffold(title = "Settings") {
                SettingsScreen(
                    state = state,
                    onAuraIntensityChange = vm::setAuraIntensity,
                    onReducedEffectsChange = vm::setReducedEffects,
                    onGhostTrainerChange = vm::setGhostTrainerEnabled,
                    onOpenPermissionSettings = {
                        val intent = Intent(
                            AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null),
                        )
                        context.startActivity(intent)
                    },
                    onClearHistory = vm::clearHistory,
                    modifier = it,
                )
            }
        }

        composable(
            route = HoloDestinations.Workout.ROUTE,
            arguments = listOf(
                navArgument(HoloDestinations.ARG_EXERCISE_TYPE) { type = NavType.StringType },
                navArgument(HoloDestinations.ARG_TARGET) {
                    type = NavType.IntType
                    defaultValue = -1
                },
            ),
        ) {
            val vm: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory)
            val state by vm.uiState.collectAsStateWithLifecycle()

            val finishToSummary: () -> Unit = {
                vm.stop { sessionId ->
                    navController.navigate(HoloDestinations.Summary.build(sessionId)) {
                        popUpTo(HoloDestinations.Workout.ROUTE) { inclusive = true }
                    }
                }
            }

            // The only gesture that needs navigation is the hand raise finish; exercise switching
            // happens in place inside the ViewModel.
            LaunchedEffect(vm) {
                vm.gestures.collect { gesture ->
                    when (gesture) {
                        WorkoutGesture.FINISH -> finishToSummary()
                    }
                }
            }

            // The title follows the current exercise, which can change in place.
            ScreenScaffold(title = state.exercise.displayName, onBack = { navController.popBackStack() }) {
                WorkoutScreen(
                    state = state,
                    poseFrames = vm.poseFrame,
                    ghostFrames = vm.ghostFrame,
                    onPoseFrame = vm::onPoseFrame,
                    onTogglePause = vm::togglePause,
                    onStop = finishToSummary,
                    onNextExercise = vm::switchToNext,
                    onPrevExercise = vm::switchToPrevious,
                    onPermissionState = vm::setPermissionState,
                    modifier = it,
                )
            }
        }

        composable(
            route = HoloDestinations.Summary.ROUTE,
            arguments = listOf(navArgument(HoloDestinations.ARG_SESSION_ID) { type = NavType.StringType }),
        ) {
            val vm: SummaryViewModel = viewModel(factory = SummaryViewModel.Factory)
            val summary by vm.state.collectAsStateWithLifecycle()
            ScreenScaffold(title = "Summary", onBack = { navController.popBackStack() }) { modifier ->
                val current = summary
                if (current == null) {
                    LoadingPane(modifier)
                } else {
                    SummaryScreen(
                        state = current,
                        onBackToDashboard = {
                            navController.navigate(HoloDestinations.DASHBOARD) {
                                popUpTo(HoloDestinations.DASHBOARD) { inclusive = true }
                            }
                        },
                        onRepeat = {
                            navController.navigate(HoloDestinations.Workout.build(current.exerciseTypeId)) {
                                popUpTo(HoloDestinations.Summary.ROUTE) { inclusive = true }
                            }
                        },
                        onReplayGhost = {
                            navController.navigate(HoloDestinations.Ghost.build(current.exerciseTypeId))
                        },
                        modifier = modifier,
                    )
                }
            }
        }

        composable(
            route = HoloDestinations.HistoryDetail.ROUTE,
            arguments = listOf(navArgument(HoloDestinations.ARG_SESSION_ID) { type = NavType.StringType }),
        ) {
            val vm: HistoryDetailViewModel = viewModel(factory = HistoryDetailViewModel.Factory)
            val session by vm.state.collectAsStateWithLifecycle()
            ScreenScaffold(title = "Session Detail", onBack = { navController.popBackStack() }) { modifier ->
                val current = session
                if (current != null) {
                    HistoryDetailScreen(
                        session = current,
                        onReplay = { navController.navigate(HoloDestinations.Ghost.build(current.exerciseTypeId)) },
                        onDelete = { vm.delete(onDeleted = { navController.popBackStack() }) },
                        modifier = modifier,
                    )
                } else {
                    LoadingPane(modifier)
                }
            }
        }

        composable(
            route = HoloDestinations.Ghost.ROUTE,
            arguments = listOf(navArgument(HoloDestinations.ARG_EXERCISE_TYPE) { type = NavType.StringType }),
        ) {
            val vm: GhostTrainerViewModel = viewModel(factory = GhostTrainerViewModel.Factory)
            val state by vm.ui.collectAsStateWithLifecycle()
            ScreenScaffold(title = "Ghost Trainer", onBack = { navController.popBackStack() }) { modifier ->
                GhostTrainerScreen(state = state, onTogglePlay = vm::togglePlay, modifier = modifier)
            }
        }
    }
}

/**
 * Wraps a screen with the shared top bar and passes a weight modifier so the screen fills the space
 * below the bar. Keeps every destination visually consistent.
 */
@Composable
private fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        HoloTopBar(title = title, onBack = onBack)
        content(Modifier.weight(1f))
    }
}

/** Simple centered progress indicator while a detail screen loads its data. */
@Composable
private fun LoadingPane(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CyanPulse)
    }
}
