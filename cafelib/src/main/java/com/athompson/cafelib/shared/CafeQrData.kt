package com.athompson.cafelib.shared

import com.athompson.cafelib.shared.SharedConstants.CAFE_QR_ID
import com.athompson.cafelib.shared.SharedConstants.ERROR_RETURN
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

data class CafeQrData(val qr: Int = CAFE_QR_ID, val organization: Int, val venue: Int, val table: Int)

fun CafeQrData.valid(): Boolean? {
    if(qr!= CAFE_QR_ID)
        return false
    if(organization== ERROR_RETURN)
        return false
    if(venue== ERROR_RETURN)
        return false
    if(table== ERROR_RETURN)
        return false
    return true
}

fun CafeQrData.toJson(): String? {
    val gson = Gson()
    val jsonString = gson.toJson(this)
    return jsonString
}

fun String.fromJson():CafeQrData? {
    try {
        return Gson().fromJson(this, CafeQrData::class.java)
    }catch (ex:JsonSyntaxException)
    {
        return null
    }
}
