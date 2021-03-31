package com.athompson.cafelib.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
 object VenueExtensions{
     fun Venue.Copy(): Venue {
         return Venue(name,location,description,imageUrl,selectedMenuId,selectedTheme)
     }
     fun Venue.CopyFields(from:Venue) {
         name = from.name
         location = from.location
         description = from.description
         imageUrl = from.imageUrl
         selectedMenuId = from.selectedMenuId
         selectedTheme = from.selectedTheme
     }
 }

@Parcelize
data class Venue(

    var name: String = "",
    var location: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var selectedMenuId:String = "",
    var selectedTheme: String = "",
    @Transient
    var id: String? = null, //unique identifier for this venue
    @Transient
    var menu: CafeQrMenu? = null
) : Parcelable
{
    override fun toString(): String {
        println(name)
        println(location)
        println(description)
        println(imageUrl)
        println(selectedMenuId)
        println(id)
        return super.toString()
    }


}