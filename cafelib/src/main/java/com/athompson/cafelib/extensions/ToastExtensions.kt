package com.athompson.cafelib.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.athompson.cafelib.extensions.ResourceExtensions.asString
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


    fun Context.showShortToast(res : Int){
        Toast.makeText(this, res.asString(), Toast.LENGTH_SHORT).show()
    }

    fun Context.showLongToast(res : Int){
        Toast.makeText(this, res.asString(), Toast.LENGTH_LONG).show()
    }
    fun Fragment.showShortToast(res : Int){
        Toast.makeText(this.requireContext(), res.asString(), Toast.LENGTH_SHORT).show()
    }

    fun Fragment.showShortToast(res : Int,ex:Exception?){
        Toast.makeText(this.requireContext(), res.asString() + ex?.message, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.showLongToast(res : Int){
        Toast.makeText(this.requireContext(), res.asString(), Toast.LENGTH_LONG).show()
    }
}