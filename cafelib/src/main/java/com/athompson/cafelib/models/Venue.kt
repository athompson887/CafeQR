package com.athompson.cafelib.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Venue(

    val name: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val selectedMenuId:String = "",//menu id
    @Transient
    var id:String = "", //unique identifier for this venue
    @Transient
    var menu: CafeQrMenu? = null
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(location)
        println(description)
        println(imageUrl)
        println(selectedMenuId)
        println(id)
        return super.toString()
    }
}