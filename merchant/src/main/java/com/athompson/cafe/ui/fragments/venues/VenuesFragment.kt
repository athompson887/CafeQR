package com.athompson.cafe.ui.fragments.venues

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentVenuesBinding
import com.athompson.cafe.databinding.VenuesListItemBinding
import com.athompson.cafe.extensions.ViewExtensions.setImage
import com.athompson.cafe.firestore.FireStoreVenue
import com.athompson.cafe.ui.activities.AddVenuesActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.FragmentExtensions.logError
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.Venue
import com.google.android.material.transition.MaterialElevationScale


class VenuesFragment : BaseFragment() {

    private lateinit var mRootView: View
    private lateinit var binding:FragmentVenuesBinding
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

        hideProgressDialog()

        if (venuesList.size > 0) {
            binding.recycler.show()
            binding.empty.remove()

            binding.recycler.setLayoutManagerVertical()
            binding.recycler.setHasFixedSize(true)
            binding.recycler.setLayoutManagerVertical()
            binding.recycler.showVerticalDividers()
            binding.recycler.adapter = VenuesListAdapter(requireActivity(), venuesList)
        } else {
            binding.recycler.remove()
            binding.empty.show()
        }
    }

    private fun getFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }


    fun deleteVenue(venueID: String) {
        showAlertDialogToDeleteVenue(venueID)
    }

    private fun showAlertDialogToDeleteVenue(venueID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title.asString())
        builder.setMessage(R.string.delete_dialog_message_venue)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes.asString()) { dialogInterface, _ ->
            showProgressDialog(R.string.please_wait.asString())
            FireStoreVenue().delete(::deleteSuccess,::deleteFailure,venueID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteSuccess() {
        hideProgressDialog()
        showShortToast(R.string.venue_delete_success_message)
        getVenuesListFromFireStore()
    }

    private fun deleteFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }


    inner class VenuesListAdapter(
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