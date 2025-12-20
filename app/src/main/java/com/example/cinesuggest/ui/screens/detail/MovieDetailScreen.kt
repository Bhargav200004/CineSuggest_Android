package com.example.cinesuggest.ui.screens.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cinesuggest.ui.components.GalaxyRatingBar
import com.example.cinesuggest.utils.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
                        onRatingChanged = { rating ->
                            viewModel.onEvent(
                                MovieDetailUiEvent.OnRatingChange(
                                    rating = rating
                                )
                            )
                        }
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
                InfoChip(text = movie.releaseDate)
                InfoChip(text = "${movie.runtime} min")
                InfoChip(text = movie.genres)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Your Rating" , style = MaterialTheme.typography.titleMedium , fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            CinematicRatingBar(
                currentRating = movie.userRating,
                onRatingChanged = onRatingChanged,
                starSize = 42.dp // You can adjust the size here
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
fun CinematicRatingBar(
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    starSize: Dp = 48.dp, // Slightly larger for better touch targets
    starSpacing: Dp = 8.dp
) {

    var displayRating by remember { mutableIntStateOf(currentRating) }

    LaunchedEffect(currentRating) {
        displayRating = currentRating
    }

    LaunchedEffect(displayRating) {
        if (displayRating != currentRating) {
            delay(500) // Wait for user to stop dragging (400ms)
            onRatingChanged(displayRating) // NOW call the API
        }
    }

    var rowWidth by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val starWidth = rowWidth / maxStars
                    val rating = (offset.x / starWidth).toInt() + 1
                    displayRating = rating.coerceIn(1, maxStars) // Update Local State ONLY
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val starWidth = rowWidth / maxStars
                    val x = change.position.x
                    val rating = (x / starWidth).toInt() + 1
                    displayRating = rating.coerceIn(1, maxStars) // Update Local State ONLY
                }
            },
        horizontalArrangement = Arrangement.spacedBy(starSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Measure the layout width dynamically
        Layout(
            content = {
                for (i in 1..maxStars) {
                    CinematicStar(
                        index = i,
                        rating = displayRating,
                        size = starSize
                    )
                }
            }
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val width = placeables.sumOf { it.width } + ((maxStars - 1) * starSpacing.toPx()).toInt()
            val height = placeables.maxOf { it.height }

            rowWidth = width.toFloat()

            layout(width, height) {
                var xPosition = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = 0)
                    xPosition += placeable.width + starSpacing.toPx().toInt()
                }
            }
        }
    }
}

@Composable
private fun CinematicStar(
    index: Int,
    rating: Int,
    size: Dp
) {
    val isSelected = index <= rating

    // 1. Rotation Animation (One-shot spin when selected)
    val rotation = remember { Animatable(0f) }

    // 2. Shockwave Animation (Radius and Alpha)
    val shockwaveRadius = remember { Animatable(0f) }
    val shockwaveAlpha = remember { Animatable(0f) }

    // Trigger animations when 'isSelected' changes to TRUE
    LaunchedEffect(isSelected) {
        if (isSelected) {
            launch {
                // Spin 360 degrees
                rotation.snapTo(0f)
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                // Expand shockwave
                shockwaveRadius.snapTo(0f)
                shockwaveAlpha.snapTo(0.5f) // Start visible

                launch {
                    shockwaveRadius.animateTo(
                        targetValue = 1.5f, // Expand to 1.5x size
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    shockwaveAlpha.animateTo(
                        targetValue = 0f, // Fade out
                        animationSpec = tween(500, easing = LinearEasing)
                    )
                }
            }
        } else {
            // Reset state if unselected
            rotation.snapTo(0f)
        }
    }

    // 3. Continuous Scale Animation (Pulse when selected)
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // 4. Color Animation
    val starColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.3f), // Gold vs Gray
        animationSpec = tween(300),
        label = "color"
    )

    Box(contentAlignment = Alignment.Center) {
        // A. Shockwave Circle (Canvas)
        Canvas(modifier = Modifier.size(size)) {
            if (shockwaveAlpha.value > 0f) {
                drawCircle(
                    color = Color(0xFFFFD700), // Gold shockwave
                    radius = (size.toPx() / 2) * shockwaveRadius.value,
                    alpha = shockwaveAlpha.value,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // B. The Star Icon
        Icon(
            imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = null,
            tint = starColor,
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation.value // Apply the spin
                }
        )
    }
}