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
}
