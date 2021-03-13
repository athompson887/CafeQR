package com.athompson.cafelib.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Menu(
    var organisationId: String = "",
    var venueId: String = "",
    val name: String = "",
    val description: String = "",
) : Parcelable
{
    override fun toString(): String {
        println(organisationId)
        println(venueId)
        println(name)
        println(description)
        return super.toString()
    }
}