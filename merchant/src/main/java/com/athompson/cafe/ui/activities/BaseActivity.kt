package com.athompson.cafe.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.athompson.cafe.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView

open class BaseActivity : AppCompatActivity() {


    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val textView:MaterialTextView = mProgressDialog.findViewById(R.id.tv_progress_text)
        textView.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}