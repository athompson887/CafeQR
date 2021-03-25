package com.athompson.cafe.adapters

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.VenueDisplayCardBinding
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.Venue
import kotlinx.android.parcel.Parcelize


open class VenuesViewPagerAdapter(
    private val context: Context,
    private var venuesList: ArrayList<Venue>,
    private var menusList: ArrayList<CafeQrMenu>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VenueViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.venue_display_card,
                parent,
                false
            )
        )
    }

    fun dataChanged() {

        venuesList.forEach {  vid ->
            val menu = menusList.find { it.uid == vid.uid }
            if(menu!=null)
                vid.menu = menu
            else
                vid.menu = null
        }
        notifyDataSetChanged()

    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val venue = venuesList[position]

        if (holder is VenueViewHolder) {

            GlideLoader(context).loadImagePicture(venue.imageUrl, holder.binding.selectedVenueImage)
            holder.binding.selectedVenueName.text = venue.name
            holder.binding.selectedVenueTown.text = venue.location
            holder.binding.selectedMenuName.text = "Not Known"
            holder.binding.selectedMenuDescription.text = "Not Known Description"
        }
    }


    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return venuesList.size
    }


    inner class VenueViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: VenueDisplayCardBinding =
            VenueDisplayCardBinding.bind(mView)

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
