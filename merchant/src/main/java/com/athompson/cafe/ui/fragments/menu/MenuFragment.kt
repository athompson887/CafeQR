package com.athompson.cafe.ui.fragments.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.athompson.cafe.R
import com.athompson.cafe.adapters.MenuAdapter
import com.athompson.cafe.databinding.FragmentMenuBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafe.ui.activities.AddOrganisationActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.FoodMenuItem


class MenuFragment : BaseFragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mRootView: View
    private lateinit var binding:FragmentMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_menu, container, false)
        return mRootView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuBinding.bind(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_food_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_food_item) {
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

        getMenuItemsListFromFireStore()
    }

    private fun getMenuItemsListFromFireStore() {
        showProgressDialog(R.string.please_wait.asString())

        FireStoreClass().getMenusList(this@MenuFragment)
    }



    private fun showAlertDialogToDeleteMenuItem(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title.asString())
        builder.setMessage(R.string.delete_dialog_message_menu_item)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes.asString()) { dialogInterface, _ ->
            showProgressDialog(R.string.please_wait.asString())
            FireStoreClass().deleteMenuItem(this@MenuFragment, productID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
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

    fun deleteMenuItem(orgID: String) {
        showAlertDialogToDeleteMenuItem(orgID)
    }

    fun failureMenuList(e: Exception) {

    }

    fun deleteMenuDeleteFailure(e: Exception) {
        hideProgressDialog()
        showShortToast(R.string.menu_delete_success_message.asString())
    }


    fun deleteMenuDeleteSuccess() {
        hideProgressDialog()
        showShortToast(R.string.menu_delete_success_message.asString())
        getMenuItemsListFromFireStore()
    }

    fun successfulMenuList(menuList: ArrayList<FoodMenuItem>) {
        hideProgressDialog()

        if (menuList.size > 0) {
            binding.rvMenuItems.show()
            binding.tvNoFoodItems.remove()

            binding.rvMenuItems.setLayoutManagerVertical()
            binding.rvMenuItems.showVerticalDividers()
            binding.rvMenuItems.setHasFixedSize(true)
            binding.rvMenuItems.adapter = MenuAdapter(requireActivity(), menuList, this@MenuFragment)
        } else {
            binding.rvMenuItems.remove()
            binding.tvNoFoodItems.show()
        }
    }


    interface OnFragmentInteractionListener{
    }
}