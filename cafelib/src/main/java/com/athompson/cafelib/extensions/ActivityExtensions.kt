package com.athompson.cafelib.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.athompson.cafelib.R
import com.athompson.cafelib.extensions.SnackBarExtensions.snackBarWithAction
import com.athompson.cafelib.shared.AppPermission
import com.athompson.cafelib.shared.SharedConstants.LOGGING_ON
import com.google.android.material.snackbar.Snackbar

object ActivityExtensions {

    fun AppCompatActivity.toolbarTitle(title:String) {
        this.supportActionBar?.title = title
    }
    fun AppCompatActivity.toolbarSubTitle(subtitle:String) {
        this.supportActionBar?.subtitle = subtitle
    }

    fun AppCompatActivity.logInfo(msg:String) {
        if(LOGGING_ON)
            Log.i("", msg)
    }
    fun AppCompatActivity.logDebug(msg:String) {
        if(LOGGING_ON)
            Log.d("", msg)
    }
    fun AppCompatActivity.logError(msg:String) {
        if(LOGGING_ON)
            Log.e("", msg)
    }

    fun AppCompatActivity.logInfo(caller:String,msg:String) {
        if(LOGGING_ON)
            Log.i(caller, msg)
    }
    fun AppCompatActivity.logDebug(caller:String,msg:String) {
        if(LOGGING_ON)
            Log.d(caller, msg)
    }
    fun AppCompatActivity.logError(caller:String,msg:String) {
        if(LOGGING_ON)
            Log.e(caller, msg)
    }

    inline fun <reified  T : Any> Activity.getValue(
            label : String, defaultValue : T? = null) = lazy{
        val value = intent?.extras?.get(label)
        if (value is T) value else defaultValue
    }

    inline fun <reified  T : Any> Activity.getValueNonNull(
            label : String, defaultValue : T? = null) = lazy{
        val value = intent?.extras?.get(label)
        requireNotNull((if (value is T) value else defaultValue)){label}
    }

    fun AppCompatActivity.checkPermission(permission: AppPermission) = run {
        baseContext?.let {
            (ActivityCompat.checkSelfPermission(it, permission.permissionName
            ) == PermissionChecker.PERMISSION_GRANTED)
        } ?: false
    }

    fun AppCompatActivity.shouldRequestPermissionRationale(permission: AppPermission) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permissionName)

    fun AppCompatActivity.requestAllPermissions(permission: AppPermission) {
        ActivityCompat.requestPermissions(this, arrayOf(permission.permissionName), permission.requestCode)
    }

    fun AppCompatActivity.showErrorSnackBar(message: String, errorMessage: Boolean) {

        val snackBar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this,
                            R.color.colorSnackBarError
                    )
            )
        }else{
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this,
                            R.color.colorSnackBarSuccess
                    )
            )
        }
        snackBar.show()
    }

}