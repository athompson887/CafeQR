package com.athompson.cafe.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityRegisterBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafelib.firestore.FireStoreClassShared
import com.athompson.cafelib.models.User
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.isEmpty
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Suppress("DEPRECATION")
class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        // It is deprecated in the API level 30. I will update you with the alternate solution soon.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        binding.btnRegister.setOnClickListener {

            registerUser()
        }

        binding.tvLogin.setOnClickListener{
            onBackPressed()
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarRegisterActivity)

        val actionBar = supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            binding.etFirstName.isEmpty() -> {
                showErrorSnackBar(R.string.err_msg_enter_first_name.asString(), true)
                false
            }

            binding.etLastName.isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            binding.etEmail.isEmpty() -> {
                showErrorSnackBar(R.string.err_msg_enter_email.asString(), true)
                false
            }

            binding.etPassword.isEmpty() -> {
                showErrorSnackBar(R.string.err_msg_enter_password.asString(), true)
                false
            }

            binding.etConfirmPassword.isEmpty() -> {
                showErrorSnackBar(
                    R.string.err_msg_enter_confirm_password.asString(),
                    true
                )
                false
            }

            binding.etPassword.trimmed() != binding.etConfirmPassword.trimmed() -> {
                showErrorSnackBar(
                    R.string.err_msg_password_and_confirm_password_mismatch.asString(),
                    true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    R.string.err_msg_agree_terms_and_condition.asString(),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser() {

        if (validateRegisterDetails()) {

            showProgressDialog(R.string.please_wait.asString())

            val email: String = binding.etEmail.trimmed()
            val password: String = binding.etPassword.trimmed()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result?.user!!
                        val user = User(
                            firebaseUser.uid,
                            binding.etFirstName.trimmed(),
                            binding.etLastName.trimmed(),
                           binding.etEmail.trimmed()
                        )

                        FireStoreClass().registerUser(this@RegisterActivity, user)
                    } else {
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
        }
    }
    fun signOut()
    {
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        showShortToast(R.string.register_success.asString())
        signOut()
    }

    fun userRegistrationFailure() {
        showShortToast(R.string.register_success.asString())
        hideProgressDialog()
    }
}