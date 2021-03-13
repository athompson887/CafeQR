package com.athompson.cafe.ui.activities

import android.app.Dialog
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.athompson.cafe.R
import com.athompson.cafe.databinding.DialogProgressBinding
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast

open class BaseActivity : AppCompatActivity() {


    private lateinit var mProgressDialog: Dialog
    private lateinit var dialogBinding: DialogProgressBinding
    private var doubleBackToExitPressedOnce = false
    
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        dialogBinding = DialogProgressBinding.inflate(LayoutInflater.from(this))
        mProgressDialog.setContentView(dialogBinding.root)
        dialogBinding.tvProgressText.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        showShortToast(R.string.please_click_back_again_to_exit.asString())

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}