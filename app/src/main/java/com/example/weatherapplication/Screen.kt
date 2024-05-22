package com.example.weatherapplication

sealed class Screen(val route:String){
    object MainScreen : Screen("main_screen")
    object SearchScreen : Screen("search_screen")
    object FavoritesScreen : Screen("favorites_screen")
    object LoginScreen : Screen("login_screen")
}