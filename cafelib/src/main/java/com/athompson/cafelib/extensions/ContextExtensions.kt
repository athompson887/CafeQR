package com.athompson.cafelib.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use

object ContextExtensions {

    @ColorInt
    @SuppressLint("Recycle")
    fun Context.themeColor(
        @AttrRes themeAttrId: Int
    ): Int {
        return obtainStyledAttributes(
            intArrayOf(themeAttrId)
        ).use {
            it.getColor(0, Color.MAGENTA)
        }
    }

    /**
     * Retrieve a style from the current [android.content.res.Resources.Theme].
     */
    @StyleRes
    fun Context.themeStyle(@AttrRes attr: Int): Int {
        val tv = TypedValue()
        theme.resolveAttribute(attr, tv, true)
        return tv.data
    }

    @SuppressLint("Recycle")
    fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator {
        return AnimationUtils.loadInterpolator(
            this,
            obtainStyledAttributes(intArrayOf(attr)).use {
                it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
            }
        )
    }

    fun Context.getDrawableOrNull(@DrawableRes id: Int?): Drawable? {
        return if (id == null || id == 0) null else AppCompatResources.getDrawable(this, id)
    }

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun Context.isOnline(): Boolean {
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
    fun Context.isOnline(failBlock : () -> Unit  = { globalInternetFailBock() }, successBlock : () -> Unit ) {
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

    private fun globalInternetFailBock() {
        // show alter to user or implement custom code here
    }
}