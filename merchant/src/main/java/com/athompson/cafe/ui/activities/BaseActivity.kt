package com.athompson.cafe.ui.activities

import android.app.Dialog
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.athompson.cafe.R
import com.athompson.cafe.databinding.DialogProgressBinding
import com.athompson.cafe.ui.progress.CafeQrProgress
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast

open class BaseActivity : AppCompatActivity() {


    private lateinit var progressDialog: CafeQrProgress
    private var doubleBackToExitPressedOnce = false

    fun showProgressDialog(text: String) {
        if(this::progressDialog.isInitialized && progressDialog.isShowing())
            return
        progressDialog = CafeQrProgress()
        progressDialog.show(this,text)
    }

    fun hideProgressDialog() {
        progressDialog.dialog.dismiss()
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