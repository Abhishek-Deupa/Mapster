package com.example.mapster

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.example.mapster.screens.DestinationSelectorScreen
import com.example.mapster.screens.LandingScreen
import com.example.mapster.screens.ResultScreen
import com.example.mapster.screens.ScannerScreen

@Composable
fun QRScannerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.LandingPage.route) {
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onResultScanned = { result ->
                    navController.navigate(
                        Screen.DestinationSelector.createRoute(result),
                        navOptions {
                            popUpTo(Screen.DestinationSelector.route) { inclusive = true }
                            launchSingleTop = true
                        })
                }
            )
        }

        composable(
            route = Screen.DestinationSelector.route,
            arguments = listOf(navArgument("result") { type = NavType.StringType })
        ) { backStackEntry ->
            DestinationSelectorScreen(
                result = backStackEntry.arguments?.getString("result") ?: "",
                onBackClick = {
                    navController.popBackStack()
                },
                onResultClick = { result, destination ->
                    navController.navigate(Screen.Result.createRoute(result, destination))
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("result") { type = NavType.StringType },
                navArgument("destination") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ResultScreen(
                result = backStackEntry.arguments?.getString("result") ?: "",
                destination = backStackEntry.arguments?.getString("destination") ?: "",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.LandingPage.route,
        ) {
            LandingScreen { navController.navigate(Screen.Scanner.route) }
        }
    }
}