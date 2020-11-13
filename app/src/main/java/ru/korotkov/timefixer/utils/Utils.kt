package ru.korotkov.timefixer.utils

import android.transition.Slide
import android.view.Gravity
import android.view.Window

fun setAnimation(window: Window) {
    val slide = Slide()
    slide.slideEdge = Gravity.END
    window.exitTransition = slide
    window.enterTransition = slide
}
