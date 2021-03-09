package com.athompson.cafelib.extensions
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.athompson.cafelib.shared.AppPermission
import com.athompson.cafelib.shared.SharedConstants

object FragmentExtensions {


    fun Fragment.toolBarTitle(title:String) {
        val act = this.requireActivity() as AppCompatActivity
        act.supportActionBar?.title = title
    }
    fun Fragment.toolBarSubTitle(subtitle:String) {
        val act = this.requireActivity() as AppCompatActivity
        act.supportActionBar?.subtitle = subtitle
    }

    fun Fragment.logInfo(msg:String) {
        if(SharedConstants.LOGGING_ON)
            Log.i("", msg)
    }
    fun Fragment.logDebug(msg:String) {
        if(SharedConstants.LOGGING_ON)
            Log.d("", msg)
    }
    fun Fragment.logError(msg:String) {
        if(SharedConstants.LOGGING_ON)
            Log.e("", msg)
    }

    fun Fragment.logInfo(caller:String, msg:String) {
        if(SharedConstants.LOGGING_ON)
            Log.i(caller, msg)
    }
    fun Fragment.logDebug(caller:String, msg:String) {
        if(SharedConstants.LOGGING_ON)
            Log.d(caller, msg)
    }
    fun Fragment.logError(caller:String, msg:String) {
        if(SharedConstants.LOGGING_ON)
            Log.e(caller, msg)
    }

    inline fun <reified T: Any> Fragment.getValue(label: String, defaultValue: T? = null) = lazy {
        val value = arguments?.get(label)
        if (value is T) value else defaultValue
    }

    inline fun <reified T: Any> Fragment.getValueNonNull(label: String, defaultValue: T? = null) = lazy {
        val value = arguments?.get(label)
        requireNotNull(if (value is T) value else defaultValue) { label }
    }}
    //https://betterprogramming.pub/5-more-kotlin-extensions-for-android-developers-3857b1f16407
    //https://proandroiddev.com/easy-way-to-ask-for-permissions-on-android-62a9ae4a22b0
    fun Fragment.isGranted(permission: AppPermission) = run {
        context?.let {
            (PermissionChecker.checkSelfPermission(it, permission.permissionName
            ) == PermissionChecker.PERMISSION_GRANTED)
        } ?: false
    }

    fun Fragment.shouldShowRationale(permission: AppPermission) = run {
        shouldShowRequestPermissionRationale(permission.permissionName)
    }

    fun Fragment.requestPermission(permission: AppPermission) {
        requestPermissions(arrayOf(permission.permissionName), permission.requestCode
        )
    }