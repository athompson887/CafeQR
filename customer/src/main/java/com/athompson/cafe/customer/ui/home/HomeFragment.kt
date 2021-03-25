package com.athompson.cafe.customer.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.athompson.cafe.customer.Enums
import com.athompson.cafe.customer.R
import com.athompson.cafe.customer.databinding.FragmentHomeBinding
import com.athompson.cafe.customer.ui.home.barcode.BarcodeGraphic
import com.athompson.cafe.customer.ui.home.barcode.GraphicOverlay
import com.athompson.cafe.customer.ui.home.barcode.VisionProcessorBase
import com.athompson.cafelib.extensions.FragmentExtensions.logDebug
import com.athompson.cafelib.extensions.StringExtensions.safe
import com.athompson.cafelib.extensions.ToastExtensions.showShortToast
import com.athompson.cafelib.extensions.ViewExtensions.remove
import com.athompson.cafelib.extensions.ViewExtensions.show
import com.athompson.cafelib.firestore.FireStoreClassShared
import com.athompson.cafelib.shared.CafeQrData
import com.athompson.cafelib.shared.fromJson
import com.athompson.cafelib.shared.valid
import com.google.android.gms.tasks.Task
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector: CameraSelector? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var needUpdateGraphicOverlayImageSourceInfo = false

    companion object{
        const val REQUEST_CODE_PERMISSIONS = 10
        const val SCANNER_CAMERA_PERMISSIONS = 10001
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.progressInfo.observe(viewLifecycleOwner, {
            binding.successView.progressInfo.text = it
        })

        homeViewModel.mode.observe(viewLifecycleOwner, {
            switchMode(mode = it)
        })

       // analyzer = QRCodeImageAnalyzer()
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())


        return root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        binding.scanView.homeButton.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.SCAN)
        }
        binding.welcomeView.button.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
        }
        binding.welcomeView.button.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.SCAN)
        }
        binding.scanView.homeButton.setOnClickListener {
            homeViewModel.setMode(Enums.HomeScreenMode.WELCOME)
        }

        permissionsCheck()

        FireStoreClassShared().getOrganisationList(this@HomeFragment)
    }



    private fun switchMode(mode: Enums.HomeScreenMode) {
        if (this::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
        binding.welcomeView.welcomeView.remove()
        binding.scanView.scanView.remove()
        binding.errorView.errorView.remove()
        binding.successView.successView.remove()

        when (mode) {
            Enums.HomeScreenMode.WELCOME -> {
                binding.welcomeView.welcomeView.show()
            }
            Enums.HomeScreenMode.SCAN -> {
                binding.scanView.scanView.show()
                prepareScanner()
            }
            Enums.HomeScreenMode.ERROR -> {
                binding.errorView.errorView.show()
            }
            Enums.HomeScreenMode.SUCCESS -> {
                binding.successView.successView.show()
            }
        }
    }
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindBarcodeAnalyser() {

        val imageProcessor = BarcodeScannerProcessor(requireContext())
        val builder = ImageAnalysis.Builder()
        val targetResolution = Size(800,800)
        builder.setTargetResolution(targetResolution)
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped =
                        lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees =
                        imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        binding.scanView.graphicOverlay?.setImageSourceInfo(
                            imageProxy.width, imageProxy.height, isImageFlipped
                        )
                    } else {
                        binding.scanView.graphicOverlay?.setImageSourceInfo(
                            imageProxy.height, imageProxy.width, isImageFlipped
                        )
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    imageProcessor.processImageProxy(imageProxy, binding.scanView.graphicOverlay)
                } catch (e: MlKitException) {
                    showShortToast(e.localizedMessage.safe())
                }
            }
        )
        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, analysisUseCase)
    }

    private fun prepareScanner() {
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
            bindBarcodeAnalyser()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()
        val preview: Preview = Preview.Builder()
                .build()
        cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        preview.setSurfaceProvider(binding.scanView.previewView.surfaceProvider)
    }


    fun showCafe(data: CafeQrData)
    {
        logDebug(data.toString())
        homeViewModel.setMode(Enums.HomeScreenMode.SUCCESS)
    }

    inner class BarcodeScannerProcessor(context: Context) : VisionProcessorBase<List<Barcode>>(context) {

        private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        override fun stop() {
            super.stop()
            barcodeScanner.close()
        }

        override fun detectInImage(image: InputImage): Task<List<Barcode>> {
            return barcodeScanner.process(image)
        }

        override fun onSuccess(results: List<Barcode>, graphicOverlay: GraphicOverlay) {
            if (results.isEmpty()) {
                Log.v("MANUAL_TESTING_LOG", "No barcode has been detected")
            }
            for (i in results.indices) {
                val barcode = results[i]
                graphicOverlay.add(BarcodeGraphic(graphicOverlay, barcode))
                when (barcode.valueType) {
                    Barcode.TYPE_TEXT -> {
                        val rawValue = barcode.rawValue
                        val cafeData = rawValue?.fromJson()
                        if (cafeData?.valid() == true) {
                            showCafe(cafeData)
                        }
                    }
                }
            }
        }

        override fun onFailure(e: Exception) {
            Log.e("MANUAL_TESTING_LOG", "Barcode detection failed $e")
        }
    }
    private fun permissionsCheck()
    {
        if (!allPermissionsGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {

                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle(getString(R.string.permission_required))
                alert.setMessage(getString(R.string.to_scan_camera_required))
                alert.setPositiveButton(R.string.ok) { dialog: DialogInterface, _: Int ->
                    requestPermission()
                    dialog.dismiss()
                }
                alert.setCancelable(false)
                alert.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                alert.show()

            } else {
                //does not need to show rational
                requestPermission()
            }
        } else {
            prepareScanner()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            SCANNER_CAMERA_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
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
}