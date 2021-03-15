package com.athompson.cafe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.models.Venue

/**
 * A adapter class for dashboard items list.
 */
open class VenueListAdapter(
    private val context: Context,
    private var list: ArrayList<Venue>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.org_list_item,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is OrganisationsListAdapter.OrgViewHolder) {

            GlideLoader(context).loadImagePicture(model.imageUrl, holder.binding.image)

            holder.binding.tvName.text = model.name
            holder.binding.tvAddress1.text = model.address1
            holder.binding.tvAddress2.text = model.address2
            holder.binding.tvCity.text = model.city
            holder.binding.tvEmail.text = model.email
            holder.binding.tvTelephone.text = model.telephone.toString()

            //     holder.itemView.ib_delete_product.setOnClickListener {

            //       fragment.deleteOrganisation(model.product_id)
            //  }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}