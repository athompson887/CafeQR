package com.athompson.cafe.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.Constants
import com.athompson.cafe.Enums
import com.athompson.cafe.ui.fragments.dashboard.DashboardFragment
import com.athompson.cafe.ui.fragments.menu.MenuFragment
import com.athompson.cafelib.models.FoodMenuItem

open class MenuAdapter(
    private val context: Context,
    private var menus: ArrayList<FoodMenuItem>,
    private val fragment: MenuFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun dataChanged(data:ArrayList<FoodMenuItem>)
    {
        menus.clear()
        menus.addAll(data)
        notifyDataSetChanged()
        menus.add(FoodMenuItem(Constants.ADD_ITEM_NAME,"",""))
    }

    override fun getItemViewType(position: Int): Int {
        val it = menus.get(index = position)

        return if (it.uid== Constants.ADD_ITEM_NAME) {
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
        val org = menus[position]
        when (holder) {
            is DashboardFragment.ItemViewHolder -> {
                holder.itemView.setOnClickListener { }
                holder.binding.check.setOnClickListener { }
                holder.binding.name.text = org.name
                holder.binding.type.text = org.description
            }
            is DashboardFragment.AddItemViewHolder -> {
                holder.itemView.setOnClickListener {
                    //call activity to add organisation
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return menus.size
    }
}
