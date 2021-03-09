package com.athompson.cafe.ui.qrcodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.athompson.cafe.R
import com.athompson.cafe.databinding.FragmentQrcodeBinding
import com.athompson.cafelib.extensions.StringExtensions.int
import com.athompson.cafelib.extensions.ViewExtensions.hide
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.shared.CafeQrData
import com.athompson.cafelib.shared.SharedConstants
import com.athompson.cafelib.shared.valid

class QRCodeFragment : Fragment() {

    private lateinit var qrCodeViewModel: QRCodeViewModel
    private lateinit var binding:FragmentQrcodeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentQrcodeBinding.bind(view)
        binding.qrImage.hide()
        binding.generateButton.setOnClickListener{
            val dataItem= CafeQrData(SharedConstants.CAFE_QR_ID, binding.organisationCode.int(),binding.venueCode.int(),binding.tableId.int())
            if(dataItem.valid() == true)
                qrCodeViewModel.generateBarCode(dataItem)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        qrCodeViewModel = ViewModelProvider(this).get(QRCodeViewModel::class.java)
        qrCodeViewModel.qrImage.observe(viewLifecycleOwner, Observer {
            binding.qrImage.setImageBitmap(it)
            binding.qrImage.show()
        })
        return  inflater.inflate(R.layout.fragment_qrcode, container, false)
    }
}