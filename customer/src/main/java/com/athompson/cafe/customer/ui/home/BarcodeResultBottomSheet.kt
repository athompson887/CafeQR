package com.athompson.cafe.customer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.athompson.cafe.customer.R
import com.athompson.cafelib.shared.CafeQrData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BarcodeResultBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_barcode_data, container, false)

    fun showCafe(data: CafeQrData?) {
            view?.apply {
                findViewById<TextView>(R.id.organisation)?.text = data?.organization.toString()
                findViewById<TextView>(R.id.venue)?.text = data?.venue.toString()
                findViewById<TextView>(R.id.table)?.text = data?.table.toString()
            }
        }
    }
