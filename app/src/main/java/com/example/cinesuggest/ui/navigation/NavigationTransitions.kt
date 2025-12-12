package com.example.cinesuggest.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

object NavigationTransitions {

    private const val ANIMATION_DURATION = 500;

    fun enterFromRight() : (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            initialOffsetX = {it},
            animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(300)
        )
    }

    fun exitToRight() : (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(ANIMATION_DURATION , easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(300)
        )
    }

    fun exitToLeftParallax() : (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            targetOffsetX = { -it / 3},
            animationSpec = tween(ANIMATION_DURATION , easing = FastOutSlowInEasing)
        ) + scaleOut(
            targetScale = 0.92f,
            animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(ANIMATION_DURATION)
        )
    }

    fun enterFromLeftParallax() : (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = tween(ANIMATION_DURATION  , easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.92f,
            animationSpec = tween(ANIMATION_DURATION , easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(ANIMATION_DURATION )
        )
    }
}