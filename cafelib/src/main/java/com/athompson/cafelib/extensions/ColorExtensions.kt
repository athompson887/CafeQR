package com.athompson.cafelib.extensions

import android.graphics.Color

object ColorExtensions {

    fun String.hexToRGB() : Triple<String, String, String>{
        var name = this
        if (!name.startsWith("#")){
            name = "#$this"
        }
        val color = Color.parseColor(name)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return Triple(red.toString(), green.toString(), blue.toString())
    }

    fun Int.colorToHexString(): String {
        val data = String.format("#%06X", -0x1 and this).replace("#FF","#")
        return data
    }
}