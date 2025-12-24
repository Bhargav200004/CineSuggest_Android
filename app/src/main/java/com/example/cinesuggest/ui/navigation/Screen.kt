package com.example.cinesuggest.ui.navigation

sealed class Screen(val route : String){
    object Register : Screen("register")
    object Home : Screen("home");
    object Detail : Screen("detail/{movieId}"){
        fun createRoute(movieId : Int) = "detail/$movieId"
    }
}