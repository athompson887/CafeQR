package com.athompson.cafe.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.athompson.cafe.Constants
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityAddMenuItemBinding
import com.athompson.cafe.firestore.FireStoreImage
import com.athompson.cafe.firestore.FireStoreCafeQrMenu
import com.athompson.cafe.firestore.FireStoreFoodMenuItem
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.logError
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.FragmentExtensions.logError
import com.athompson.cafelib.extensions.ResourceExtensions.asDrawable
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showLongToast
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.models.CafeQrMenu
import com.athompson.cafelib.models.FoodMenuItem
import com.athompson.cafelib.shared.SharedConstants
import com.athompson.cafelib.shared.SharedConstants.FOOD_TYPES
import java.io.IOException

class AddMenuItemActivity : BaseActivity(){

    private var selectedMenuID: String? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mFoodImageURL: String = ""
    private var selectedFoodType: String = ""
    private var selectedMenu:CafeQrMenu? = null
    private val menuListName = ArrayList<String?>()
    private val menus = ArrayList<CafeQrMenu?>()
    lateinit var binding:ActivityAddMenuItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuItemBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupActionBar()
        binding.ivUpdateMenuItem.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddMenuItemActivity)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SharedConstants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        // Assign the click event to submit button.
        binding.btnSubmit.setOnClickListener{
            if (validate()) {
                uploadImage()
            }
        }
        selectedMenuID = intent.extras?.getString("menuID")
        print(selectedMenuID)

        val foodTypesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, FOOD_TYPES)
        binding.foodTypesAutoComplete.setAdapter(foodTypesAdapter)
        binding.foodTypesAutoComplete.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedFoodType = FOOD_TYPES[0]
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedFoodType = FOOD_TYPES[position]
                }
            }
        binding.foodTypesAutoComplete.setText(selectedFoodType)
        binding.foodTypesDropDown.editText?.setText(selectedMenu?.name)
        populateMenus()
    }

    private fun populateMenus()
    {
        showProgressDialog("Getting Menus")
        FireStoreCafeQrMenu().getAll(::successMenu, ::failureMenu)
    }

    private fun successMenu(menusItems: ArrayList<CafeQrMenu?>) {
        hideProgressDialog()
        menuListName.clear()
        menusItems.forEach { menuListName.add(it?.name) }

        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, menuListName)

        binding.menusAutoComplete.setAdapter(adapter)
        binding.menusAutoComplete.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedMenu = null
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedMenu = menus[position]
                    selectedMenuID = selectedMenu?.id
                }
            }
        if(menus.isNotEmpty()) {
            binding.menusAutoComplete.setSelection(getSelectedMenuIndex(menusItems))
            binding.menusAutoComplete.setText(selectedMenu?.name)
            binding.menusDropDown.editText?.setText(selectedMenu?.name)
        }

    }
    private fun getSelectedMenuIndex(m: ArrayList<CafeQrMenu?>): Int {
        m.forEach {
            if(it?.id == selectedMenuID) {
                return m.indexOf(it)
            }
        }
        return 0
    }


    private fun failureMenu(e:Exception)
    {
        hideProgressDialog()
        logError(e.toString())
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SharedConstants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddMenuItemActivity)
            } else {
                showLongToast(R.string.read_storage_permission_denied.asString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == SharedConstants.PICK_IMAGE_REQUEST_CODE
            && data?.data != null
        ) {

            // Replace the add icon with edit icon once the image is selected.
            binding.ivUpdateMenuItem.setImageDrawable(R.drawable.ic_vector_edit.asDrawable())

            mSelectedImageFileUri = data.data

            try {
                // Load the product image in the ImageView.
                mSelectedImageFileUri?.let {
                    GlideLoader(this@AddMenuItemActivity).loadImagePicture(it, binding.ivMenuItemImage)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddProductActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun validate(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(R.string.err_msg_select_organisation_image.asString(), true)
                false
            }

            selectedMenuID==null -> {
                showErrorSnackBar("You cannot add a menu item until you have created and selected a menu", true)
                false
            }

            TextUtils.isEmpty(binding.etMenuItemName.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_name.asString(), true)
                false
            }


            TextUtils.isEmpty(binding.etMenuItemDescription.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.price.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun uploadImage() {

        if(selectedMenuID!=null) {
            showProgressDialog(R.string.please_wait.asString())
            FireStoreImage().uploadImageToCloudStorage(
                this@AddMenuItemActivity,
                mSelectedImageFileUri,
                null,
                SharedConstants.MENU_ITEM_IMAGE_SUFFIX,
                ::imageUploadSuccess,
                ::imageUploadFailure
            )
        }
    }

    private fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        mFoodImageURL = imageURL
        if (!selectedMenuID.isNullOrEmpty()) {
            uploadMenuItem(selectedMenuID.toString())
        }
    }

    private fun imageUploadFailure(exception: Exception) {
        logError(exception.message.toString())
        showShortToast(R.string.upload_image_failure.asString())
        hideProgressDialog()
    }


    private fun uploadMenuItem(menuItemId:String) {

        // Here we get the text from editText and trim the space
        val food = FoodMenuItem(
            name = binding.etMenuItemName.trimmed(),
            type =  selectedFoodType,
            description = binding.etMenuItemDescription.trimmed(),
            imageUrl = mFoodImageURL,
            price = binding.price.trimmed().toDouble(),
            menuId = menuItemId
        )

        FireStoreFoodMenuItem().add(::addMenuItemSuccess,::addMenuItemFailure, food)
    }

    private fun addMenuItemSuccess() {
        hideProgressDialog()
        showShortToast(R.string.add_menu_item_success.asString())
        finish()
    }
    private fun addMenuItemFailure(e:Exception) {
        hideProgressDialog()
        logError(e.message.toString())
    }
}