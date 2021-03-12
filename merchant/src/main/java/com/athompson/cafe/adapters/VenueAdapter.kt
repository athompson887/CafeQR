package com.athompson.cafe.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.Constants
import com.athompson.cafe.Enums
import com.athompson.cafe.models.Venue
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment

class VenueAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var venues: ArrayList<Venue> = ArrayList()

    fun dataChanged(data:ArrayList<Venue>)
    {
        venues.clear()
        venues.addAll(data)
        notifyDataSetChanged()
        venues.add(Venue(Constants.ADD_ITEM_NAME,"","","","","","",0))
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
                holder.itemView.setOnClickListener { }
                holder.binding.check.setOnClickListener { }
                holder.binding.name.text = org.name
                holder.binding.type.text = org.city
            }
            is DashboardFragment.AddItemViewHolder ->{
                holder.itemView.setOnClickListener {
                    //call activity to add venue
                }
            }
        }
    }
}