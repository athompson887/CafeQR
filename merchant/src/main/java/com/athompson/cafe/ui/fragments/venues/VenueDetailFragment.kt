package com.athompson.cafe.ui.fragments.venues

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.athompson.cafe.Constants
import com.athompson.cafe.R
import com.athompson.cafe.adapters.SimpleMenuItemAdapter
import com.athompson.cafe.databinding.FragmentVenuesDetailsBinding
import com.athompson.cafe.extensions.ViewExtensions.Edit
import com.athompson.cafe.extensions.ViewExtensions.EditLong
import com.athompson.cafe.firestore.FireStoreMenu
import com.athompson.cafe.firestore.FireStoreMenuItem
import com.athompson.cafe.firestore.FireStoreVenue
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
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.models.VenueExtensions.Copy
import com.athompson.cafelib.models.VenueExtensions.CopyFields
import com.athompson.cafelib.shared.SharedConstants
import com.google.android.material.transition.MaterialContainerTransform
import java.io.IOException


class VenueDetailFragment : BaseFragment(){
    private var originalVenue: Venue? = null
    private var selectedTheme: String? = "Dark"
    private var adapter: SimpleMenuItemAdapter? = null
    private val args: VenueDetailFragmentArgs by navArgs()
    private val selectedVenue: Venue? by lazy(LazyThreadSafetyMode.NONE) { args.selectedVenue }
    private var selectedMenu:CafeQrMenu? = null
    private val menuListName = ArrayList<String?>()
    private var menus = ArrayList<CafeQrMenu?>()
    private val menuFoodItems = ArrayList<FoodMenuItem?>()
    private lateinit var binding: FragmentVenuesDetailsBinding
    private var imageFileLocation: Uri? = null
    private var mVenueImageUrl: String = ""
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
        return inflater.inflate(R.layout.fragment_venues_details, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVenuesDetailsBinding.bind(view)

        binding.btnUpdate.isEnabled = false
        val venue = selectedVenue
        if (venue == null) {
            showError()
            return
        }
        originalVenue = venue.Copy()
        if (venue.imageUrl.safe().isNotEmpty())
            GlideLoader(requireContext()).loadImagePicture(venue.imageUrl, binding.image)
        else
            binding.image.setImageResource(R.drawable.cafe_image)

        toolBarTitle(venue.name)
        toolBarSubTitle(venue.location)

        binding.name.text = venue.name
        binding.location.text = venue.location
        binding.description.text = venue.description

        binding.btnUpdate.setOnClickListener {
            save()
        }


        val themesAdaptor = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, SharedConstants.THEMES)
        binding.themesTypesAutoComplete.setAdapter(themesAdaptor)

