package com.athompson.cafe.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.athompson.cafe.Constants
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityUserProfileBinding
import com.athompson.cafe.firestore.FireStoreImage
import com.athompson.cafe.firestore.FireStoreUser
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ActivityExtensions.logError
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.athompson.cafelib.models.User
import com.athompson.cafelib.shared.SharedConstants
import java.io.IOException


@Suppress("DEPRECATION")
class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private lateinit var binding: ActivityUserProfileBinding
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        if (intent.hasExtra(SharedConstants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(SharedConstants.EXTRA_USER_DETAILS)!!
        }

        if (mUserDetails.profileCompleted == 0) {
            binding.tvTitle.text = R.string.title_complete_profile.asString()
            binding.etFirstName.isEnabled = false
            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.isEnabled = false
            binding.etLastName.setText(mUserDetails.lastName)
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mUserDetails.email)
        } else {
            setupActionBar()
            binding.tvTitle.text = R.string.title_edit_profile.asString()
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, binding.ivUserPhoto)
            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.setText(mUserDetails.lastName)
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mUserDetails.email)

            if (mUserDetails.mobile != 0L) {
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == SharedConstants.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }
        }

        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        binding.btnSave.setOnClickListener(this@UserProfileActivity)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SharedConstants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                showShortToast(R.string.read_storage_permission_denied.asString())
            }
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SharedConstants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data

                        val url = mSelectedImageFileUri
                        if (url != null) {
                            GlideLoader(this@UserProfileActivity).loadUserPicture(
                                url,
                                binding.ivUserPhoto
                            )
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

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarUserProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.trimmed()) -> {
                showErrorSnackBar(R.string.err_msg_enter_mobile_number.asString(), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        val firstName = binding.etFirstName.trimmed()
        if (firstName != mUserDetails.firstName) {
            userHashMap[SharedConstants.FIRST_NAME] = firstName
        }

        // Get the LastName from editText and trim the space
        val lastName = binding.etLastName.trimmed()
        if (lastName != mUserDetails.lastName) {
            userHashMap[SharedConstants.LAST_NAME] = lastName
        }

        val gender = if (binding.rbMale.isChecked) {
            SharedConstants.MALE
        } else {
            SharedConstants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[SharedConstants.IMAGE] = mUserProfileImageURL
        }

        val mobileNumber = binding.etMobileNumber.trimmed()
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[SharedConstants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[SharedConstants.GENDER] = gender
        }

        if (mUserDetails.profileCompleted == 0) {
            userHashMap[SharedConstants.COMPLETE_PROFILE] = 1
        }

        // call the registerUser function of FireStore class to make an entry in the database.
        FireStoreUser().update(::userProfileUpdateSuccess,::userProfileUpdateFailure, userHashMap)
    }


    private fun userProfileUpdateSuccess(user:User) {
        hideProgressDialog()
        showShortToast(R.string.msg_profile_update_success.asString().plus(" ").plus(user.gender))
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }
    private fun userProfileUpdateFailure(e:Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SharedConstants.READ_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btn_save -> {

                    if (validateUserProfileDetails()) {
                        showProgressDialog(R.string.please_wait.asString())
                        val uri = mSelectedImageFileUri
                        if (uri != null) {
                            FireStoreImage().uploadImageToCloudStorage(this,uri,SharedConstants.USER_PROFILE_IMAGE_SUFFIX,null, ::imageUploadSuccess,::imageUploadFailure)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun imageUploadSuccess(imageURL: String) {

        hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private fun imageUploadFailure(e: Exception) {
        logError(e.message.toString())
        hideProgressDialog()
    }
}