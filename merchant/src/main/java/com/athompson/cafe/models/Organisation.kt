package com.athompson.cafe.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organisation(
    val userId: String = "",
    var organisationId: String = "",
    val name: String = "",
    val type: String = "",
    val address1: String = "",
    val address2: String = "",
    val city: String = "",
    val email: String = "",
    val telephone: Long = 0
) : Parcelable
{
    override fun toString(): String {
        println(userId)
        println(name)
        println(type)
        println(address1)
        println(address2)
        println(city)
        println(email)
        println(telephone)
        return super.toString()
    }
}