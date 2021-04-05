package com.athompson.cafe.ui.fragments.venues

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentVenuesBinding
import com.athompson.cafe.databinding.VenuesListItemBinding
import com.athompson.cafe.extensions.ViewExtensions.setImage
import com.athompson.cafe.firestore.FireStoreVenue
import com.athompson.cafe.ui.activities.AddVenuesActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.FragmentExtensions.logError
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.Venue
import com.google.android.material.transition.MaterialElevationScale
import com.myshoppal.utils.SwipeToDeleteCallback


class VenuesFragment : BaseFragment() {

    private lateinit var mRootView: View
    private lateinit var binding:FragmentVenuesBinding
    private var list: ArrayList<Venue> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_venues, container, false)
        return mRootView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVenuesBinding.bind(view)
        toolBarTitle("My Venues")
        toolBarSubTitle("")
        getVenuesListFromFireStore()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.venues_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_venue) {
            startActivity(Intent(activity, AddVenuesActivity::class.java))
            return true
        }
        else if (id == android.R.id.home){
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getVenuesListFromFireStore() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreVenue().getAll(::getSuccess,::getFailure)
    }

    private fun getSuccess(venuesList: ArrayList<Venue>) {
        list.clear()
        list.addAll(venuesList)
        hideProgressDialog()

        if (list.size > 0) {
            binding.recycler.show()
            binding.empty.remove()

            binding.recycler.setLayoutManagerVertical()
            binding.recycler.setHasFixedSize(true)
            binding.recycler.setLayoutManagerVertical()
            binding.recycler.showVerticalDividers()
            binding.recycler.adapter = VenuesListAdapter()
            val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val venue = venuesList[viewHolder.adapterPosition]
                    venue.id?.let { deleteVenue(it,viewHolder.adapterPosition) }
                  }
            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.recycler)

        } else {
            binding.recycler.remove()
            binding.empty.show()
        }
    }

    private fun getFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }


    fun deleteVenue(venueID: String,position: Int) {
        showAlertDialogToDeleteVenue(venueID,position)
    }

    private fun showAlertDialogToDeleteVenue(venueID: String,position: Int) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title.asString())
        builder.setMessage(R.string.delete_dialog_message_venue)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes.asString()) { dialogInterface, _ ->
            showProgressDialog(R.string.please_wait.asString())
            FireStoreVenue().delete(::deleteSuccess,::deleteFailure,venueID,position)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->
            binding.recycler.adapter?.notifyItemChanged(position)
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteSuccess(position: Int) {
        hideProgressDialog()
        showShortToast(R.string.venue_delete_success_message)
        list.removeAt(position)
        binding.recycler.adapter?.notifyItemRemoved(position)
    }

    private fun deleteFailure(e: Exception,position: Int) {
        logError(e.message.toString())
        binding.recycler.adapter?.notifyItemChanged(position)
        hideProgressDialog()
    }


    inner class VenuesListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                holder.binding.image.setImage(venue.imageUrl,R.drawable.cafe_image)
                holder.binding.tvName.text = venue.name
                holder.binding.description.text = venue.description
                holder.binding.tvLocation.text = venue.location
                holder.itemView.setOnClickListener {
                    exitTransition = MaterialElevationScale(false).apply {
                        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                    }
                    reenterTransition = MaterialElevationScale(true).apply {
                        duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                    }
                    val venueCardDetailTransitionName = getString(R.string.venue_card_detail_transition_name)
                    val extras = FragmentNavigatorExtras(holder.binding.card to venueCardDetailTransitionName)
                    val directions =   VenuesFragmentDirections.actionNavigationVenuesToVenueDetailFragment(venue)
                    try {
                        findNavController().navigate(directions, extras)
                    }
                    catch (ex:java.lang.Exception)
                    {
                        print(ex)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }


        inner class VenueViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val binding: VenuesListItemBinding =
                VenuesListItemBinding.bind(mView)

            override fun toString(): String {
                return super.toString() + " '"
            }
        }
    }
}