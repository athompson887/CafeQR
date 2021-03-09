package com.athompson.cafe.customer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.athompson.cafe.customer.databinding.ActivityMainBinding
import com.athompson.cafelib.extensions.ActivityExtensions.logDebug
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    companion object {
        const val TAG: String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        initFireBase()
        populate()
    }


    fun initFireBase()
    {
        var db:FirebaseFirestore =   FirebaseFirestore.getInstance()
    }

    fun populate()
    {
        val db:FirebaseFirestore =   FirebaseFirestore.getInstance()
        db.collection("users")
                .get()
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            logDebug(TAG, document.id + " => " + document.data)
                        }
                    } else {
                        logDebug( "Error getting documents."+ task.exception)
                    }
                })
    }
}