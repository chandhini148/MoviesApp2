package com.example.moviesapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {

    // Authentication screens
    object Login : Screen(
        route = "login",
        title = "Login"
    )

    object Register : Screen(
        route = "register",
        title = "Register"
    )

    object ForgotPassword : Screen(
        route = "forgot_password",
        title = "Forgot Password"
    )


    // Main app screens
    object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Search : Screen(
        route = "search",
        title = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    object Favorites : Screen(
        route = "favorites",
        title = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )

    object Profile : Screen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    object Detail : Screen(
        route = "detail/{movieId}",
        title = "Detail"
    ) {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }

    companion object {
        // Login and Register are intentionally NOT included here
        // because they should not show the bottom navigation bar.
        val bottomNavItems = listOf(
            Home,
            Search,
            Favorites,
            Profile
        )
    }
}