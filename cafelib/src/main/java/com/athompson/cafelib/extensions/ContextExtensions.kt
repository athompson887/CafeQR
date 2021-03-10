package com.athompson.cafelib.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object ContextExtensions {

    fun Context.isOnline(): Boolean {
        this?.apply {
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
        return false
    }


    // if you want to execute some code when there is no internet you can pass it as first lambda
    // isOnline({
    // Offline
    // }) {
    // Online
    //   }
    @Suppress("DEPRECATION")
    fun Context.isOnline(failBlock : () -> Unit  = { globalInternetFailBock() }, successBlock : () -> Unit ) {
        this?.apply {
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
            }
            else {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected) {
                    successBlock()
                } else {
                    failBlock()
                }
            }
        }
    }

    private fun Context.globalInternetFailBock(){
        // show alter to user or implement custom code here
    }
}