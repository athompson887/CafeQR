package com.athompson.cafe.ui.fragments.organisations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.athompson.cafe.R
import com.athompson.cafe.adapters.OrganisationsListAdapter
import com.athompson.cafe.databinding.FragmentOrganisationsBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafe.ui.activities.AddOrganisationActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.Organisation


class OrganisationsFragment : BaseFragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mRootView: View
    private lateinit var binding:FragmentOrganisationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_organisations, container, false)
        return mRootView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrganisationsBinding.bind(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_organisation_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_organisation) {
            startActivity(Intent(activity, AddOrganisationActivity::class.java))
            return true
        }
        else if (id == android.R.id.home){
            findNavController().navigateUp()
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        getOrganisationsListFromFireStore()
    }

    private fun getOrganisationsListFromFireStore() {
        showProgressDialog(R.string.please_wait.asString())

        FireStoreClass().getOrganisationList(this@OrganisationsFragment)
    }

    fun successfulOrganisationsList(productsList: ArrayList<Organisation>) {

        hideProgressDialog()

        if (productsList.size > 0) {
            binding.rvOrganisations.show()
            binding.tvNoOrganisations.remove()

            binding.rvOrganisations.setLayoutManagerVertical()
            binding.rvOrganisations.showVerticalDividers()
            binding.rvOrganisations.setHasFixedSize(true)
            binding.rvOrganisations.adapter = OrganisationsListAdapter(requireActivity(), productsList, this@OrganisationsFragment)
        } else {
            binding.rvOrganisations.remove()
            binding.tvNoOrganisations.show()
        }
    }


    fun deleteOrganisation(orgID: String) {
        showAlertDialogToDeleteOrganisation(orgID)
    }

    fun deleteOrganisationDeleteSuccess() {

        hideProgressDialog()
        showShortToast(R.string.organisation_delete_success_message.asString())
        getOrganisationsListFromFireStore()
    }

    private fun showAlertDialogToDeleteOrganisation(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title.asString())
        builder.setMessage(R.string.delete_dialog_message_organisation)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes.asString()) { dialogInterface, _ ->
            showProgressDialog(R.string.please_wait.asString())
            FireStoreClass().deleteOrganisation(this@OrganisationsFragment, productID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



    fun deleteOrganisationDeleteFailure(e: Exception) {

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener{
        fun hideNavBar()
        fun showNavBar()
    }
}