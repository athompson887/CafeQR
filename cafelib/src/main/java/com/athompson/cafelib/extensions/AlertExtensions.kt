package com.athompson.cafelib.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.athompson.cafelib.R

object AlertExtensions {

    // Show alert dialog
    fun Context.showAlertDialog(positiveButtonLable : String = getString(R.string.okay),
                                title : String = getString(R.string.app_name), message : String,
                                actionOnPositveButton : () -> Unit) {
        val builder = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButtonLable) { dialog, id ->
                    dialog.cancel()
                    actionOnPositveButton()
                }
        val alert = builder.create()
        alert.show()
    }
}