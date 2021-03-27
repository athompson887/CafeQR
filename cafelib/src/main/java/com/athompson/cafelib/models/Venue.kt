package com.athompson.cafelib.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Venue(

    val name: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val vuid:String = "", //unique identifier for this venue
    val muid:String = "",//menu id
    @Transient
    var menu: CafeQrMenu? = null
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(location)
        println(description)
        println(imageUrl)
        return super.toString()
    }
}