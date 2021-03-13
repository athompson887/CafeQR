package com.athompson.cafe.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.athompson.cafe.Constants
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityLoginBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafelib.models.User
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ContextExtensions.isOnline
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.athompson.cafelib.extensions.ViewExtensions.isEmpty
import com.athompson.cafelib.extensions.ViewExtensions.trimmed
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var mGoogleClient: GoogleSignInClient? = null

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.tvForgotPassword.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener{

            if(isOnline())
                logInRegisteredUser()
        }
        binding.tvRegister.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.googleSignInButton.setOnClickListener { googleSignIn() }

        initGoogleClient()
    }

    private fun googleSignIn() {
        showProgressDialog(R.string.please_wait.asString())
        val signInIntent: Intent? = mGoogleClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun initGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(R.string.default_web_client_id.asString())
                .requestEmail()
                .build()

        mGoogleClient = this.let { GoogleSignIn.getClient(it, gso) }

    }

    private fun authWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                hideProgressDialog()
                loginSuccess()
            } else {
                hideProgressDialog()
                loginError(task.exception?.message.toString())
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    authWithGoogle(account)
                } else {
                    hideProgressDialog()
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            binding.etEmail.isEmpty() -> {
                showErrorSnackBar(R.string.err_msg_enter_email.asString(), true)
                false
            }
            binding.etPassword.isEmpty()  -> {
                showErrorSnackBar(R.string.err_msg_enter_password.asString(), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            showProgressDialog(R.string.please_wait.asString())

            val email = binding.etEmail.trimmed()
            val password = binding.etPassword.trimmed()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->



                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        loginError(task.exception?.message.toString())
                    }
                }
        }
    }
    private fun loginSuccess()
    {
        showErrorSnackBar(R.string.successful_login.asString(), false)
        lifecycleScope.launch(context = Dispatchers.Main) {
            delay(1000)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun loginError(message:String)
    {
        showErrorSnackBar(message, true)
    }

    fun userLoggedInSuccess(user: User) {
        // Hide the progress dialog.
        hideProgressDialog()

        user.toString()

        if (user.profileCompleted == 0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            // Redirect the user to Main Screen after log in.
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }
}