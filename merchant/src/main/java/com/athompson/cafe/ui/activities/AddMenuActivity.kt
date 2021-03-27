package com.athompson.cafe.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
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
import com.athompson.cafe.databinding.ActivityAddMenuBinding
import com.athompson.cafe.firestore.FireStoreImage
import com.athompson.cafe.firestore.FireStoreMenu
import com.athompson.cafe.firestore.FireStoreUser
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asDrawable
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.uuid
import com.athompson.cafelib.extensions.ToastExtensions.showLongToast
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.models.CafeQrMenu
import java.io.IOException

class AddMenuActivity : BaseActivity(){

    private var mSelectedImageFileUri: Uri? = null
    private var imageUrl: String = ""
    lateinit var binding:ActivityAddMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupActionBar()
        binding.updateMenuImage.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddMenuActivity)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        // Assign the click event to submit button.
        binding.btnSubmit.setOnClickListener{
            if (validate()) {
                upload()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddMenuActivity)
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
            binding.updateMenuImage.setImageDrawable(R.drawable.ic_vector_edit.asDrawable())

            mSelectedImageFileUri = data.data

            try {
                // Load the product image in the ImageView.
                mSelectedImageFileUri?.let {
                    GlideLoader(this@AddMenuActivity).loadImagePicture(it, binding.menuImage)
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

            TextUtils.isEmpty(binding.etMenuName.trimmed()) -> {
                showErrorSnackBar("Enter a name for the menu", true)
                false
            }

            TextUtils.isEmpty(binding.etDescription.trimmed()) -> {
                showErrorSnackBar("Enter a menu description", true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun upload() {

        showProgressDialog(R.string.please_wait.asString())
        if(mSelectedImageFileUri!=null)
            FireStoreImage().uploadImageToCloudStorage(this@AddMenuActivity, mSelectedImageFileUri,::imageUploadSuccess,::imageUploadFailure)
        else
            uploadCafeQrMenu()
    }

    private fun imageUploadSuccess(imageURL: String) {
        imageUrl = imageURL
        uploadCafeQrMenu()
    }

    private fun imageUploadFailure(exception:Exception) {
        hideProgressDialog()
        uploadCafeQrMenu()
        showShortToast(R.string.upload_image_failure.asString())
    }

    private fun uploadCafeQrMenu() {

        val menu = CafeQrMenu(
            name = binding.etMenuName.trimmed(),
            description =  binding.etDescription.trimmed(),
            imageUrl = mSelectedImageFileUri.toString()
        )
        FireStoreMenu().addCafeQrMenu(::addMenuSuccess,::addMenuFailure, menu)
    }

    private fun addMenuSuccess() {
        hideProgressDialog()
        showShortToast(R.string.add_menu_success.asString())
        finish()
    }

    private fun addMenuFailure(e:Exception) {
        hideProgressDialog()
        showShortToast(R.string.add_menu_failure.asString())
    }
}