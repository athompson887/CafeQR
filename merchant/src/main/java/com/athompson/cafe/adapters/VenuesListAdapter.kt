package com.athompson.cafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentVenuesListItemBinding
import com.athompson.cafe.ui.fragments.venues.VenuesFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.models.Venue


open class VenuesListAdapter(
    private val context: Context,
    private var list: ArrayList<Venue>,
    private val fragment: VenuesFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VenueViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.fragment_venues_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is VenueViewHolder) {

            GlideLoader(context).loadImagePicture(model.imageUrl, holder.binding.image)
            holder.binding.tvName.text = model.name
            holder.binding.tvAddress1.text = model.address1
            holder.binding.tvAddress2.text = model.address2
            holder.binding.tvCity.text = model.city
            holder.binding.tvEmail.text = model.email
            holder.binding.tvTelephone.text = model.telephone.toString()

        //    holder.itemView.ib_delete_product.setOnClickListener {

        //        fragment.deleteVenue(model.product_id)
        //    }
        }
    }


    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }


    inner class VenueViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: FragmentVenuesListItemBinding =
            FragmentVenuesListItemBinding.bind(mView)

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
