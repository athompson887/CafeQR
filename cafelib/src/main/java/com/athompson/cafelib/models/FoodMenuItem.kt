package com.athompson.cafelib.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodMenuItem(
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val price:Double = 0.0,
    val oid:String = "",
    val vid:String = "",
    var uid:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(type)
        println(description)
        return super.toString()
    }
}