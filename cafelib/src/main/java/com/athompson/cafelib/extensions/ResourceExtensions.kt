package com.athompson.cafelib.extensions

import androidx.core.content.ContextCompat
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.shared.CafeQRApplication

object ResourceExtensions {

    fun Int.asColor() = ContextCompat.getColor(CafeQRApplication.appInstance, this)
    fun Int.asString() = CafeQRApplication.appInstance.getString(this).safe().trim()
    fun Int.asDrawable() = ContextCompat.getDrawable(CafeQRApplication.appInstance, this)
}