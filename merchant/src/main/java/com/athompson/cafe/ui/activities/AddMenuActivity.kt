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
            if (validateOrganisationDetails()) {
                uploadOrganisationImage()
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


    private fun validateOrganisationDetails(): Boolean {
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

    private fun uploadOrganisationImage() {

        showProgressDialog(R.string.please_wait.asString())
        FireStoreImage().uploadImageToCloudStorage(this@AddMenuActivity, mSelectedImageFileUri,::imageUploadSuccess,::imageUploadFailure)
    }

    private fun imageUploadSuccess(imageURL: String) {
        imageUrl = imageURL
        uploadCafeQrMenu()
    }

    private fun imageUploadFailure(exception:Exception) {
        hideProgressDialog()
        showShortToast(R.string.upload_image_failure.asString())
    }

    private fun uploadCafeQrMenu() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username = this.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")!!

        val uuid = "".uuid()
        // Here we get the text from editText and trim the space
        val menu = CafeQrMenu(
            FireStoreUser().getCurrentUserID(),
            username,
            binding.etMenuName.trimmed(),
            binding.etDescription.trimmed(),
       //     binding.etAddresss1.trimmed(),
       //     binding.etAddresss2.trimmed(),
        //    binding.etCity.trimmed(),
        //    binding.etEmail.trimmed(),
        //    binding.etPhone.trimmed().toLong(),
        //    mOrganisationImageURL,
       //     uuid
        )

    }



}