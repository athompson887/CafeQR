package com.athompson.cafe.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.athompson.cafe.Constants
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityAddMenuItemBinding
import com.athompson.cafe.firestore.FireStoreImage
import com.athompson.cafe.firestore.FireStoreMenuItem
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.logError
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asDrawable
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showLongToast
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.models.FoodMenuItem
import java.io.IOException

class AddMenuItemActivity : BaseActivity(){

    private var selectedMenu: String? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mFoodImageURL: String = ""
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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        // Assign the click event to submit button.
        binding.btnSubmit.setOnClickListener{
            if (validate()) {
                uploadImage()
            }
        }
        selectedMenu = intent.extras?.getString("menuID")
        print(selectedMenu)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
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
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
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

            selectedMenu==null -> {
                showErrorSnackBar("You cannot add a menu item until you have created and selected a menu", true)
                false
            }

            TextUtils.isEmpty(binding.etMenuItemName.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_name.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etMenuItemType.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_type.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etMenuItemDescription.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etPrice.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun uploadImage() {

        if(selectedMenu!=null) {
            showProgressDialog(R.string.please_wait.asString())
            FireStoreImage().uploadImageToCloudStorage(
                this@AddMenuItemActivity,
                mSelectedImageFileUri,
                ::imageUploadSuccess,
                ::imageUploadFailure
            )
        }
    }

    private fun imageUploadSuccess(imageURL: String) {
        hideProgressDialog()
        mFoodImageURL = imageURL
        if (!selectedMenu.isNullOrEmpty()) {
            uploadMenuItem(selectedMenu.toString())
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
            type =  binding.etMenuItemType.trimmed(),
            description = binding.etMenuItemDescription.trimmed(),
            imageUrl = mFoodImageURL,
            price = binding.etPrice.trimmed().toDouble(),
            menuId = menuItemId
        )

        FireStoreMenuItem().addMenuItem(::addMenuItemSuccess,::addMenuItemFailure, food)
    }

    private fun addMenuItemSuccess() {
        hideProgressDialog()
        showShortToast(R.string.add_menu_item_success.asString())
        finish()
    }
    private fun addMenuItemFailure(e:Exception) {
        hideProgressDialog()
        showShortToast(R.string.add_menu_item_failure.asString())
    }
}