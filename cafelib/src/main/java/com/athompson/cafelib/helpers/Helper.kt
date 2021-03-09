package com.athompson.cafelib.helpers

import android.util.DisplayMetrics

object Helper {
    fun displayWidthPixels(): Int {
        val displayMetrics = DisplayMetrics()
        return displayMetrics.widthPixels
    }

    fun displayHeightPixels(): Int {
        val displayMetrics = DisplayMetrics()
        return displayMetrics.widthPixels
    }
}