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
import com.athompson.cafe.MerchantApplication
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityAddVenueBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asDrawable
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.uuid
import com.athompson.cafelib.extensions.ToastExtensions.showLongToast
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.models.Venue
import com.athompson.cafelib.shared.CafeQRApplication
import kotlinx.android.synthetic.main.activity_register.*
import java.io.IOException

class AddVenuesActivity : BaseActivity(){

    private var mSelectedImageFileUri: Uri? = null
    private var mVenueImageURL: String = ""
    lateinit var binding:ActivityAddVenueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVenueBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupActionBar()
        binding.ivUpdateVenue.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddVenuesActivity)
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
                Constants.showImageChooser(this@AddVenuesActivity)
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
            binding.ivUpdateVenue.setImageDrawable(R.drawable.ic_vector_edit.asDrawable())

            mSelectedImageFileUri = data.data

            try {
                // Load the product image in the ImageView.
                mSelectedImageFileUri?.let {
                    GlideLoader(this@AddVenuesActivity).loadImagePicture(it, binding.ivUpdateVenue)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddVenueActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddVenueActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun validateOrganisationDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(R.string.err_msg_select_organisation_image.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etVenueName.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_name.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etVenueType.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_type.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etAddresss1.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etAddresss2.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            TextUtils.isEmpty(binding.etCity.trimmed()) -> {
                showErrorSnackBar(R.string.err_enter_city.asString(), true)
                false
            }

            TextUtils.isEmpty(et_email.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_email.asString(), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadOrganisationImage() {

        showProgressDialog(R.string.please_wait.asString())
        FireStoreClass().uploadImageToCloudStorage(this@AddVenuesActivity, mSelectedImageFileUri)
    }

    fun imageUploadSuccess(imageURL: String) {

        mVenueImageURL = imageURL
        uploadVenue()
    }

    private fun uploadVenue() {
        val venue = Venue(
            CafeQRApplication.selectedOrganisation?.uid.toString(),
            binding.etVenueName.trimmed(),
            binding.etAddresss1.trimmed(),
            binding.etAddresss2.trimmed(),
            binding.etCity.trimmed(),
            binding.etEmail.trimmed(),
            binding.etPhone.trimmed().toLong(),
            mVenueImageURL,
            "".uuid()
        )

        FireStoreClass().addVenue(this@AddVenuesActivity, venue)
    }

    fun addVenueSuccess() {
        hideProgressDialog()
        showShortToast(R.string.add_venue_success.asString())
        finish()
    }

    fun addVenueFailure() {
        hideProgressDialog()
        showShortToast(R.string.add_venue_failure.asString())
    }

    fun imageUploadFailure() {
        hideProgressDialog()
        showShortToast(R.string.upload_image_failure.asString())
    }
}