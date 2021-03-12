package com.athompson.cafe.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MenuItem(
    var organisationId: String = "",
    var venueId: String = "",
    var menuId: String = "",
    var shared:Boolean = true,
    val name: String = "",
    val description: String = "",
    val image: String = "",
) : Parcelable
{
    override fun toString(): String {
        println(organisationId)
        println(venueId)
        println(menuId)
        println(shared)
        println(name)
        println(description)
        println(image)
        return super.toString()
    }
}