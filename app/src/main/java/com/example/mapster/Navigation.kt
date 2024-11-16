package com.example.mapster

sealed class Screen(val route: String) {
    data object Scanner : Screen("scanner")
    data object DestinationSelector : Screen("result/{result}") {
        fun createRoute(result: String) = "result/${result}"
    }

    data object LandingPage : Screen("landing page")
    data object Result : Screen("result/{result}/destination/{destination}") {
        fun createRoute(result: String, destination: String) =
            "result/${result}/destination/${destination}"
    }
}