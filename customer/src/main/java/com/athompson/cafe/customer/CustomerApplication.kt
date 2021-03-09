package com.athompson.cafe.customer

import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.athompson.cafelib.shared.CafeQRApplication


open class CustomerApplication : CafeQRApplication(),CameraXConfig.Provider {

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}