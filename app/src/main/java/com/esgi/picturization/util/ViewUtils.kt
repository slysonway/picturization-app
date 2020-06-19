package com.esgi.picturization.util

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.toggle() {
    val transition: Transition = Slide(Gravity.BOTTOM)
    transition.duration = 500
    transition.addTarget(this)

    if (this.visibility == View.INVISIBLE) {
        TransitionManager.beginDelayedTransition(this as ViewGroup, transition)
        this.visibility = View.VISIBLE
    } else {
        TransitionManager.beginDelayedTransition(this as ViewGroup, transition)
        this.visibility = View.INVISIBLE
    }
}

fun View.dismiss() {
    val transition: Transition = Slide(Gravity.BOTTOM)
    transition.duration = 500
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this as ViewGroup, transition)
    this.visibility = View.INVISIBLE
}