package com.scanqr.app.ui

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic
import com.google.android.gms.vision.barcode.Barcode
import com.scanqr.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MultiBarcodeScannerFragment : Fragment(R.layout.fragment_multi_barcode_scanner),
    BarcodeRetriever {


    private var resultList = MutableLiveData<String>()
    private var mainList = arrayListOf<String>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val barcodeCapture = childFragmentManager.findFragmentById(R.id.barcode) as BarcodeCapture
        barcodeCapture.setRetrieval(this)
        observeData()
    }

    companion object {

        private const val TAG = "Scanner"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MultiBarcodeScannerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRetrieved(barcode: Barcode?) {
        Log.e(TAG, "Barcode read: " + barcode!!.displayValue)
        resultList.postValue(barcode.displayValue.toString())
        /* CoroutineScope(Dispatchers.Main).launch {
             val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                 .setTitle("code retrieved")
                 .setMessage(barcode.displayValue)
             builder.show()
         }*/

    }

    private fun observeData() {
        resultList.observe(requireActivity()) {
            mainList.add(it)
            printValues()
        }
    }

    private fun printValues() {
        Log.e(TAG, "printValues: ${mainList.toSet()}")
    }

    override fun onRetrievedMultiple(
        closetToClick: Barcode?,
        barcode: MutableList<BarcodeGraphic>?
    ) {
        Log.e(TAG, "onRetrievedMultiple: list!")
        CoroutineScope(Dispatchers.Main).launch {
            barcode?.let {
                var message = """
              Code selected : ${closetToClick!!.displayValue}
              
              other codes in frame include : 
              
              """.trimIndent()
                for (index in 0 until barcode.size) {
                    val barcode: Barcode = barcode[index].barcode
                    message += """
                  ${index + 1}. ${barcode.displayValue}
                  
                  """.trimIndent()
                }


                val builder = AlertDialog.Builder(requireContext())
                    .setTitle("code retrieved")
                    .setMessage(message)
                builder.show()
            } ?: run {
                Log.e(TAG, "onRetrievedMultiple: list empty!")
            }
        }
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {
        Log.e(TAG, "onRetrievedMultiple: Bitmap!")
    }

    override fun onRetrievedFailed(reason: String?) {
        Log.e(TAG, "onRetrievedMultiple: Failed!")
    }

    override fun onPermissionRequestDenied() {
        Log.e(TAG, "onRetrievedMultiple: Permission Denied!")
    }
}