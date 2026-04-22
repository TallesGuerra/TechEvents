package com.example.techevents.utils

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
