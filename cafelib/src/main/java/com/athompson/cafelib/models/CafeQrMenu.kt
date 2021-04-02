package com.athompson.cafelib.models

import android.os.Parcelable
import com.athompson.cafelib.extensions.StringExtensions.uuid
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CafeQrMenu(
    var name: String = "",
    var description: String = "",
    var imageUrl:String = "",
    @Transient
    var id:String = ""
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(description)
        print(imageUrl)
        print(id)
        return super.toString()
    }
}