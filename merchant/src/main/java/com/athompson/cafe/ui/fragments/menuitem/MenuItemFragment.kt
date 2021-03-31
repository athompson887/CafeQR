package com.athompson.cafe.ui.fragments.menuitem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.athompson.cafe.R
import com.athompson.cafe.adapters.MenusViewPagerAdapter
import com.athompson.cafe.databinding.FragmentMenuItemsBinding
import com.athompson.cafe.databinding.SimpleFoodItemBinding
import com.athompson.cafe.firestore.FireStoreMenu
import com.athompson.cafe.firestore.FireStoreMenuItem
import com.athompson.cafe.ui.activities.AddMenuItemActivity
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
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.FoodMenuItem
import com.google.android.material.transition.MaterialElevationScale


class MenuItemFragment : BaseFragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mRootView: View
    private lateinit var binding:FragmentMenuItemsBinding
    private var foodItems: ArrayList<FoodMenuItem?> = ArrayList()
    private var cafeQrMenus: ArrayList<CafeQrMenu?> = ArrayList()
    private var selectedMenu:CafeQrMenu? = null
    private lateinit var simpleMenuItemAdapter:SimpleMenuItemAdapter
    private lateinit var menusViewPagerAdapter: MenusViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_menu_items, container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuItemsBinding.bind(view)

        setupRecycler()
        getMenusList()
    }

    private fun getMenusList() {
        showProgressDialog(R.string.please_wait.asString())
        FireStoreMenu().getMenus(::successfulCafeQrMenuList,::failureCafeQrMenuList)
    }

    private fun successfulCafeQrMenuList(menuList: ArrayList<CafeQrMenu?>) {
        if (menuList.size > 0) {
            cafeQrMenus.clear()
            cafeQrMenus.addAll(menuList)
        } else {
            addBlankItem()
        }
        menusViewPagerAdapter.dataChanged()
        hideProgressDialog()
    }

    private fun failureCafeQrMenuList(e: Exception) {
        logError(e.toString())
        hideProgressDialog()
        addBlankItem()
    }

    private fun addBlankItem()
    {
        cafeQrMenus.clear()
        cafeQrMenus.add(CafeQrMenu("You have no menus","Click to creat a menu","",""))
        menusViewPagerAdapter.dataChanged()
    }

    fun getSelectedMenu(position: Int):CafeQrMenu?
    {
        return menusViewPagerAdapter.itemAt(position)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_food_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_food_item) {
            if(selectedMenu==null)
            {
                showShortToast("You cannot add a menu item until you have created a menu")
            }
            else {

                val intent = Intent(activity, AddMenuItemActivity::class.java)
                intent.putExtra("menuID", selectedMenu?.id)
                startActivity(intent)
            }
            return true
        }
        else if (id == android.R.id.home){
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecycler()
    {
        menusViewPagerAdapter = MenusViewPagerAdapter(requireContext(),cafeQrMenus) //for displaying menus
        binding.viewPager.adapter = menusViewPagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                println(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println(position)
            }


            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectedMenu = getSelectedMenu(position)
                toolBarTitle(selectedMenu?.name.safe())
                toolBarSubTitle(selectedMenu?.description.safe())
                getMenuItemsFireStore()
            }
        })

        simpleMenuItemAdapter = SimpleMenuItemAdapter(requireContext(),foodItems)
        binding.recycler.setLayoutManagerVertical()
        binding.recycler.itemAnimator = DefaultItemAnimator()
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.HORIZONTAL
            )
        )
        binding.recycler.adapter = simpleMenuItemAdapter

    }

    private fun getMenuItemsFireStore() {
        val selected = selectedMenu
        if(selected!=null) {
            showProgressDialog(R.string.please_wait.asString())
            FireStoreMenuItem().getMenuItems(selected.id, ::getSuccess, ::getFailure)
        }
    }

    private fun getFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }

    private fun getSuccess(menuList: ArrayList<FoodMenuItem?>) {
        hideProgressDialog()

        if (menuList.isNotEmpty()) {
            binding.recycler.show()
            binding.empty.remove()

            binding.recycler.setLayoutManagerVertical()
            binding.recycler.showVerticalDividers()
            binding.recycler.setHasFixedSize(true)
            binding.recycler.adapter = SimpleMenuItemAdapter(requireActivity(), menuList)
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
            FireStoreMenu().deleteCafeQrMenu(::deleteSuccess,::deleteFailure, productID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.no.asString()) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    fun deleteMenuItem(id: String) {
        showAlertDialogToDeleteMenuItem(id)
    }

    private fun deleteFailure(e: Exception) {
        hideProgressDialog()
        showShortToast(R.string.menu_delete_success_message.asString())
    }


    private fun deleteSuccess() {
        hideProgressDialog()
        showShortToast(R.string.menu_delete_success_message.asString())
        getMenuItemsFireStore()
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
    }

    inner class SimpleMenuItemAdapter(
        private val context: Context,
        private var list: ArrayList<FoodMenuItem?>) : RecyclerView.Adapter<SimpleMenuItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.simple_food_item,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val menuItem = list[position]

            if (menuItem?.imageUrl.safe().isNotEmpty())
                GlideLoader(context).loadImagePicture(
                    menuItem?.imageUrl.safe(),
                    holder.binding.image
                )
            else
                holder.binding.image.setImageResource(R.drawable.cafe_image)

            holder.binding.name.text = menuItem?.name
            holder.binding.description.text = menuItem?.description

            holder.itemView.setOnClickListener {
                if (menuItem != null) {
                    exitTransition = MaterialElevationScale(false).apply {
                        duration =
                            resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                    }
                    reenterTransition = MaterialElevationScale(true).apply {
                        duration =
                            resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                    }
                    val trans = getString(R.string.food_card_detail_transition_name)
                    val extras = FragmentNavigatorExtras(holder.binding.card to trans)
                    val directions =
                        MenuItemFragmentDirections.actionNavigationFoodMenuToFoodDetailFragment(
                            menuItem
                        )
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

        fun dataChanged(items: ArrayList<FoodMenuItem?>) {
            list = items
            notifyDataSetChanged()
        }


        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val binding: SimpleFoodItemBinding =
                SimpleFoodItemBinding.bind(mView)

            override fun toString(): String {
                return super.toString() + " '"
            }
        }
    }
}