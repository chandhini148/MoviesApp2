package com.example.moviesapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviesapp.data.model.GenreHelper
import com.example.moviesapp.ui.components.CategoryChips
import com.example.moviesapp.ui.components.ErrorView
import com.example.moviesapp.ui.components.FeaturedCarousel
import com.example.moviesapp.ui.components.LoadingIndicator
import com.example.moviesapp.ui.components.MovieCard
import com.example.moviesapp.ui.components.SectionHeader
import com.example.moviesapp.ui.theme.CinemaBackground
import com.example.moviesapp.ui.theme.CinemaCardBackground
import com.example.moviesapp.ui.theme.CinemaGold
import com.example.moviesapp.ui.theme.CinemaSurfaceVariant
import com.example.moviesapp.ui.theme.TextPrimary
import com.example.moviesapp.ui.theme.TextSecondary
import com.example.moviesapp.viewmodel.HomeUiState
import com.example.moviesapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    message = "Loading the cinematic universe..."
                )
            }

            is HomeUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadHomeData() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HomeUiState.Success -> {
                HomeContent(
                    state = state,
                    onMovieClick = onMovieClick,
                    onSearchClick = onSearchClick,
                    onProfileClick = onProfileClick,
                    onCategorySelect = { viewModel.selectCategory(it) }
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onMovieClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCategorySelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Greeting & Header
        item {
            HomeTopBar(
                onProfileClick = onProfileClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Search Bar trigger
        item {
            HomeSearchBar(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Featured Movies Carousel
        if (state.featuredMovies.isNotEmpty()) {
            item {
                SectionHeader(title = "Featured")
                FeaturedCarousel(
                    movies = state.featuredMovies,
                    onMovieClick = onMovieClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        // Categories Chips
        item {
            SectionHeader(title = "Categories")
            CategoryChips(
                categories = GenreHelper.defaultCategories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = onCategorySelect,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // If a specific category filter is active, show the category movies list
        if (state.selectedCategory != "All" && state.categoryMovies.isNotEmpty()) {
            item {
                SectionHeader(title = "${state.selectedCategory} Movies")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.categoryMovies) { movie ->
                        MovieCard(movie = movie, onClick = onMovieClick)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Trending Now Section
        if (state.trendingMovies.isNotEmpty()) {
            item {
                SectionHeader(title = "🔥 Trending Now")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.trendingMovies) { movie ->
                        MovieCard(movie = movie, onClick = onMovieClick)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Popular Movies Section
        if (state.popularMovies.isNotEmpty()) {
            item {
                SectionHeader(title = "🍿 Popular Movies")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.popularMovies) { movie ->
                        MovieCard(movie = movie, onClick = onMovieClick)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Top Rated Section
        if (state.topRatedMovies.isNotEmpty()) {
            item {
                SectionHeader(title = "⭐ Top Rated")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.topRatedMovies) { movie ->
                        MovieCard(movie = movie, onClick = onMovieClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome Back 👋",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Discover Movies",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CinemaSurfaceVariant)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = CinemaGold,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun HomeSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CinemaCardBackground)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = CinemaGold,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "Search movies, genres, actors...",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
