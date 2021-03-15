package com.athompson.cafelib.models

import android.os.Parcelable
import com.athompson.cafelib.extensions.StringExtensions.uuid
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Venue(
    var organisationId: String = "",
    val name: String = "",
    val address1: String = "",
    val address2: String = "",
    val city: String = "",
    val email: String = "",
    val telephone: Long = 0,
    val imageUrl: String = "",
    val uid:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(organisationId)
        println(name)
        println(address1)
        println(address2)
        println(city)
        println(email)
        println(telephone)
        return super.toString()
    }
}