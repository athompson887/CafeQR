package com.athompson.cafe.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import com.athompson.cafe.R
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String) {
        if(this::mProgressDialog.isInitialized && mProgressDialog.isShowing)
            return
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}