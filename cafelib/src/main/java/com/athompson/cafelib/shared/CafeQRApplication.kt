package com.athompson.cafelib.shared

import android.app.Application
import com.athompson.cafelib.models.Organisation
import com.athompson.cafelib.models.Venue

open class CafeQRApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object{
        lateinit var appInstance: CafeQRApplication
        var selectedOrganisation: Organisation? = null
        var selectedVenue: Venue? = null
    }
}
