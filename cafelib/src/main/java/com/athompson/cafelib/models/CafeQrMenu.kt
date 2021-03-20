package com.athompson.cafelib.models

import android.os.Parcelable
import com.athompson.cafelib.extensions.StringExtensions.uuid
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CafeQrMenu(
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    var uid:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(userId)
        println(name)
        println(description)
        return super.toString()
    }
}