package com.athompson.cafe.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityDashboardBinding
import com.athompson.cafe.ui.fragments.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : BaseActivity(), HomeFragment.OnFragmentInteractionListener {

    lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        binding = ActivityDashboardBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }


        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.app_gradient_color_background
            )
        )

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_home,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }


    override fun onBackPressed() {
        doubleBackToExit()
    }

    override fun loggedOut() {

    }
}