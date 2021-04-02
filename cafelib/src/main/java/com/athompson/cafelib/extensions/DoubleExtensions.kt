package com.athompson.cafelib.extensions
import com.athompson.cafelib.extensions.DoubleExtensions.DoubleFromCurrency
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

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

    fun String.priceValue(): Double {
        var res = this.trimStart('£').toDoubleOrNull()
        if(res==null)
            res=0.0
        return res
    }
    fun Double.toPrice(): String {
        val format = NumberFormat.getCurrencyInstance()
        format.currency = Currency.getInstance("GBP")
        return format.format(this)
     }

    fun String.DoubleFromCurrency(): Double {
        val res = this.trimStart('£').toDoubleOrNull()
        return res?:0.0
    }
}