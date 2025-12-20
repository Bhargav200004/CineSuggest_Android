import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

                    val newRating = rating.coerceIn(1, maxStars)

                    if (newRating != displayRating) {
                        displayRating = newRating
                    }
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