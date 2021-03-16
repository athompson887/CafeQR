package com.athompson.cafe.ui.fragments.venues

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.athompson.cafe.R
import com.athompson.cafe.adapters.VenuesListAdapter
import com.athompson.cafe.databinding.FragmentVenuesBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafe.ui.activities.AddOrganisationActivity
import com.athompson.cafe.ui.activities.AddVenuesActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.Venue


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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_venues_menu, menu)
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

    override fun onResume() {
        super.onResume()
        getVenuesListFromFireStore()
    }

    private fun getVenuesListFromFireStore() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreClass().getVenueItemsList(this@VenuesFragment)
    }

    fun successfulVenuesList(venuesList: ArrayList<Venue>) {

        hideProgressDialog()

        if (venuesList.size > 0) {
            binding.rvVenues.show()
            binding.tvNoVenues.remove()

            binding.rvVenues.setLayoutManagerVertical()
            binding.rvVenues.setHasFixedSize(true)
            binding.rvVenues.setLayoutManagerVertical()
            binding.rvVenues.showVerticalDividers()
            binding.rvVenues.adapter = VenuesListAdapter(requireActivity(), venuesList, this@VenuesFragment)
        } else {
            binding.rvVenues.remove()
            binding.tvNoVenues.show()
        }
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
            FireStoreClass().deleteVenue(this@VenuesFragment, venueID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun deleteVenueDeleteSuccess() {

        hideProgressDialog()
        showShortToast(R.string.venue_delete_success_message)
        getVenuesListFromFireStore()
    }

    fun deleteVenueDeleteFailure(e: Exception) {

    }

    fun failureVenueList(e: Exception) {

    }
}