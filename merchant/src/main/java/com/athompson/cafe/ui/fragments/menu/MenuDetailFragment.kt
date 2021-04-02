package com.athompson.cafe.ui.fragments.menu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentMenusDetailsBinding
import com.athompson.cafe.databinding.SimpleFoodItemBinding
import com.athompson.cafe.extensions.ViewExtensions.edit
import com.athompson.cafe.extensions.ViewExtensions.editLong
import com.athompson.cafe.extensions.ViewExtensions.setImage
import com.athompson.cafe.firestore.FireStoreCafeQrMenu
import com.athompson.cafe.firestore.FireStoreFoodMenuItem
import com.athompson.cafe.firestore.FireStoreImage
import com.athompson.cafe.ui.fragments.BaseFragment
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ContextExtensions.themeColor
import com.athompson.cafelib.extensions.FragmentExtensions.logError
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.StringExtensions.trimmed
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.setLayoutManagerVertical
import com.athompson.cafelib.extensions.ViewExtensions.showVerticalDividers
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.extensions.getStoragePermissions
import com.athompson.cafelib.extensions.showImageChooser
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.shared.SharedConstants
import com.google.android.material.transition.MaterialContainerTransform
import java.io.IOException


class MenuDetailFragment : BaseFragment() {
    private var saveMenuIcon: MenuItem? = null
    private var adapter: MenuItemAdapter? = null
    private val args: MenuDetailFragmentArgs by navArgs()
    private val selectedMenu: CafeQrMenu? by lazy(LazyThreadSafetyMode.NONE) { args.selectedMenu }
    private var imageUrl: String?= null
    private val menuFoodItems = ArrayList<FoodMenuItem?>()
    private lateinit var binding: FragmentMenusDetailsBinding
    private var imageFileLocation: Uri? = null
    private var dataChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menus_details, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details_fragment_menus, menu)
        saveMenuIcon = menu.findItem(R.id.save_menu_details)
        saveMenuIcon?.isEnabled = false
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            findNavController().navigateUp()
        }
        if (id == R.id.save_menu_details) {
           save()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenusDetailsBinding.bind(view)

        val menu = selectedMenu
        if (menu == null) {
            showError()
            return
        }

        binding.image.setImage(selectedMenu?.imageUrl,R.drawable.cafe_image)

        toolBarTitle(selectedMenu?.name.safe())
        toolBarSubTitle(selectedMenu?.description.safe())

        binding.name.text = selectedMenu?.name.safe()
        binding.description.text = selectedMenu?.description.safe()
        binding.image.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                getStoragePermissions()
            }
        }

        edit()
        populateMenu()
    }

    private fun showError() {
        // Do nothing
    }

    private fun populateMenu()
    {
        val selected = selectedMenu
        if(selected!=null) {
            showProgressDialog("Getting Menu Items")
            adapter = MenuItemAdapter()
            binding.recycler.setLayoutManagerVertical()
            binding.recycler.itemAnimator = DefaultItemAnimator()
            binding.recycler.showVerticalDividers()
            binding.recycler.adapter = adapter
            FireStoreFoodMenuItem().getAll(selected.id, ::successMenuItem, ::failureMenuItem)
        }
    }

    private fun successMenuItem(m:ArrayList<FoodMenuItem?>)
    {
        hideProgressDialog()
        menuFoodItems.clear()
        menuFoodItems.addAll(m)
        adapter?.dataChanged()
    }

    private fun failureMenuItem(e:Exception)
    {
        hideProgressDialog()
        menuFoodItems.clear()
        adapter?.dataChanged()
        logError(e.toString())
    }

    private fun edit()
    {
        binding.name.setOnClickListener {
            binding.name.edit("Edit Name", "Type to edit name",::onChanged)
        }

        binding.location.setOnClickListener {
            binding.location.edit("Edit Location", "Type to edit location",::onChanged)
        }

        binding.description.setOnClickListener {
            binding.description.editLong("Edit description", "Type to edit description",::onChanged)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onChanged(view:View, oldValue:String?, newValue:String?)
    {
        changed()
    }
    private fun changed()
    {
        dataChanged = hasChanged()
        saveMenuIcon?.isEnabled = dataChanged
    }

    private fun hasChanged():Boolean
    {
        if(selectedMenu?.name.trimmed()!=binding.name.text.toString())
            return true
        if(selectedMenu?.description.trimmed()!=binding.description.trimmed())
            return true
        if(imageFileLocation!=null)
            return true
        return false
    }

    private fun save()
    {
        if (validate()) {
            showProgressDialog(R.string.please_wait.asString())
            val uri = imageFileLocation
            if (uri != null) {
                FireStoreImage().uploadImageToCloudStorage(requireActivity(),uri,
                    SharedConstants.VENUE_IMAGE_SUFFIX,::imageUploadSuccess,::imageUploadFailure)
            } else {
                update()
            }
        }
    }

    private fun imageUploadSuccess(url: String) {

        hideProgressDialog()
        imageUrl = url
        update()
    }

    private fun imageUploadFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }


    private fun validate(): Boolean {
        return when {
            binding.name.text.isEmpty() -> {
                requireActivity().showErrorSnackBar("The menu name cannot be empty", true)
                false
            }
           binding.description.text.isEmpty() -> {
                requireActivity().showErrorSnackBar("The menu description cannot be empty", true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun update() {

        val name = binding.name.text
        if (name != selectedMenu?.name) {
            selectedMenu?.name = name as String
        }
        val description = binding.description.text
        if (description != selectedMenu?.description) {
           selectedMenu?.description = description as String
        }
        if (selectedMenu?.imageUrl != imageUrl && !imageUrl.isNullOrEmpty()) {
            selectedMenu?.imageUrl = imageUrl.toString()
        }
           selectedMenu?.let {
            FireStoreCafeQrMenu().update(::updateSuccess,::updateFailure, selectedMenu?.id.toString(),
                it
            )
        }
    }

    private fun updateSuccess() {
        imageFileLocation = null
        changed()
        hideProgressDialog()
        showShortToast(R.string.msg_menu_update_success)
    }

    private fun updateFailure(e:Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SharedConstants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        imageFileLocation = data.data
                        val url = imageFileLocation
                        if (url != null) {
                            GlideLoader(requireContext()).loadImagePicture(url, binding.image)
                            changed()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showShortToast(R.string.image_selection_failed.asString())
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    inner class MenuItemAdapter() : RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

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
            val menuItem = menuFoodItems[position]
            holder.binding.image.setImage(menuItem?.imageUrl,R.drawable.cafe_image)
            holder.binding.name.text = menuItem?.name
            holder.binding.description.text = menuItem?.description
        }


        override fun getItemCount(): Int {
            return menuFoodItems.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun dataChanged() {
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
