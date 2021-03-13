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
import com.athompson.cafe.databinding.ActivityAddProductBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafelib.models.Organisation
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asDrawable
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.StringExtensions.uuid
import com.athompson.cafelib.extensions.ToastExtensions.showLongToast
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_add_product.et_email
import kotlinx.android.synthetic.main.activity_register.*
import java.io.IOException

class AddOrganisationActivity : BaseActivity(){

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    lateinit var binding:ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupActionBar()
        binding.ivUpdateOrganisation.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddOrganisationActivity)
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
                Constants.showImageChooser(this@AddOrganisationActivity)
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
            binding.ivUpdateOrganisation.setImageDrawable(R.drawable.ic_vector_edit.asDrawable())

            mSelectedImageFileUri = data.data

            try {
                // Load the product image in the ImageView.
                mSelectedImageFileUri?.let {
                    GlideLoader(this@AddOrganisationActivity).loadOrganisationPicture(it, iv_organisation_image)
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

        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun validateOrganisationDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(R.string.err_msg_select_organisation_image.asString(), true)
                false
            }

            TextUtils.isEmpty(et_organisation_name.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_name.asString(), true)
                false
            }

            TextUtils.isEmpty(et_organisation_type.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_organisation_type.asString(), true)
                false
            }

            TextUtils.isEmpty(et_addresss_1.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            TextUtils.isEmpty(et_addresss_2.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
                false
            }

            TextUtils.isEmpty(et_city.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_address.asString(), true)
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
        FireStoreClass().uploadImageToCloudStorage(this@AddOrganisationActivity, mSelectedImageFileUri)
    }

    fun imageUploadSuccess(imageURL: String) {

        mProductImageURL = imageURL
        uploadOrganisation()
    }

    private fun uploadOrganisation() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username = this.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")!!

        val uuid = "".uuid()
        // Here we get the text from editText and trim the space
        val organisation = Organisation(
            FireStoreClass().getCurrentUserID(),
            username,
            et_organisation_name.trimmed(),
            et_organisation_type.trimmed(),
            et_addresss_1.trimmed(),
            et_addresss_2.trimmed(),
            et_city.trimmed(),
            et_email.trimmed(),
            et_phone.trimmed().toLong(),
            mProductImageURL,
            uuid
        )
        FireStoreClass().addOrganisation(this@AddOrganisationActivity, organisation)
    }

    fun addOrganisationSuccess() {
        hideProgressDialog()
        showShortToast(R.string.add_org_success.asString())
        finish()
    }

    fun addOrganisationFailure() {
        showShortToast(R.string.add_org_success.asString())
    }
}