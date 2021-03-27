package com.athompson.cafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.SimpleVenueItemBinding
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.models.Venue

class SimpleVenueAdapter(
    private val context: Context,
    private var venues: ArrayList<Venue>
) : RecyclerView.Adapter<SimpleVenueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.simple_venue_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val venueItem = venues[position]
        if(venueItem.imageUrl.safe().isNotEmpty())
            GlideLoader(context).loadImagePicture(venueItem.imageUrl, holder.binding.image)
        else
            holder.binding.image.setImageResource(R.drawable.cafe_image)

        holder.binding.name.text = venueItem.name
        holder.binding.location.text = venueItem.location
    }


    override fun getItemCount(): Int {
        return venues.size
    }

    fun dataChanged(cafeQrMenus: ArrayList<Venue>) {
        venues = cafeQrMenus
        notifyDataSetChanged()
    }


    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: SimpleVenueItemBinding =
            SimpleVenueItemBinding.bind(mView)

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
