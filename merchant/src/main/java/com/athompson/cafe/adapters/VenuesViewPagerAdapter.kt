package com.athompson.cafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.VenueDisplayCardBinding
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.Venue


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
            val menu = menusList.find { it.id == vid.selectedMenuId }
            if(menu!=null)
                vid.menu = menu
            else
                vid.menu = null
        }
        notifyDataSetChanged()
    }



    fun getSelectedMenu(currentVenue:Venue):CafeQrMenu?
    {
        menusList.forEach {
            if(it.id.isNotBlank()&&it.id== currentVenue.selectedMenuId)
            {
                return  it
            }
        }
        return null
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val venue = venuesList[position]

        if (holder is VenueViewHolder) {

            if(venue.imageUrl.safe().isNotEmpty())
                GlideLoader(context).loadImagePicture(venue.imageUrl, holder.binding.image)
            else
                holder.binding.image.setImageResource(R.drawable.cafe_image)

            holder.binding.selectedVenueName.text = venue.name
            holder.binding.selectedVenueTown.text = venue.location
            val selectedMenu=getSelectedMenu(venue)
            holder.binding.selectedMenuName.text = selectedMenu?.name?:"No Menu Assigned To Venue"
            holder.binding.selectedMenuDescription.text =  selectedMenu?.name?:""
        }
    }

    override fun getItemCount(): Int {
        return venuesList.size
    }

    fun itemAt(currentItemIndex: Int): Venue? {
        if(venuesList.isNullOrEmpty())
            return null
        if(currentItemIndex >  venuesList.size-1 )
            return null
        return venuesList[currentItemIndex]
    }


    inner class VenueViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: VenueDisplayCardBinding =
            VenueDisplayCardBinding.bind(mView)

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
