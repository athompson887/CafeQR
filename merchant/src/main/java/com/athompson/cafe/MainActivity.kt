package com.athompson.cafe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.athompson.cafe.databinding.ActivityMainBinding
import com.athompson.cafe.ui.home.HomeFragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth


    companion object {
        const val TAG: String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initFireBase()
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun initFireBase() {
        firebaseAuth = Firebase.auth
    }

//    fun populate() {
//        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
//        db.collection("users")
//                .get()
//                .addOnCompleteListener({ task ->
//                    if (task.isSuccessful) {
//                        for (document in task.result!!) {
//                            logDebug(TAG, document.id + " => " + document.data)
//                        }
//                    } else {
//                        logError(TAG, "Error getting documents." + task.exception)
//                    }
//                })
//    }
}