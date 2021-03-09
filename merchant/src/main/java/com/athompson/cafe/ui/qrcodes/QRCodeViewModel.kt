package com.athompson.cafe.ui.qrcodes

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.athompson.cafelib.helpers.QRCodeHelper
import com.athompson.cafelib.shared.CafeQrData
import com.athompson.cafelib.shared.toJson

class QRCodeViewModel : ViewModel() {

    private var _qrImage = MutableLiveData<Bitmap>()
    val qrImage: LiveData<Bitmap> = _qrImage

    fun generateBarCode(data: CafeQrData) {
        val json = data.toJson()
        if(json!=null) {
            val bmp = QRCodeHelper.generateQRCode(json, 300)
            _qrImage.postValue(bmp)
        }
    }
}