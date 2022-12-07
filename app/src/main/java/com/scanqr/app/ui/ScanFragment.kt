package com.scanqr.app.ui

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.scanqr.R
import com.scanqr.app.utils.Constants.CAMERA_PERMISSION_REQUEST_CODE
import kotlinx.android.synthetic.main.fragment_scan.*
import pub.devrel.easypermissions.EasyPermissions


class ScanFragment : Fragment(R.layout.fragment_scan) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_scan.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                add<BarcodeScanFragment>(R.id.fcv_main)
            }


        }
    }


}


