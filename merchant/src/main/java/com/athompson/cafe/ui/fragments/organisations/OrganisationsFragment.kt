package com.athompson.cafe.ui.fragments.organisations

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.athompson.cafe.R
import com.athompson.cafe.adapters.OrganisationsListAdapter
import com.athompson.cafe.databinding.FragmentOrganisationsBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafelib.firestore.FireStoreClassShared
import com.athompson.cafelib.models.Organisation
import com.athompson.cafe.ui.activities.AddOrganisationActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.show
import java.lang.Exception


class OrganisationsFragment : BaseFragment() {

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
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        getOrganisationsListFromFireStore()
    }

    private fun getOrganisationsListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(R.string.please_wait.asString())

        // Call the function of Firestore class.
        FireStoreClassShared().getOrganisationList(this@OrganisationsFragment)
    }

    fun successfulOrganisationsList(productsList: ArrayList<Organisation>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (productsList.size > 0) {
            binding. rvMyProductItems.show()
            binding.tvNoProductsFound.remove()

            binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductItems.setHasFixedSize(true)

            val adapterProducts =
                OrganisationsListAdapter(requireActivity(), productsList, this@OrganisationsFragment)
            binding.rvMyProductItems.adapter = adapterProducts
        } else {
            binding.rvMyProductItems.remove()
            binding.tvNoProductsFound.show()
        }
    }

    /**
     * A function that will call the delete function of FirestoreClass that will delete the product added by the user.
     *
     * @param productID To specify which product need to be deleted.
     */
    fun deleteProduct(productID: String) {

        // TODO Step 6: Remove the toast message and call the function to ask for confirmation to delete the product.
        // START
        // Here we will call the delete function of the FirestoreClass. But, for now lets display the Toast message and call this function from adapter class.

        /*Toast.makeText(
            requireActivity(),
            "You can now delete the product. $productID",
            Toast.LENGTH_SHORT
        ).show()*/

        showAlertDialogToDeleteProduct(productID)
        // END
    }


    fun deleteOrganisationDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            R.string.organisation_delete_success_message.asString(),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest products list from cloud firestore.
        getOrganisationsListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(R.string.delete_dialog_title.asString())
        //set message for alert dialog
        builder.setMessage(R.string.delete_dialog_message_organisation)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(R.string.yes.asString()) { dialogInterface, _ ->

            // TODO Step 7: Call the function to delete the product from cloud firestore.
            // START
            // Show the progress dialog.
            showProgressDialog(R.string.please_wait.asString())

            // Call the function of Firestore class.
            FireStoreClass().deleteOrganisation(this@OrganisationsFragment, productID)
            // END

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



    fun deleteOrganisationDeleteFailure(e: Exception) {

    }
}