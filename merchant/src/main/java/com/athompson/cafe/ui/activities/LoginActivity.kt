package com.athompson.cafe.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityLoginBinding
import com.athompson.cafelib.extensions.ActivityExtensions.showErrorSnackBar
import com.athompson.cafelib.extensions.ViewExtensions.isEmpty
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
        showProgressDialog(resources.getString(R.string.please_wait))
        val signInIntent: Intent? = mGoogleClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun initGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            binding.etPassword.isEmpty()  -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    hideProgressDialog()

                    if (task.isSuccessful) {
                        loginSuccess()
                    } else {
                        loginError(task.exception?.message.toString())
                    }
                }
        }
    }
    private fun loginSuccess()
    {
        showErrorSnackBar(getString(R.string.successful_login), false)
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
}