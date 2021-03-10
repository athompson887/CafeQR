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
import com.athompson.cafe.ui.activities.SettingsActivity
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarSubTitle
import com.athompson.cafelib.extensions.FragmentExtensions.toolBarTitle
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.shared.SharedConstants.DISPLAY_NAME_FIELD
import com.athompson.cafelib.shared.SharedConstants.EMAIL_FIELD
import com.athompson.cafelib.shared.SharedConstants.FIRST_NAME_FIELD
import com.athompson.cafelib.shared.SharedConstants.LAST_NAME_FIELD
import com.athompson.cafelib.shared.SharedConstants.UID_FIELD
import com.athompson.cafelib.shared.SharedConstants.USER_DB
import com.athompson.cafelib.shared.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener


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
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.action_menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
               R.id.action_settings -> {
                   startActivity(Intent(activity, SettingsActivity::class.java))
                   return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.mode.observe(viewLifecycleOwner, {
            if (it != null)
                when (it) {
                    Enums.HomeScreenMode.WELCOME -> {
                        renderWelcomeScreen()
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
                logout()
            } else {
                homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
            }
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun logout(){

    }



    private fun renderWelcomeScreen() {
        this.toolBarTitle(getString(R.string.welcome))
        this.toolBarSubTitle(getString(R.string.welcome_subtitle))
        binding.title.text = getString(R.string.welcome)

        val currentUser = homeViewModel.userDocument
        val user = User(
                currentUser?.get(FIRST_NAME_FIELD).toString(),
                currentUser?.get(LAST_NAME_FIELD).toString(),
                currentUser?.get(DISPLAY_NAME_FIELD).toString(),
                currentUser?.get(EMAIL_FIELD).toString(),
                currentUser?.get(UID_FIELD).toString())
        binding.title.text = getString(R.string.hi_full_name, user.firstName.safe(), user.lastName.safe())

        val uri = firebaseAuth?.currentUser?.photoUrl
        if (uri.toString().isEmpty())
            binding.image.setImageResource(R.drawable.welcome)
        else
            binding.image.setImageURI(uri)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()
        userLoggedIn()
    }

    private fun openSettings() {
        firebaseAuth?.signOut()
        listener?.loggedOut()
    }

    //
    // There should only be one CafeQr user for a particular email address
    //
    private fun checkQrUserExists() {
        val db = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().currentUser?.email
        db.collection(USER_DB)
                .whereEqualTo(EMAIL_FIELD, id)
                .get()
                .addOnSuccessListener {
                    if (it.documents.isNullOrEmpty()) {
                        createCafeQrUser()
                    } else {
                        homeViewModel.userDocument = it.documents[0]
                    }
                }
                .addOnFailureListener {
                    homeViewModel.userDocument = null
                }
    }

    private fun createCafeQrUser() {
        val db = FirebaseFirestore.getInstance()
        val user = User("", "", firebaseAuth?.currentUser?.displayName, firebaseAuth?.currentUser?.email, firebaseAuth?.currentUser?.uid)
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

    private fun userLoggedIn() {
        val email = firebaseAuth?.currentUser?.email
        if (!email.isNullOrEmpty()) {
            getCafeQrUser(email)
        }
    }

    private fun getCafeQrUser(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_DB)
                .whereEqualTo(UID_FIELD, uid)
                .get()
                .addOnSuccessListener {
                    if (it.documents.isNotEmpty()) {
                        homeViewModel.userDocument = it.documents[0]
                        val doc = homeViewModel.userDocument
                        homeViewModel.setStatus("" + doc?.get("firstName") + " " + doc?.get("lastName") + " " + doc?.get("id"))
                        homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
                        binding.progress.remove()
                    } else {
                        homeViewModel.setStatus("Failed to get CafeQr user data")
                        binding.progress.remove()
                    }
                }
                .addOnFailureListener {
                    homeViewModel.setStatus("Failed to create user")
                    binding.progress.remove()
                }
    }

    interface OnFragmentInteractionListener{
       fun loggedOut()
    }

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