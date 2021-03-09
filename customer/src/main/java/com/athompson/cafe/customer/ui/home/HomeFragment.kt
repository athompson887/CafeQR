package com.athompson.cafe.customer.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.athompson.cafe.customer.Enums
import com.athompson.cafe.customer.R
import com.athompson.cafe.customer.databinding.FragmentHomeBinding
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.helpers.Helper
import com.athompson.cafelib.shared.CafeQrData
import com.athompson.cafelib.shared.fromJson
import com.athompson.cafelib.shared.valid
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class HomeFragment : Fragment() {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var analyzer: QRCodeImageAnalyzer


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.progressInfo.observe(viewLifecycleOwner, Observer {
            binding.successView.progressInfo.text = it
        })

        homeViewModel.mode.observe(viewLifecycleOwner, Observer {
            switchMode(mode = it)
        })

        analyzer = QRCodeImageAnalyzer()
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        binding.errorView.button.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.SCAN)
        }
        binding.errorView.homeButton.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
        }
        binding.welcomeView.button.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.SCAN)
        }
        binding.scanView.homeButton.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
        }

        if (allPermissionsGranted()) {
            prepareScanner()
        }
    }

    fun switchMode(mode: Enums.HomeScreenMode) {
        if (this::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
        binding.welcomeView.welcomeView.remove()
        binding.scanView.scanView.remove()
        binding.errorView.errorView.remove()
        binding.successView.successView.remove()

        if (mode == Enums.HomeScreenMode.WELCOME) {
            binding.welcomeView.welcomeView.show()
        } else if (mode == Enums.HomeScreenMode.SCAN) {
            binding.scanView.scanView.show()
            prepareScanner()
        } else if (mode == Enums.HomeScreenMode.ERROR) {
            binding.errorView.errorView.show()
        } else if (mode == Enums.HomeScreenMode.SUCCESS) {
            binding.successView.successView.show()
        }
    }

    private fun prepareScanner() {
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()
        val preview: Preview = Preview.Builder()
                .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        preview.setSurfaceProvider(binding.scanView.previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(Helper.displayHeightPixels(), Helper.displayWidthPixels()))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                imageAnalysis,
                preview
        )
    }
    fun showCafe(data: CafeQrData)
    {
        homeViewModel.setMode(Enums.HomeScreenMode.SUCCESS)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                prepareScanner()
            } else {
                requireContext().showShortToast(getString(R.string.permissions_not_granted))
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


    inner class QRCodeImageAnalyzer() : ImageAnalysis.Analyzer {

        override fun analyze(imageProxy: ImageProxy) {
            scanBarcode(imageProxy)
        }

        @SuppressLint("UnsafeExperimentalUsageError")
        private fun scanBarcode(imageProxy: ImageProxy) {
            imageProxy.image?.let { image ->
                val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()
                scanner.process(inputImage)
                        .addOnCompleteListener {
                            imageProxy.close()
                            if (it.isSuccessful) {
                                readBarcodeData(it.result as List<Barcode>)
                            } else {
                                it.exception?.printStackTrace()
                            }
                        }
            }
        }

        private fun readBarcodeData(barcodes: List<Barcode>) {
            for (barcode in barcodes) {
                when (barcode.valueType) {
                    Barcode.TYPE_TEXT -> {
                        val rawValue = barcode.rawValue
                        val cafeData = rawValue?.fromJson()
                        if (cafeData?.valid() == true) {
                            homeViewModel.setMode(Enums.HomeScreenMode.SUCCESS)
                            showCafe(cafeData)
                        }
                        else
                        {
                            homeViewModel.setMode(Enums.HomeScreenMode.ERROR)
                        }
                    }
                }
            }
        }
    }
}