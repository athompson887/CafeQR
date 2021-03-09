package com.athompson.cafelib.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackBarExtensions {

    fun View.showShortSnackBar(message : String){
        Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
    }

    fun View.showLongSnackBar(message : String){
        Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
    }

    fun View.snackBarWithAction(message : String, actionLabel : String,
                                block : () -> Unit){
        Snackbar.make(this, message, Snackbar.LENGTH_LONG)
                .setAction(actionLabel) {
                    block()
                }
    }
}