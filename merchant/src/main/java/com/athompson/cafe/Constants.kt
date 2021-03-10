package com.athompson.cafe

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

object Constants {
    const val SEARCH_PAYLOAD = "PAYLOAD"
    const val USERS: String = "users"

    const val PREFERENCES: String = "CafeQrPreferences"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    // Intent extra constants.
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}