package com.scanqr.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.scanqr.R
import com.scanqr.app.utils.Constants
import kotlinx.android.synthetic.main.fragment_barcode_scan.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import android.util.DisplayMetrics
import android.util.Rational
import android.view.*
import androidx.camera.core.*
import androidx.camera.view.PreviewView


typealias BarcodeListener = (barcode:String) ->Unit

class BarcodeScanFragment : Fragment(R.layout.fragment_barcode_scan),
    EasyPermissions.PermissionCallbacks,SurfaceHolder.Callback {
    private lateinit var mCameraView: PreviewView
    private lateinit var holder: SurfaceHolder
    private lateinit var surfaceView:SurfaceView
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private var cameraHeight = 0
    private var cameraWidth:Int = 0
    private var xOffset:Int = 0
    private var yOffset:Int = 0
    private var boxWidth:Int = 0
    private var boxHeight:Int = 0

    private lateinit var cameraExecutor :ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Create the bounding box
        surfaceView = sv_overlay
        surfaceView.setZOrderOnTop(true)
        holder = surfaceView.holder
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)


        startCameraTask()

        val toolbar = tb_barcode_scan as androidx.appcompat.widget.Toolbar
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace<ScanFragment>(R.id.fcv_main)
            }
        }

        btn_scan_again.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace<BarcodeScanFragment>(R.id.fcv_main)
            }
        }

    }


    private fun startCameraTask() {
        if (hasAllPermissions()) {
            startCamera()

        } else {
            val perms =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            EasyPermissions.requestPermissions(
                this,
                "Grant",
                Constants.CAMERA_PERMISSION_REQUEST_CODE,
                *perms
            )
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        //Create an instance of ProcessCameraProvider
        // which will be used to bind the use cases to lifecycle owner
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        // Add a listener to the cameraProviderFuture.
        // The first argument is a Runnable, which will be where the magic actually happens.
        // The second argument (way down below) is an Executor that runs on the main thread.

        cameraProviderFuture.addListener({
            //binding the lifecycle of my camera to lifecycle owner in application's life
            val cameraProvider:ProcessCameraProvider = cameraProviderFuture.get()
            //initializing the preview object
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(pv_barcode_scan.surfaceProvider)
            }
            val viewPort = ViewPort.Builder(Rational(boxWidth,boxHeight), Surface.ROTATION_0).build()

            // Setup the ImageAnalyzer for the ImageAnalysis use case
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(360,360))
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                            tv_barcode_info.text = barcode

                    })
                }
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .setViewPort(viewPort)
                .build()
            //select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind any bound use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to lifecycleOwner
                cameraProvider.bindToLifecycle(this, cameraSelector,useCaseGroup)
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun hasAllPermissions(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return EasyPermissions.hasPermissions(requireContext(), *perms)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startCameraTask()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}