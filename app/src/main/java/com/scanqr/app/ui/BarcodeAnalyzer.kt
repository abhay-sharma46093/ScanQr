package com.scanqr.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.media.Image
import android.view.SurfaceView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerCreator
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.internal.BarcodeScannerOptionsParcel
import com.google.mlkit.vision.common.InputImage
import com.scanqr.R

class BarcodeAnalyzer(private val barcodeListener: BarcodeListener):ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()



    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage =imageProxy.image

        if (mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage,imageProxy.imageInfo.rotationDegrees)

            scanner.process(image).addOnSuccessListener {barcodes ->
                for (barcode in barcodes){
                    barcodeListener(barcode.rawValue ?:"")
                }
            }.addOnCompleteListener {
                    imageProxy.close()
                }

        }
    }

//    @SuppressLint("UnsafeOptInUsageError")
//    fun setRect(imageProxy: ImageProxy): Rect {
//        val height = .height
//        val width = .width
//        var diameter = width
//
//        if (height < width!!) {
//            diameter = height
//        }
//
//        var offset = 0.05 * diameter
//        diameter -= offset.toInt()
//
//        var left = width / 2 - diameter / 3
//        var top = height / 2 - diameter / 3
//        var right = width / 2 + diameter / 3
//        var bottom = height / 2 + diameter / 3
//
//        return Rect(left, top, right, bottom)
//
//
//    }

}