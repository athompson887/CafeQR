package com.athompson.cafe.customer

import com.athompson.cafe.customer.SavePrefsConstants.SETTINGS
import com.athompson.cafe.customer.SavePrefsConstants.CUSTOMER_EXPIRY_LIMIT
import com.athompson.cafelib.shared.CafeQRApplication.Companion.appInstance

object SavePrefsConstants {
    const val CUSTOMER_EXPIRY_LIMIT = "CUSTOMER_EXPIRY"
    const val SETTINGS = "SETTINGS"
}

class SavePrefs {

    val customerExpiryLimit: Long
        get() = appInstance.getSharedPreferences(SETTINGS, 0)
                .getLong(CUSTOMER_EXPIRY_LIMIT, 3600000L)

    fun saveCustomerExpiryLimit(millis: Long) {
        val prefs = appInstance.getSharedPreferences("Settings", 0)
        val editor = prefs.edit()
        editor.putLong(CUSTOMER_EXPIRY_LIMIT, millis)
        editor.apply()
    }
}