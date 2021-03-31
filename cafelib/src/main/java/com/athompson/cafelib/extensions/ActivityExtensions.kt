package com.athompson.cafelib.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.athompson.cafelib.R
import com.athompson.cafelib.extensions.ResourceExtensions.asColor
import com.athompson.cafelib.extensions.SnackBarExtensions.snackBarWithAction
import com.athompson.cafelib.shared.AppPermission
import com.athompson.cafelib.shared.CafeQRApplication
import com.athompson.cafelib.shared.SharedConstants.LOGGING_ON
import com.google.android.material.snackbar.Snackbar

object ActivityExtensions {

    private fun AppCompatActivity?.globalInternetFailBock(){
        // show alter to user or implement custom code here
    }

    fun Activity.isOnline(): Boolean {

        this.apply {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw      = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.isConnected ?: false
            }
        }
    }


    // if you want to execute some code when there is no internet you can pass it as first lambda
    // isOnline({
    // Offline
    // }) {
    // Online
    //   }
    @Suppress("DEPRECATION")
    fun AppCompatActivity.isOnline(failBlock : () -> Unit  = { globalInternetFailBock() }, successBlock : () -> Unit ) {
        this.apply {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork
                val actNw = connectivityManager.getNetworkCapabilities(nw)
                var connected = true
                if(actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == false)
                    connected=false
                if(actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == false)
                    connected=false
                if(actNw?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == false)
                    connected=false
                if(actNw?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == false)
                    connected=false
                if (connected) {
                    successBlock()
                } else {
                    failBlock()
                }
            } else {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected) {
                    successBlock()
                } else {
                    failBlock()
                }
            }
        }
    }

    fun AppCompatActivity.toolbarTitle(title:String) {
        this.supportActionBar?.title = title
    }
    fun AppCompatActivity.toolbarSubTitle(subtitle:String) {
        this.supportActionBar?.subtitle = subtitle
    }

    fun Activity.logInfo(msg:String) {
        if(LOGGING_ON)
            Log.i("", msg)
    }
    fun Activity.logDebug(msg:String) {
        if(LOGGING_ON)
            Log.d("", msg)
    }
    fun Activity.logError(msg:String) {
        if(LOGGING_ON)
            Log.e("", msg)
    }

    fun Activity.logInfo(caller:String,msg:String) {
        if(LOGGING_ON)
            Log.i(caller, msg)
    }
    fun Activity.logDebug(caller:String,msg:String) {
        if(LOGGING_ON)
            Log.d(caller, msg)
    }
    fun Activity.logError(caller:String,msg:String) {
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

    fun Activity.checkPermission(permission: AppPermission) = run {
        baseContext?.let {
            (ActivityCompat.checkSelfPermission(it, permission.permissionName
            ) == PermissionChecker.PERMISSION_GRANTED)
        } ?: false
    }

    fun Activity.shouldRequestPermissionRationale(permission: AppPermission) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permissionName)

    fun Activity.requestAllPermissions(permission: AppPermission) {
        ActivityCompat.requestPermissions(this, arrayOf(permission.permissionName), permission.requestCode)
    }

    fun Activity.showErrorSnackBar(message: String, errorMessage: Boolean) {

        val snackBar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(R.color.colorSnackBarError.asColor())
        }else{
            snackBarView.setBackgroundColor(R.color.colorSnackBarSuccess.asColor())
        }
        snackBar.show()
    }
}