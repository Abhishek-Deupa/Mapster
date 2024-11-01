package com.example.mapster

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mapster.Screens.ResultScreen
import com.example.mapster.Screens.ScannerScreen

@Composable
fun QRScannerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Scanner.route) {
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onResultScanned = { result ->
                    navController.navigate(Screen.Result.createRoute(result))
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("result") { type = NavType.StringType })
        ) { backStackEntry ->
            ResultScreen(
                result = backStackEntry.arguments?.getString("result") ?: "",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}