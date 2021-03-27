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
    var menuId:String = "",
    @Transient
    var id:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(type)
        println(description)
        println(imageUrl)
        println(price)
        println(menuId)
        println(id)
        return super.toString()
    }
}