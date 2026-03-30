package com.halam.gallerity.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.halam.gallerity.presentation.onboarding.OnboardingScreen

// Placeholder MainScreen for execution continuity
import com.halam.gallerity.presentation.home.HomeScreen

@Composable
fun AppNavigation(viewModel: NavigationViewModel = hiltViewModel()) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState(initial = null)
    
    // Hold drawing implicitly while resolving async SharedPrefs
    if (isFirstLaunch == null) return

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isFirstLaunch == true) "onboarding" else "main"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onFinishOnboarding = {
                    viewModel.completeFirstLaunch()
                    navController.navigate("main") {
                        // Prevent back press from returning to Onboarding
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            com.halam.gallerity.presentation.main.MainScreen(navController)
        }
        composable("day_photos/{timestamp}") { backStackEntry ->
            val timestamp = backStackEntry.arguments?.getString("timestamp")?.toLongOrNull() ?: System.currentTimeMillis()
            com.halam.gallerity.presentation.calendar.DayPhotosScreen(
                timestamp = timestamp,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
