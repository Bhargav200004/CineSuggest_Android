package com.example.cinesuggest.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    // TODO : onProfileClick: () -> Unit
) {

    val viewModel : HomeViewModel =  hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommended for you", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {}) {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOutline,
                            contentDescription = "Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            when (val state = uiState) {
                UiState.Idle -> {

                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                UiState.Loading -> {
                    val brush = rememberShimmerBrush()
                    LazyColumn {
                        item { SectionHeader(title = "Picked For You") }
                        item {
                            Row(Modifier.padding(horizontal = 16.dp)) {
                                repeat(2) { Box(Modifier.width(280.dp).height(180.dp).padding(end=16.dp).clip(RoundedCornerShape(20.dp)).background(brush)) }
                            }
                        }
                        item { SectionHeader(title = "Explore All") }
                        items(5) { // Show 5 rows of shimmers (10 items total)
                            Row(Modifier.padding(16.dp)) {
                                Box(Modifier.weight(1f)) { ShimmerMovieItem(brush) }
                                Spacer(Modifier.width(16.dp))
                                Box(Modifier.weight(1f)) { ShimmerMovieItem(brush) }
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    MovieGrid(
                        recommendedMovies = state.data.recommendedMovies,
                        allMovies = state.data.movies,
                        onMovieClick = {movieId ->
                            onMovieClick(movieId)
                        }
                    )

                }
            }

        }
    }
}

@Composable
fun MovieGrid(
    recommendedMovies: List<Movie>,
    allMovies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            SectionHeader(title = "Picked For You")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recommendedMovies) { movie ->
                    RecommendationCard(movie = movie , onClick = { onMovieClick(movie.id) })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionHeader(title = "Explore All")
        }

        val columns = 2
        val rows = allMovies.chunked(columns)

        items(rows) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (movie in rowItems){
                    MovieItem(
                        movie = movie,
                        modifier = Modifier.weight(1f),
                        onClick = { onMovieClick(movie.id) }
                    )
                }

                if (rowItems.size < columns) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }


    }
}

@Composable
fun RecommendationCard(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(280.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box{
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f
                        )
                    )
            )
            Text(
                text = movie.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun MovieItem(movie: Movie, modifier: Modifier = Modifier, onClick: () -> Unit) {
   Column(
       modifier = modifier.clickable(
           onClick = onClick,
           interactionSource = remember { MutableInteractionSource() },
//           indication = rememberRipple(bounded = true)
       )
   ) {
       Card(
           shape = RoundedCornerShape(16.dp),
           elevation = CardDefaults.cardElevation(2.dp)
       ) {
           AsyncImage(
               model = movie.posterUrl,
               contentDescription = movie.title,
               contentScale = ContentScale.Crop,
               modifier = Modifier
                   .fillMaxWidth()
                   .aspectRatio(0.7f)
           )
       }
       Spacer(modifier = Modifier.height(8.dp))
       Text(
           text = movie.title,
           style = MaterialTheme.typography.bodyLarge,
           fontWeight = FontWeight.SemiBold,
           maxLines = 1,
           overflow = TextOverflow.Ellipsis
       )
       Text(
           text = movie.genres.split("|").firstOrNull() ?: "",
           style = MaterialTheme.typography.bodySmall,
           color = MaterialTheme.colorScheme.onSurfaceVariant
       )
   }
}

@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}


@Composable
fun ShimmerMovieItem(brush: Brush) {
    Column(modifier = Modifier.padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}