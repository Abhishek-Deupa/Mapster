package com.example.mapster

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun QRScannerApp() {
    val navController = rememberNavController()
    val viewModel = remember { QRScannerViewModel() }

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
            val context = LocalContext.current
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (uri != null) {
                        try {
                            val image = InputImage.fromFilePath(context, uri)
                            val scanner = BarcodeScanning.getClient()

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    if (barcodes.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "No QR code found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        for (barcode in barcodes) {
                                            val rawValue = barcode.rawValue
                                            val result = rawValue.toString()
                                            viewModel.onQRCodeScanned(result)
                                            navController.navigate(
                                                Screen.DestinationSelector.createRoute(result),
                                                navOptions {
                                                    popUpTo(Screen.DestinationSelector.route) { inclusive = true }
                                                    launchSingleTop = true
                                                })
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                    Toast.makeText(
                                        context,
                                        "Error reading image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }


            fun onUploadClick() {
                launcher.launch("image/*")
            }
            LandingScreen(
                onScanClick = { navController.navigate(Screen.Scanner.route) },
                onUploadClick = { onUploadClick() }
            )
        }
    }
}