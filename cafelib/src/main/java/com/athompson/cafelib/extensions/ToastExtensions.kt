package com.athompson.cafelib.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.lang.Exception

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

    fun Fragment.showShortToast(message : String,ex:Exception?){
        Toast.makeText(this.requireContext(), message + ex?.message, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.showLongToast(message : String){
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
    }
}