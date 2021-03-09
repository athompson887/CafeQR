package com.athompson.cafelib.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

object ToastExtensions {

    fun Context.showShortToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.showLongToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    fun Fragment.showShortToast(message : String){
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.showLongToast(message : String){
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
    }
}