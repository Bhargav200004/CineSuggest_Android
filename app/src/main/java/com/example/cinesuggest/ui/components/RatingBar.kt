package com.example.cinesuggest.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.ceil
import kotlin.random.Random

// Standard Star Path Data (SVG)
private const val STAR_PATH_DATA = "M12,17.27L18.18,21l-1.64-7.03L22,9.24l-7.19-0.61L12,2L9.19,8.63L2,9.24l5.46,4.73L5.82,21L12,17.27z"

@Composable
fun GalaxyRatingBar(
    modifier: Modifier = Modifier,
    rating: Int, // CHANGED: Float -> Int
    onRatingChanged: (Int) -> Unit, // CHANGED: Float -> Int
    starSize: Dp = 48.dp,
    starSpacing: Dp = 12.dp,
    totalStars: Int = 5
) {
    val density = LocalDensity.current
    val view = LocalView.current

    // Internal state tracking Integer values
    var currentDragRating by remember { mutableIntStateOf(rating) }

    // Sync external rating if it changes (e.g. loaded from API)
    LaunchedEffect(rating) {
        currentDragRating = rating
    }

    // Debounce Logic for API calls
    LaunchedEffect(currentDragRating) {
        if (currentDragRating != rating) {
            // Haptic feedback (Compatible with all Android versions)
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

            delay(300) // Debounce (wait for user to stop sliding)
            onRatingChanged(currentDragRating)
        }
    }

    // Animation states
    val particles = remember { mutableStateListOf<Particle>() }

    // We animate as a Float so the bar "slides" to the new Integer value smoothly
    val animatedRating = animateFloatAsState(
        targetValue = currentDragRating.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ratingAnimation"
    )

    // Particle System Loop
    LaunchedEffect(Unit) {
        while (true) {
            val frameTime = withFrameNanos { it }
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                if (p.update()) {
                    iterator.remove()
                }
            }
            delay(16) // ~60 FPS
        }
    }

    // Star Path Initialization
    val starPath = remember {
        PathParser().parsePathString(STAR_PATH_DATA).toPath().apply {
            val bounds = getBounds()
            translate(Offset(-bounds.left, -bounds.top))
            val scaleMatrix = Matrix()
            scaleMatrix.scale(1f / bounds.width, 1f / bounds.height)
            transform(scaleMatrix)
        }
    }

    // Gradient Brushes
    val glowBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFD700).copy(alpha = 0.6f),
            Color.Transparent
        )
    )
    val fillBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFE082), // Light Gold
            Color(0xFFFFB300)  // Dark Gold
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val starWidthPx = starSize.toPx()
                    val spacingPx = starSpacing.toPx()
                    val totalWidth = (starWidthPx + spacingPx) * totalStars

                    // Logic: Map tap position to 1..5 Integer range
                    val relativeX = offset.x / totalWidth
                    val newRating = (relativeX * totalStars).coerceIn(0f, totalStars.toFloat())

                    // CEIL ensures if I click slightly on star 4 (e.g. 3.1), it becomes 4.
                    val finalRating = ceil(newRating).toInt().coerceIn(1, totalStars)

                    currentDragRating = finalRating
                    spawnParticles(particles, finalRating - 1, starSize.toPx(), spacingPx)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val starWidthPx = starSize.toPx()
                    val spacingPx = starSpacing.toPx()
                    val totalWidth = (starWidthPx + spacingPx) * totalStars

                    // Logic: Map drag position to 0..5 Integer range
                    val relativeX = change.position.x / totalWidth
                    val newRating = (relativeX * totalStars).coerceIn(0f, totalStars.toFloat())

                    // Convert to Int immediately
                    val finalRating = ceil(newRating).toInt().coerceIn(0, totalStars)

                    currentDragRating = finalRating
                }
            }
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(starSpacing)
        ) {
            Canvas(
                modifier = Modifier
                    .width((starSize * totalStars) + (starSpacing * (totalStars - 1)))
                    .height(starSize)
            ) {
                val starWidthPx = starSize.toPx()
                val spacingPx = starSpacing.toPx()

                for (i in 0 until totalStars) {
                    val starOffset = i * (starWidthPx + spacingPx)

                    // 1. Draw Glow (Only for fully filled stars in animation)
                    if (i < animatedRating.value) {
                        val glowRadius = starWidthPx * 0.8f
                        translate(left = starOffset + starWidthPx / 2, top = size.height / 2) {
                            drawCircle(
                                brush = glowBrush,
                                radius = glowRadius,
                                alpha = (animatedRating.value - i).coerceIn(0f, 1f)
                            )
                        }
                    }

                    // 2. Draw Star Background (Outline)
                    translate(left = starOffset, top = 0f) {
                        scale(scale = starWidthPx, pivot = Offset.Zero) {
                            drawPath(
                                path = starPath,
                                color = Color.DarkGray.copy(alpha = 0.3f),
                                style = Stroke(width = 0.05f)
                            )
                        }
                    }

                    // 3. Draw Star Fill
                    // Calculates how much of THIS star should be filled based on the current animation value.
                    // Since inputs are integers, this will slide from 0.0 -> 1.0 completely.
                    val fillLevel = (animatedRating.value - i).coerceIn(0f, 1f)

                    if (fillLevel > 0f) {
                        clipRect(
                            left = starOffset,
                            top = 0f,
                            right = starOffset + (starWidthPx * fillLevel),
                            bottom = size.height
                        ) {
                            translate(left = starOffset, top = 0f) {
                                scale(scale = starWidthPx, pivot = Offset.Zero) {
                                    drawPath(
                                        path = starPath,
                                        brush = fillBrush,
                                        style = Fill
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Draw Particles
                particles.forEach { p ->
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.radius,
                        center = p.position
                    )
                }
            }
        }
    }
}

// --- Particle System Logic (Unchanged) ---

private class Particle(
    var position: Offset,
    var velocity: Offset,
    var radius: Float,
    var alpha: Float,
    val color: Color,
    val decayRate: Float
) {
    fun update(): Boolean {
        position += velocity
        velocity += Offset(0f, 0.2f) // Gravity
        alpha -= decayRate
        return alpha <= 0f
    }
}

private fun spawnParticles(
    list: MutableList<Particle>,
    starIndex: Int,
    starWidth: Float,
    spacing: Float
) {
    val starCenterX = (starIndex * (starWidth + spacing)) + (starWidth / 2)
    val starCenterY = starWidth / 2

    repeat(15) {
        val angle = Random.nextDouble(0.0, 6.28)
        val speed = Random.nextDouble(2.0, 8.0)

        list.add(
            Particle(
                position = Offset(starCenterX, starCenterY),
                velocity = Offset(
                    (cos(angle) * speed).toFloat(),
                    (sin(angle) * speed).toFloat()
                ),
                radius = Random.nextDouble(2.0, 6.0).toFloat(),
                alpha = 1f,
                color = if (Random.nextBoolean()) Color(0xFFFFD700) else Color(0xFFFFFFFF),
                decayRate = Random.nextDouble(0.02, 0.05).toFloat()
            )
        )
    }
}