package com.athompson.cafelib.models

import android.os.Parcelable
import com.athompson.cafelib.extensions.StringExtensions.uuid
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CafeQrMenu(
    val name: String = "",
    val description: String = "",
    var uid:String = "",
    var imageUrl:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(description)
        return super.toString()
    }
}