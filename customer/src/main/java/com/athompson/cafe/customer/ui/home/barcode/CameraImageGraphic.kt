package com.athompson.cafe.customer.ui.home.barcode

import android.graphics.Bitmap
import android.graphics.Canvas
import com.athompson.cafe.customer.ui.home.barcode.GraphicOverlay.Graphic

/** Draw camera image to background.  */
class CameraImageGraphic(overlay: GraphicOverlay?, private val bitmap: Bitmap) : Graphic(overlay) {
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, transformationMatrix, null)
    }
}