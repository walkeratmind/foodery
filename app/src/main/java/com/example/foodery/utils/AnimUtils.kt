package com.example.foodery.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.example.foodery.ui.main.view.main.MainActivity

fun View.fadeOut(
    interpolator: Interpolator = AccelerateDecelerateInterpolator(),
    animDelay: Long = 500L,
    animDuration: Long = 1000L
) {
    animate()
        .alpha(0f)
        .setInterpolator(interpolator)
        .setStartDelay(animDelay)
        .setDuration(animDuration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                hide()
            }
        })
}

fun View.fadeIn(
    interpolator: Interpolator = AccelerateDecelerateInterpolator(),
    animDelay: Long = 1000L,
    animDuration: Long = 1000L
) {
    animate()
        .alpha(1f)
        .setInterpolator(interpolator)
        .setStartDelay(animDelay)
        .setDuration(animDuration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                show()
            }
        })
}

fun View.fadeInFadeOut(
    interpolator: Interpolator = AccelerateDecelerateInterpolator(),
    animDelay: Long = 300L,
    animDuration: Long = 1000L
) {
    animate()
        .alpha(1f)
        .setInterpolator(interpolator)
        .setStartDelay(animDelay)
        .setDuration(animDuration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                show()
            }

            override fun onAnimationEnd(animation: Animator?) {
                hide()
            }
        })
}