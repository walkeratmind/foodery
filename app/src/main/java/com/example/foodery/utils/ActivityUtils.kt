package com.example.foodery.utils

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


// Show Toast from any Activity
fun  Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, message, duration).show()
}

fun Activity.showSnackbar(view: View,message: String, duration: Int=Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, message, duration).show()
}

fun Activity.getColorRes(@ColorRes id: Int) = ContextCompat.getColor(applicationContext, id)
