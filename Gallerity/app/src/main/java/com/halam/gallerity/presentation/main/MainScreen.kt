package com.halam.gallerity.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.halam.gallerity.presentation.home.HomeScreen
import com.halam.gallerity.presentation.calendar.CalendarScreen
import com.halam.gallerity.presentation.settings.SettingsScreen
import com.halam.gallerity.presentation.login.LoginScreen

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    Triple("home", "Trang chủ", Icons.Default.Home),
                    Triple("calendar", "Lịch ảnh", Icons.Default.DateRange),
                    Triple("settings", "Cài đặt", Icons.Default.Settings),
                    Triple("login", "Đăng nhập", Icons.Default.AccountCircle)
                )

                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentRoute == route,
                        onClick = {
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen()
            }
            composable("calendar") {
                CalendarScreen(
                    onDayClick = { timestamp ->
                        rootNavController.navigate("day_photos/$timestamp")
                    }
                )
            }
            composable("settings") {
                SettingsScreen()
            }
            composable("login") {
                LoginScreen()
            }
        }
    }
}
