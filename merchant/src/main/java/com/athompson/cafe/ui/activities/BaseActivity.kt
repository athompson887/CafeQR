package com.athompson.cafe.ui.activities

import android.app.Dialog
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityMainBinding
import com.athompson.cafe.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView

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

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}