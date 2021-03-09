package com.athompson.cafe.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.athompson.cafe.Enums
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentHomeBinding
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.StringExtensions.validEmail
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.shared.SharedConstants.DISPLAY_NAME_FIELD
import com.athompson.cafelib.shared.SharedConstants.EMAIL_FIELD
import com.athompson.cafelib.shared.SharedConstants.FIRST_NAME_FIELD
import com.athompson.cafelib.shared.SharedConstants.LAST_NAME_FIELD
import com.athompson.cafelib.shared.SharedConstants.UID_FIELD
import com.athompson.cafelib.shared.SharedConstants.USER_DB
import com.athompson.cafelib.shared.data.User
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment(){
    private var homeMenuItem: MenuItem? = null
    private var deleteMenuItem: MenuItem? = null
    private var logoutMenuItem: MenuItem? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var mGoogleClient: GoogleSignInClient? = null

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth?.removeAuthStateListener(authStateListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initGoogleClient()
    }

    fun initGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleClient = context?.let { GoogleSignIn.getClient(it,gso) }

    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.action_menu_home, menu)
        homeMenuItem = menu.findItem(R.id.action_home)
        logoutMenuItem = menu.findItem(R.id.action_logout)
        deleteMenuItem = menu.findItem(R.id.action_delete)
        //initially
        if(homeViewModel.mode.value==Enums.HomeScreenMode.LOGIN)
        {
            homeMenuItem?.hide()
            logoutMenuItem?.hide()
            deleteMenuItem?.hide()
        }
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                when (homeViewModel.mode.value) {
                    Enums.HomeScreenMode.REGISTER -> {
                        homeViewModel.setMode(Enums.HomeScreenMode.LOGIN)
                    }
                    Enums.HomeScreenMode.WELCOME -> {
                        //don't do anything
                    }
                    Enums.HomeScreenMode.FORGOTTEN_PASSWORD -> {
                        homeViewModel.setMode(Enums.HomeScreenMode.LOGIN)
                    }
                    else -> {

                    }
                }
            }
            R.id.action_delete -> {
                deleteUser()
            }
            R.id.action_logout -> {
                logoutUser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.mode.observe(viewLifecycleOwner, {
            if(it!=null)
            when (it) {
                Enums.HomeScreenMode.WELCOME -> {
                    renderWelcomeScreen()
                }
                Enums.HomeScreenMode.LOGIN -> {
                    renderLoginScreen()
                }
                Enums.HomeScreenMode.FORGOTTEN_PASSWORD -> {
                    renderForgottenPasswordScreen()
                }
                Enums.HomeScreenMode.REGISTER -> {
                    renderRegisterScreen()
                }
            }
        })

        homeViewModel.status.observe(viewLifecycleOwner, {
            binding.status.show()
            binding.status.text = it
        })

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                homeViewModel.setMode(Enums.HomeScreenMode.LOGIN)
            } else {
                homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
            }
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    private fun showAll() {
        homeViewModel.setStatus("")
        binding.signUpButton.show()
        binding.signInButton.show()
        binding.forgottenPasswordButton.show()
        binding.googleSignInButton.show()
        binding.or.show()
        binding.emailWrapper.show()
        binding.passwordWrapper.show()
        binding.passwordStatus.show()
        binding.emailStatus.show()
        binding.emailStatus.show()
        binding.recoverPasswordButton.show()
        binding.registerButton.show()
    }

    private fun renderRegisterScreen() {
        showAll()
        homeMenuItem?.show()
        logoutMenuItem?.hide()
        deleteMenuItem?.hide()
        this.toolBarTitle(getString(R.string.register))
        this.toolBarSubTitle(getString(R.string.register_subtitle))
        binding.title.text = getString(R.string.register)
        binding.image.setImageResource(R.drawable.register)
        binding.signInButton.remove()
        binding.registerButton.remove()
        binding.forgottenPasswordButton.remove()
        binding.googleSignInButton.remove()
        binding.recoverPasswordButton.remove()
        binding.progress.remove()
        binding.or.remove()
    }

    private fun renderForgottenPasswordScreen() {
        showAll()
        homeMenuItem?.show()
        logoutMenuItem?.hide()
        deleteMenuItem?.hide()
        this.toolBarTitle(getString(R.string.forgotten_password_title))
        this.toolBarSubTitle(getString(R.string.forgotten_password_subtitle))
        binding.title.text = getString(R.string.forgotten_password_title)
        binding.image.setImageResource(R.drawable.forgotten)
        binding.signUpButton.remove()
        binding.signInButton.remove()
        binding.googleSignInButton.remove()
        binding.passwordWrapper.remove()
        binding.passwordStatus.remove()
        binding.forgottenPasswordButton.remove()
        binding.registerButton.remove()
        binding.progress.remove()
        binding.or.remove()
    }

    private fun renderLoginScreen() {
        showAll()
        homeMenuItem?.hide()
        logoutMenuItem?.hide()
        deleteMenuItem?.hide()
        this.toolBarTitle(getString(R.string.login))
        this.toolBarSubTitle(getString(R.string.login_subtitle))
        binding.title.text = getString(R.string.login)
        binding.image.setImageResource(R.drawable.login)
        binding.recoverPasswordButton.remove()
        binding.progress.remove()
        binding.signUpButton.remove()
    }

    private fun renderWelcomeScreen() {
        showAll()
        homeMenuItem?.hide()
        logoutMenuItem?.show()
        deleteMenuItem?.show()
        this.toolBarTitle(getString(R.string.welcome))
        this.toolBarSubTitle(getString(R.string.welcome_subtitle))
        binding.title.text = getString(R.string.welcome)

        binding.signUpButton.remove()
        binding.signInButton.remove()
        binding.googleSignInButton.remove()
        binding.passwordWrapper.remove()
        binding.passwordStatus.remove()
        binding.emailWrapper.remove()
        binding.emailStatus.remove()
        binding.registerButton.hide()
        binding.forgottenPasswordButton.remove()
        binding.or.remove()
        binding.recoverPasswordButton.remove()
        binding.progress.remove()

        val currentUser = homeViewModel.userDocument
        val user = User(
                    currentUser?.get(FIRST_NAME_FIELD).toString(),
                    currentUser?.get(LAST_NAME_FIELD).toString(),
                    currentUser?.get(DISPLAY_NAME_FIELD).toString(),
                    currentUser?.get(EMAIL_FIELD).toString(),
                    currentUser?.get(UID_FIELD).toString())
        binding.title.text = getString(R.string.hi_full_name,user.firstName.safe(),user.lastName.safe())

        val uri =  firebaseAuth?.currentUser?.photoUrl
        if (uri.toString().isEmpty())
            binding.image.setImageResource(R.drawable.welcome)
        else
            binding.image.setImageURI(uri)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.progress.remove()

        userLoggedIn()

        binding.forgottenPasswordButton.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.FORGOTTEN_PASSWORD)
        }

        binding.registerButton.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.REGISTER)
        }

        binding.recoverPasswordButton.setOnClickListener {
            recoverPassword()
        }

        binding.signInButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            signIn(email, password)
        }
        binding.signUpButton.setOnClickListener {
            signUp()
        }

        binding.googleSignInButton.setOnClickListener { googleSignIn() }
    }

    private fun logoutUser() {
        firebaseAuth?.signOut()
    }

    private fun deleteUser() {
        val db = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().currentUser?.uid
        if(!id.isNullOrBlank())
            db.collection(USER_DB).document(id).delete()
                    .addOnSuccessListener {
                        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                homeViewModel.setStatus(getString(R.string.user_deleted))
                                updateUI()
                            }
                        }
                    }
                    .addOnFailureListener {

                    }
    }

    private fun recoverPassword() {
        val email = binding.email.text.toString()
        if (email.isEmpty()) {
            homeViewModel.setStatus(getString(R.string.plaese_complete_email))
        } else {
            firebaseAuth?.sendPasswordResetEmail(email)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            homeViewModel.setStatus(getString(R.string.password_sent))
                        } else {
                            homeViewModel.setStatus(getString(R.string.email_error))
                        }
                    }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailStatus.text = getString(R.string.email_required)
            binding.emailStatus.show()
            return false
        }
        if (!email.validEmail()) {
            binding.emailStatus.text = getString(R.string.valid_email_required)
            binding.emailStatus.show()
            return false
        }
        if (password.isEmpty()) {
            binding.passwordStatus.text = getString(R.string.password_required)
            binding.passwordStatus.show()
            return false
        }
        if (password.length < 6) {
            binding.passwordStatus.text = getString(R.string.password_must_be)
            binding.passwordStatus.show()
            return false
        }
        clearStatusFields()
        return true
    }

    private fun signUp() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (!validate(email, password))
            return
        binding.progress.show()
        firebaseAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName("Andrew").build()

                        firebaseAuth?.currentUser?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener {
                                    checkQrUserExists(email)
                                }

                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            homeViewModel.setStatus(getString(R.string.account_with_email_already_exists))
                            binding.progress.remove()
                        }
                        else {
                            homeViewModel.setStatus(getString(R.string.incorrect_email))
                            binding.progress.remove()
                        }
                    }
                }
    }


    //
    // There should only be one CafeQr user for a particular email address
    //
    private fun checkQrUserExists(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener {
                    if(it.documents.isNullOrEmpty()) {
                        createCafeQrUser()
                    }
                    else
                    {
                        homeViewModel.userDocument = it.documents[0]
                    }
                }
                .addOnFailureListener {
                    homeViewModel.userDocument = null
                }
    }

    fun createCafeQrUser() {
        val db = FirebaseFirestore.getInstance()
        val user = User("","",firebaseAuth?.currentUser?.displayName,firebaseAuth?.currentUser?.email,firebaseAuth?.currentUser?.uid)
        db.collection(USER_DB)
                .add(user)
                .addOnSuccessListener {
                    homeViewModel.setStatus("User created with id${user.uid}")
                    homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
                    binding.progress.remove()
                }
                .addOnFailureListener {
                    homeViewModel.setStatus(getString(R.string.failed_to_create_user))
                    binding.progress.remove()
                }
    }

    private fun googleSignIn() {
        val signInIntent: Intent? = mGoogleClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun userLoggedIn() {
        val email = firebaseAuth?.currentUser?.email
        if (!email.isNullOrEmpty()) {
            getCafeQrUser(email)
        }
    }

    private fun clearStatusFields() {
        homeViewModel.setStatus("")
        binding.emailStatus.text = ""
        binding.passwordStatus.text = ""
        binding.status.text = ""
    }

    private fun signIn(email: String, password: String) {
        if (!validate(email, password))
            return

        binding.progress.show()
        firebaseAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val uid = firebaseAuth?.currentUser?.uid
                        if(uid!=null)
                            getCafeQrUser(uid)
                        else
                            failed()

                    } else failed()
                }
    }

    private fun failed()
    {
        homeViewModel.setStatus(getString(R.string.auth_failed))
        binding.progress.remove()
        updateUI()
    }

    private fun getCafeQrUser(uid: String)
    {
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_DB)
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener{
                    if(it.documents.isNotEmpty()) {
                        homeViewModel.userDocument = it.documents[0]
                        val doc = homeViewModel.userDocument
                        homeViewModel.setStatus("" + doc?.get("firstName") + " " +doc?.get("lastName") + " " + doc?.get("id"))
                        homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
                        binding.progress.remove()
                    }
                    else
                    {
                        homeViewModel.setStatus("Failed to get CafeQr user data")
                        binding.progress.remove()
                    }
                }
                .addOnFailureListener{
                    homeViewModel.setStatus("Failed to create user")
                    binding.progress.remove()
                }
    }

    private fun updateUI() {
        if (firebaseAuth?.currentUser != null) {
            homeViewModel.setStatus(getString(R.string.login_success))
            homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
        } else {
            homeViewModel.setStatus(getString(R.string.login_error))
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
                    homeViewModel.setStatus(getString(R.string.auth_failed))
                    binding.progress.remove()
                }
            }
        }
    }

    private fun authWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
                binding.progress.remove()
            } else {
                homeViewModel.setStatus(getString(R.string.auth_failed))
                binding.progress.remove()
            }
        }
    }


    interface OnFragmentInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}