package com.example.cinesuggest.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CinematicHeartButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    // Animation States
    val heartScale = remember { Animatable(1f) }
    val particles = remember { mutableStateListOf<HeartParticle>() }

    // Color Animation
    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "color"
    )

    // Trigger Animation when 'isFavorite' changes to TRUE
    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            // 1. Haptic Feedback
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

            // 2. Bounce Animation
            launch {
                heartScale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                heartScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            // 3. Spawn Particles
            launch {
                spawnHeartParticles(particles, iconSize.value * 2) // *2 for pixel conversion approx
            }
        }
    }

    // Game Loop for Particles
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { it }
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                if (p.update()) {
                    iterator.remove()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default ripple for cleaner look
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // 1. Particle Layer (Behind)
        Canvas(modifier = Modifier.size(iconSize * 2.5f)) {
            particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.radius,
                    center = p.position + center // Offset from center of Canvas
                )
            }
        }

        // 2. Icon Layer
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Toggle Favorite",
            tint = heartColor,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    scaleX = heartScale.value
                    scaleY = heartScale.value
                }
        )
    }
}

// --- Particle Logic ---

private class HeartParticle(
    var position: Offset,
    var velocity: Offset,
    var radius: Float,
    var alpha: Float,
    val color: Color,
    val decayRate: Float
) {
    fun update(): Boolean {
        position += velocity
        velocity += Offset(0f, 0.1f) // Slight gravity
        alpha -= decayRate
        radius *= 0.95f // Shrink over time
        return alpha <= 0f || radius <= 0.5f
    }
}

private fun spawnHeartParticles(list: MutableList<HeartParticle>, baseRadius: Float) {
    repeat(12) {
        val angle = Random.nextDouble(0.0, 6.28)
        // Explosion speed
        val speed = Random.nextDouble(2.0, 5.0)

        list.add(
            HeartParticle(
                position = Offset.Zero, // Start at center
                velocity = Offset(
                    (cos(angle) * speed).toFloat(),
                    (sin(angle) * speed).toFloat()
                ),
                radius = Random.nextDouble(4.0, 8.0).toFloat(),
                alpha = 1f,
                color = if (Random.nextBoolean()) Color(0xFFE91E63) else Color(0xFFFF80AB), // Red or Pink
                decayRate = Random.nextDouble(0.02, 0.04).toFloat()
            )
        )
    }
}