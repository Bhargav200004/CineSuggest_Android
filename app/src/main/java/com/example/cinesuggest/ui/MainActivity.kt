package com.example.cinesuggest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cinesuggest.ui.navigation.NavigationTransitions
import com.example.cinesuggest.ui.navigation.Screen
import com.example.cinesuggest.ui.screens.detail.MovieDetailScreen
import com.example.cinesuggest.ui.screens.home.HomeScreen
import com.example.cinesuggest.ui.theme.CineSuggestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineSuggestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(
                            route = Screen.Home.route,
                            exitTransition = NavigationTransitions.exitToLeftParallax(),
                            popEnterTransition = NavigationTransitions.enterFromLeftParallax()

                        ) {
                            HomeScreen(
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.Detail.createRoute(movieId = movieId))
                                },
//                                onProfileClick = {}
                            )
                        }

                        composable(
                            route = Screen.Detail.route,
                            arguments = listOf(
                                navArgument("movieId") { type = NavType.IntType }
                            ),
                            enterTransition = NavigationTransitions.enterFromRight(),
                            popExitTransition = NavigationTransitions.exitToRight()
                        ) {
                            MovieDetailScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }


                }
            }
        }
    }
}
