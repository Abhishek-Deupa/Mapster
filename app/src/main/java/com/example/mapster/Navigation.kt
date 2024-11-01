package com.example.mapster

sealed class Screen(val route: String) {
    data object Scanner : Screen("scanner")
    data object Result : Screen("result/{result}") {
        fun createRoute(result: String) = "result/${result}"
    }
}