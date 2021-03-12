package com.athompson.cafe.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.Constants
import com.athompson.cafe.Enums
import com.athompson.cafe.models.Organisation
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment

class OrganisationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var organisations: ArrayList<Organisation> = ArrayList()

    fun dataChanged(data:ArrayList<Organisation>)
    {
        organisations.clear()
        organisations.addAll(data)
        notifyDataSetChanged()
        organisations.add(Organisation(Constants.ADD_ITEM_NAME,"","","","","","","",0))
    }

    override fun getItemViewType(position: Int): Int {
        val it = organisations.get(index = position)

        return if (it.userId== Constants.ADD_ITEM_NAME) {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val org = organisations[position]
        when (holder) {
            is DashboardFragment.ItemViewHolder -> {
                holder.itemView.setOnClickListener { }
                holder.binding.check.setOnClickListener { }
                holder.binding.name.text = org.name
                holder.binding.type.text = org.type
            }
            is DashboardFragment.AddItemViewHolder -> {
                holder.itemView.setOnClickListener {
                    //call activity to add organisation
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return organisations.size
    }
}
