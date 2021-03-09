package com.athompson.cafelib.shared

import android.app.Application

open class CafeQRApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object{
        lateinit var appInstance: CafeQRApplication
    }
}
