package com.athompson.cafelib.models

import android.os.Parcelable
import com.athompson.cafelib.extensions.StringExtensions.uuid
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0,
    val uid:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(id)
        println(firstName)
        println(lastName)
        println(email)
        println(mobile)
        println(gender)
        println(profileCompleted)
        return super.toString()
    }
}