        binding.themesTypesAutoComplete.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, position: Int, id: Long ->
            selectedTheme = SharedConstants.THEMES[position]
            changed()
        }

        binding.themesTypesAutoComplete.setText(selectedTheme,false)

        binding.image.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }
        edit()
        populateMenus()

    }

    fun showImageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    private fun edit()
    {
        binding.name.setOnClickListener {
            binding.name.Edit("Edit Name", "Type to edit name",::onChanged)
        }

        binding.location.setOnClickListener {
            binding.location.Edit("Edit Location", "Type to edit location",::onChanged)
        }

        binding.description.setOnClickListener {
            binding.description.EditLong("Edit description", "Type to edit description",::onChanged)
        }
    }

    private fun onChanged(view:View,oldValue:String?,newValue:String?)
    {
       changed()
    }
    private fun changed()
    {
        dataChanged = hasChanged()
        binding.btnUpdate.isEnabled = dataChanged
    }

    private fun hasChanged():Boolean
    {
        if(originalVenue?.name.trimmed()!=binding.name.text.toString())
            return true
        if(originalVenue?.location.trimmed()!=binding.location.trimmed())
            return true
        if(originalVenue?.description.trimmed()!=binding.description.trimmed())
            return true
        if(originalVenue?.selectedMenuId.trimmed()!=selectedMenu?.id)
            return true
        if(originalVenue?.selectedTheme.trimmed()!=selectedTheme)
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
                FireStoreVenue().uploadImage(::imageUploadSuccess,::imageUploadFailure, uri,requireActivity())
            } else {
                update()
            }
        }
    }

    private fun imageUploadSuccess(imageURL: String) {

        hideProgressDialog()
        mVenueImageUrl = imageURL
        update()
    }

    private fun imageUploadFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }


    private fun validate(): Boolean {
        return when {
            binding.name.text.isEmpty() -> {
                  requireActivity().showErrorSnackBar("The venue name cannot be empty", true)
                false
            }
            binding.location.text.isEmpty() -> {
                requireActivity().showErrorSnackBar("The venue location cannot be empty", true)
                false
            }
            binding.description.text.isEmpty() -> {
                requireActivity().showErrorSnackBar("The venue description cannot be empty", true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun update() {

        val map = HashMap<String, Any>()

        val name = binding.name.text
        if (name != selectedVenue?.name) {
            map[Constants.VENUE_NAME] = name
        }
        val location = binding.location.text
        if (location != selectedVenue?.location) {
            map[Constants.VENUE_LOCATION] = location
        }
        val description = binding.description.text
        if (description != selectedVenue?.description) {
            map[Constants.VENUE_DESCRIPTION] = description
        }
        if (selectedMenu?.id != selectedVenue?.selectedMenuId) {
            map[Constants.VENUE_MENU_ID] = selectedMenu?.id.toString()
        }
        if (selectedTheme != selectedVenue?.selectedTheme) {
            map[Constants.VENUE_SELECTED_THEME] = selectedTheme.safe()
        }
        if(selectedVenue?.imageUrl!= mVenueImageUrl.safe()){
            map[Constants.VENUE_IMAGE] = mVenueImageUrl.safe()
        }

        FireStoreVenue().updateVenue(::updateSuccess,::updateFailure, selectedVenue?.id.toString(),map)
    }


    private fun updateSuccess(venue: Venue) {
        selectedVenue?.CopyFields(venue)
        imageFileLocation = null
        changed()
        hideProgressDialog()
        showShortToast(R.string.msg_venue_update_success)
    }
    private fun updateFailure(e:Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }

    private fun populateMenus()
    {
        showProgressDialog("Getting Menus")
        FireStoreMenu().getMenus(::successMenu, ::failureMenu)
    }

    private fun setSelectedMenu()
    {
        if(selectedMenu==null && !menus.isNullOrEmpty())
            selectedMenu = menus[0]
    }

    private fun successMenu(m:ArrayList<CafeQrMenu?>)
    {
        menus = m
        hideProgressDialog()
        menuListName.clear()
        menus.forEach {
            menuListName.add(it?.name)
             }
        menus.forEach {
            if(it?.id==selectedVenue?.selectedMenuId)
                selectedMenu = it
        }

        setSelectedMenu()

        binding.menusAutoComplete.setText(selectedMenu?.name,false)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, menuListName)

        binding.menusAutoComplete.setAdapter(adapter)

        binding.menusAutoComplete.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, position: Int, id: Long ->
            binding.recycler.show()
            selectedMenu = menus[position]
            populateMenu()
            changed()
        }
    }
    private fun failureMenu(e:Exception)
    {
        hideProgressDialog()
        logError(e.toString())
    }


    private fun showError() {
        // Do nothing
    }

    private fun populateMenu()
    {
        val selected = selectedMenu
        if(selected!=null) {
            showProgressDialog("Getting Menu Items")
            adapter = SimpleMenuItemAdapter(requireContext(), menuFoodItems)
            binding.recycler.adapter = adapter
            FireStoreMenuItem().getMenuItems(selected.id, ::successMenuItem, ::failureMenuItem)
        }
    }

    private fun successMenuItem(m:ArrayList<FoodMenuItem?>)
    {
        hideProgressDialog()
        menuFoodItems.clear()
        menuFoodItems.addAll(m)
        adapter?.dataChanged(menuFoodItems)
    }

    private fun failureMenuItem(e:Exception)
    {
        hideProgressDialog()
        menuFoodItems.clear()
        adapter?.dataChanged(menuFoodItems)
        logError(e.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        imageFileLocation = data.data
                        val url = imageFileLocation
                        if (url != null) {
                            GlideLoader(requireContext()).loadUserPicture(url, binding.image)
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
}
