package com.athompson.cafe.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.Constants
import com.athompson.cafe.Enums
import com.athompson.cafe.R
import com.athompson.cafelib.models.Venue
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.show

class VenueAdapter(
    private val context: Context,
    private var venues: ArrayList<Venue>,
    private val fragment: DashboardFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectedIndex = 0

    fun dataChanged(data:ArrayList<Venue>)
    {
        venues = data
        venues.add(Venue(Constants.ADD_ITEM_NAME,"","","","","",0,""))
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val it = venues.get(index = position)

        return if (it.organisationId== Constants.ADD_ITEM_NAME) {
            Enums.ITEM_VIEW_TYPE_ADD
        } else
            Enums.ITEM_VIEW_TYPE_ITEM
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Enums.ITEM_VIEW_TYPE_ADD -> DashboardFragment.AddItemViewHolder.from(parent)
            Enums.ITEM_VIEW_TYPE_ITEM -> DashboardFragment.ItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemCount(): Int {
        return venues.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val org = venues[position]
        when (holder) {
            is DashboardFragment.ItemViewHolder -> {
                holder.itemView.setOnClickListener {
                    selectedIndex = position
                    notifyDataSetChanged()
                }
                if(position==selectedIndex) {
                    fragment.setSelectedVenue(org)
                    holder.binding.check.show()
                }
                else {
                    holder.binding.check.hide()
                }
                GlideLoader(context).loadImagePicture(org.imageUrl, holder.binding.image)
                holder.binding.image
                holder.binding.name.text = org.name
                holder.binding.type.text = org.city
            }
            is DashboardFragment.AddItemViewHolder -> {
                holder.binding.add.setOnClickListener {
                    fragment.findNavController().navigate(R.id.action_navigation_dashboard_to_venuesFragment, null, null, null)
                }
            }
        }
    }
}