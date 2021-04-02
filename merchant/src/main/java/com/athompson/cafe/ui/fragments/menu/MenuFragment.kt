package com.athompson.cafe.ui.fragments.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentMenuBinding
import com.athompson.cafe.databinding.SimpleMenuItemBinding
import com.athompson.cafe.extensions.ViewExtensions.setImage
import com.athompson.cafe.firestore.FireStoreCafeQrMenu
import com.athompson.cafe.ui.activities.AddMenuActivity
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.FragmentExtensions.logError
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.models.CafeQrMenu
import com.google.android.material.transition.MaterialElevationScale


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
        getMenus()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_food_item) {
            startActivity(Intent(activity, AddMenuActivity::class.java))
            return true
        }
        else if (id == android.R.id.home){
            findNavController().navigateUp()
        }

        return super.onOptionsItemSelected(item)
    }


    private fun getMenus() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreCafeQrMenu().getAll(::getSuccess,::getFailure)
    }

    private fun getFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }

    private fun getSuccess(menuList: ArrayList<CafeQrMenu?>) {
        hideProgressDialog()

        if (menuList.size > 0) {
            binding.recycler.show()
            binding.empty.remove()

            binding.recycler.setLayoutManagerVertical()
            binding.recycler.showVerticalDividers()
            binding.recycler.setHasFixedSize(true)
            binding.recycler.adapter = SimpleMenuAdapter(requireActivity(), menuList)
        } else {
            binding.recycler.remove()
            binding.empty.show()
        }
    }




    private fun showAlertDialogToDeleteMenuItem(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title.asString())
        builder.setMessage(R.string.delete_dialog_message_menu_item)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes.asString()) { dialogInterface, _ ->
            showProgressDialog(R.string.please_wait.asString())
            FireStoreCafeQrMenu().delete(::deleteSuccess,::deleteFailure, productID)
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

    private fun deleteFailure(e: Exception) {
        hideProgressDialog()
        logError(e.message.toString())
    }


    private fun deleteSuccess() {
        hideProgressDialog()
        showShortToast(R.string.menu_delete_success_message.asString())
        getMenus()
    }


    interface OnFragmentInteractionListener{
    }

    inner class SimpleMenuAdapter(
        private val context: Context,
        private var list: ArrayList<CafeQrMenu?>,
    ) : RecyclerView.Adapter<SimpleMenuAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.simple_menu_item,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val menuItem = list[position]
            if(menuItem!=null) {
            holder.binding.image.setImage(menuItem.imageUrl,R.drawable.cafe_image)
            holder.binding.name.text = menuItem.name
            holder.binding.description.text = menuItem.description
            holder.itemView.setOnClickListener {
                exitTransition = MaterialElevationScale(false).apply {
                    duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                }
                reenterTransition = MaterialElevationScale(true).apply {
                    duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                }

                    val trans = getString(R.string.menu_card_detail_transition_name)
                    val extras = FragmentNavigatorExtras(holder.binding.card to trans)
                    val directions =
                        MenuFragmentDirections.actionNavigationMenusToMenuDetailFragment(menuItem)
                    try {
                        findNavController().navigate(directions, extras)
                    } catch (ex: java.lang.Exception) {
                        print(ex)
                    }
                }
            }
        }


        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun dataChanged(cafeQrMenus: ArrayList<CafeQrMenu?>) {
            list = cafeQrMenus
            notifyDataSetChanged()
        }


        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val binding: SimpleMenuItemBinding =
                SimpleMenuItemBinding.bind(mView)

            override fun toString(): String {
                return super.toString() + " '"
            }
        }
    }

}