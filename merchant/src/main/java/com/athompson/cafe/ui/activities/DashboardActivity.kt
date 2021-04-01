package com.athompson.cafe.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivityDashboardBinding
import com.athompson.cafe.ui.fragments.menu.MenuFragment
import com.athompson.cafe.ui.fragments.menuitem.MenuItemFragment
import com.athompson.cafe.ui.fragments.venues.VenuesFragment
import com.athompson.cafelib.extensions.ResourceExtensions.asDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : BaseActivity(), MenuItemFragment.OnFragmentInteractionListener, MenuFragment.OnFragmentInteractionListener {

    lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }

        binding = ActivityDashboardBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_venues,
                R.id.navigation_menus,
                R.id.navigation_food_menu,
                R.id.navigation_codes
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        doubleBackToExit()
    }

}