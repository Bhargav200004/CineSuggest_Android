package com.example.cinesuggest.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cinesuggest.ui.navigation.NavigationTransitions
import com.example.cinesuggest.ui.navigation.Screen
import com.example.cinesuggest.ui.screens.detail.MovieDetailScreen
import com.example.cinesuggest.ui.screens.home.HomeScreen
import com.example.cinesuggest.ui.screens.register.RegisterScreen
import com.example.cinesuggest.ui.theme.CineSuggestTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineSuggestTheme {
                val mainViewModel: MainViewModel = hiltViewModel()

                // 2. Observe the decision (Home vs Register)
                val startDestination by mainViewModel.startDestination.collectAsState()


                Surface(modifier = Modifier.fillMaxSize()) {

                    if (startDestination == null){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }else{

                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = startDestination!!
                        ) {

                            composable(
                                route = Screen.Register.route
                            ){
                                RegisterScreen(
                                    onRegistrationSuccess = {
                                        navController.navigate(Screen.Home.route){
                                            popUpTo(Screen.Register.route){ inclusive = true }
                                        }
                                    }
                                )
                            }

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
}
