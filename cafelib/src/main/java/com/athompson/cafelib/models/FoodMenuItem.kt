package com.athompson.cafelib.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class FoodMenuItem(
    var name: String = "",
    var type: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var price:Double = 0.0,
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