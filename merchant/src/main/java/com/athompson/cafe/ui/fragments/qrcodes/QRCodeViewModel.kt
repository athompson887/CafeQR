package com.athompson.cafe.ui.fragments.qrcodes

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.athompson.cafelib.helpers.QRCodeHelper
import com.athompson.cafelib.models.Venue

class QRCodeViewModel : ViewModel() {

    private var _qrImage = MutableLiveData<Bitmap>()
    val qrImage: LiveData<Bitmap> = _qrImage

    fun generateBarCode(data: Venue) {
        val bmp = QRCodeHelper.generateQRCode(data.id.toString(), 300)
            _qrImage.postValue(bmp)
        }
    }