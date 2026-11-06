package com.example.moviesapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviesapp.ui.components.BottomNavBar
import com.example.moviesapp.ui.screens.auth.ForgotPasswordScreen
import com.example.moviesapp.ui.screens.auth.LoginScreen
import com.example.moviesapp.ui.screens.auth.RegisterScreen
import com.example.moviesapp.ui.screens.details.DetailScreen
import com.example.moviesapp.ui.screens.favorites.FavoritesScreen
import com.example.moviesapp.ui.screens.home.HomeScreen
import com.example.moviesapp.ui.screens.profile.ProfileScreen
import com.example.moviesapp.ui.screens.search.SearchScreen
import com.example.moviesapp.ui.theme.CinemaBackground
import com.example.moviesapp.viewmodel.AuthViewModel

@Composable
fun MainAppScreen() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelDestination =
        Screen.bottomNavItems.any { it.route == currentRoute }

    // One AuthViewModel shared by Login and Register
    val authViewModel: AuthViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CinemaBackground,

        bottomBar = {
            if (isTopLevelDestination) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->

                        navController.navigate(screen.route) {

                            popUpTo(
                                navController.graph.findStartDestination().id
                            ) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (authViewModel.isAuthenticated.value) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ------------------------------------------------
            // LOGIN
            // ------------------------------------------------

            composable(Screen.Login.route) {

                LoginScreen(
                    authViewModel = authViewModel,

                    onLoginSuccess = {

                        navController.navigate(Screen.Home.route) {

                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },

                    onRegisterClick = {

                        navController.navigate(Screen.Register.route)
                    },

                    onForgotPasswordClick = {

                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            }


            // ------------------------------------------------
            // FORGOT PASSWORD
            // ------------------------------------------------

            composable(Screen.ForgotPassword.route) {

                ForgotPasswordScreen(
                    authViewModel = authViewModel,
                    onBackToLoginClick = {
                        navController.popBackStack()
                    }
                )
            }



            // ------------------------------------------------
            // REGISTER
            // ------------------------------------------------

            composable(Screen.Register.route) {

                RegisterScreen(
                    authViewModel = authViewModel,

                    onRegisterSuccess = {

                        navController.navigate(Screen.Home.route) {

                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },

                    onLoginClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ------------------------------------------------
            // HOME
            // ------------------------------------------------

            composable(Screen.Home.route) {

                HomeScreen(
                    onMovieClick = { movieId ->

                        navController.navigate(
                            Screen.Detail.createRoute(movieId)
                        )
                    },

                    onSearchClick = {

                        navController.navigate(Screen.Search.route)
                    },

                    onProfileClick = {

                        navController.navigate(Screen.Profile.route)
                    }
                )
            }


            // ------------------------------------------------
            // SEARCH
            // ------------------------------------------------

            composable(Screen.Search.route) {

                SearchScreen(
                    onMovieClick = { movieId ->

                        navController.navigate(
                            Screen.Detail.createRoute(movieId)
                        )
                    }
                )
            }


            // ------------------------------------------------
            // FAVORITES
            // ------------------------------------------------

            composable(Screen.Favorites.route) {

                FavoritesScreen(
                    onMovieClick = { movieId ->

                        navController.navigate(
                            Screen.Detail.createRoute(movieId)
                        )
                    }
                )
            }


            // ------------------------------------------------
            // PROFILE
            // ------------------------------------------------

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    },
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }



            // ------------------------------------------------
            // MOVIE DETAILS
            // ------------------------------------------------

            composable(
                route = Screen.Detail.route,

                arguments = listOf(
                    navArgument("movieId") {
                        type = NavType.IntType
                    }
                )

            ) { backStackEntry ->

                val movieId =
                    backStackEntry.arguments?.getInt("movieId") ?: 0

                DetailScreen(
                    movieId = movieId,

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }
        }
    }
}