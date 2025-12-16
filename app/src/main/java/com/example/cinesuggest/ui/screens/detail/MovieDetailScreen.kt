package com.example.cinesuggest.ui.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cinesuggest.utils.UiState
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    onBackClick:() -> Unit
) {
    val viewModel: MovieDetailViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState is UiState.Success) {
                        val movie = (uiState as UiState.Success).data
                        FavoriteToggleButton(
                            isFavorite = movie.isFavorite,
                            onClick = {
                                viewModel.onEvent(MovieDetailUiEvent.OnFavoriteClick(movie.isFavorite))
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())){
            when(val state = uiState){
                UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    MovieDetailContent(
                        movie = state.data,
                        onRatingChanged = {}
                    )
                }
                is UiState.Error -> {
                    Timber.tag("MovieDetailScreen").e(state.message)
                }


            }
        }
    }
}

@Composable
fun MovieDetailContent (movie: MovieDetailUiState, onRatingChanged: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )

        Column(modifier = Modifier.padding(16.dp)){
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(text = movie.releaseDate.toString())
                InfoChip(text = "${movie.runtime} min")
                movie.genres.take(2).forEach { InfoChip(text = movie.genres) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Your Rating" , style = MaterialTheme.typography.titleMedium , fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            RatingBar(
                currentRating = movie.userRating ?: 4,
                onRatingChanged = onRatingChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(movie.overview , style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "ReadMore",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable{}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.PlayArrow , contentDescription = null , modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Watch Trailer" , fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InfoChip(text : String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp , vertical = 4.dp)
        )
    }
}

@Composable
fun FavoriteToggleButton(isFavorite : Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Toggle Favorite",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RatingBar(
    maxStars : Int = 5,
    currentRating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row {
        for (i in 1..maxStars){
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (i <= currentRating) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}