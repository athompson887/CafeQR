package com.athompson.cafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.VenuesListItemBinding
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.models.Venue


open class VenuesListAdapter(
    private val context: Context,
    private var list: ArrayList<Venue>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VenueViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.venues_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val venue = list[position]

        if (holder is VenueViewHolder) {
            GlideLoader(context).loadImagePicture(venue.imageUrl, holder.binding.image)
            holder.binding.tvName.text = venue.name
            holder.binding.description.text = venue.description
            holder.binding.tvLocation.text = venue.location
            venue.location
        }
    }


    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }


    inner class VenueViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: VenuesListItemBinding =
            VenuesListItemBinding.bind(mView)

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
