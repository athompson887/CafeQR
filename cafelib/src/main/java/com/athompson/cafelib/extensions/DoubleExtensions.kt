package com.athompson.cafelib.extensions
import java.math.RoundingMode
import java.text.DecimalFormat

object DoubleExtensions {

    fun Double.toTwoDecimalPlaces(): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.UP
        val res = df.format(this)
        return res.toDouble()
    }

    fun Double?.toTwoDecimalPlaces(): Double {
        if(this==null)
            return 0.0
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.UP
        val res = df.format(this)
        return res.toDouble()
    }

    fun Double?.safe(): Double {
        if(this==null)
            return 0.0
        return this
    }
}