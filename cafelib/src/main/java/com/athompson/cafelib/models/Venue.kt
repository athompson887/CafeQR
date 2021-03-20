package com.athompson.cafelib.models

import android.os.Parcelable
import com.athompson.cafelib.extensions.StringExtensions.uuid
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Venue(

    val name: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val uid:String = ""
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