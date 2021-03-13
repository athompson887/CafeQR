package com.athompson.cafe.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.athompson.cafe.Constants
import com.athompson.cafe.R
import com.athompson.cafe.databinding.ActivitySettingsBinding
import com.athompson.cafe.firestore.FireStoreClass
import com.athompson.cafelib.firestore.FireStoreClassShared
import com.athompson.cafelib.models.User
import com.athompson.cafe.utils.GlideLoader
import com.athompson.cafelib.extensions.ResourceExtensions.asString
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupActionBar()
        binding.tvEdit.setOnClickListener(this@SettingsActivity)
        btn_logout.setOnClickListener(this@SettingsActivity)
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getUserDetails() {

        showProgressDialog(R.string.please_wait.asString())
        FireStoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"
    }
}