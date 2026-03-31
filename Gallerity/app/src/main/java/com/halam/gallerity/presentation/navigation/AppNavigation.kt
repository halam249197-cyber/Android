package com.halam.gallerity.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.halam.gallerity.presentation.onboarding.OnboardingScreen
import com.halam.gallerity.presentation.detail.ImageDetailScreen
import com.halam.gallerity.presentation.home.HomeUiState
import com.halam.gallerity.presentation.home.HomeViewModel
import com.halam.gallerity.presentation.search.SearchScreen
import java.time.Instant
import java.time.ZoneId

@Composable
fun AppNavigation(viewModel: NavigationViewModel = hiltViewModel()) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState(initial = null)

    // Hold drawing while resolving async SharedPrefs
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
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            com.halam.gallerity.presentation.main.MainScreen(navController)
        }
        composable("day_photos/{timestamp}") { backStackEntry ->
            val timestamp = backStackEntry.arguments?.getString("timestamp")?.toLongOrNull()
                ?: System.currentTimeMillis()
            com.halam.gallerity.presentation.calendar.DayPhotosScreen(
                timestamp = timestamp,
                onBack = { navController.popBackStack() }
            )
        }
        composable("image_detail/{mediaId}") { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull() ?: 0L
            val homeViewModel: HomeViewModel = hiltViewModel()
            val uiState by homeViewModel.uiState.collectAsState()

            when (val state = uiState) {
                is HomeUiState.Success -> {
                    val visibleMedia = state.media.filter { !it.isSecured && !it.isTrashed }
                    val initialIndex = visibleMedia.indexOfFirst { it.id == mediaId }.coerceAtLeast(0)
                    ImageDetailScreen(
                        mediaFiles = visibleMedia,
                        initialIndex = initialIndex,
                        onBack = { navController.popBackStack() }
                    )
                }
                else -> { /* Loading or Error — handled by parent */ }
            }
        }
        composable("ai_chat") {
            com.halam.gallerity.presentation.ai.AIChatScreen(
                onBack = { navController.popBackStack() },
                onNavigateToSearch = { query ->
                    // Navigate to search screen with Gemini's NLP parameters
                    navController.navigate("search_photos/$query")
                }
            )
        }
        composable("search_photos/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(
                query = query,
                onBack = { navController.popBackStack() },
                onImageClick = { mediaId ->
                    navController.navigate("image_detail/$mediaId")
                }
            )
        }
    }
}
