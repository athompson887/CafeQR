package com.athompson.cafe.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.athompson.cafe.R
import com.bumptech.glide.Glide
import java.io.IOException


class GlideLoader(val context: Context) {

    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {

            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * A function to load image from Uri or URL for the product image.
     */
    fun loadOrganisationPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(image) // Uri or URL of the image
                .centerCrop() // Scale type of the image.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
