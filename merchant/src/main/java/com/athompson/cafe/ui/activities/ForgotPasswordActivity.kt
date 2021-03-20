package com.athompson.cafe.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityForgotPasswordBinding
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setupActionBar()

        binding.btnSubmit.setOnClickListener {

            val email: String = binding.etEmail.trimmed()

            if (email.isEmpty()) {
                showErrorSnackBar(R.string.err_msg_enter_email.asString(), true)
            } else {


                showProgressDialog(R.string.please_wait.asString())

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        hideProgressDialog()

                        if (task.isSuccessful) {
                             showShortToast(R.string.email_sent_success.asString())
                            finish()
                        } else {
                            showErrorSnackBar(task.exception?.message.toString(), true)
                        }
                    }
            }
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarForgotPasswordActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }
    }
}