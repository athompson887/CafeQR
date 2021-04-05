package com.athompson.cafe.ui.fragments

import androidx.fragment.app.Fragment
import com.athompson.cafe.ui.progress.CafeQrProgress

open class BaseFragment : Fragment() {

    private lateinit var progressDialog: CafeQrProgress

    fun showProgressDialog(text: String) {
        if(this::progressDialog.isInitialized && progressDialog.isShowing())
            return
        progressDialog = CafeQrProgress()
        progressDialog.show(requireContext(),text)
    }

    fun hideProgressDialog() {
        if(this::progressDialog.isInitialized && !progressDialog.isShowing())
            return
        progressDialog.dialog.dismiss()
    }
}