package com.geekym.face_recognition_engage.HomeFragments.Homescreen.qr_screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.Attendance.Attendance_Result_Activity
import com.geekym.face_recognition_engage.R
import com.geekym.face_recognition_engage.databinding.ActivityQrscannerBinding
import com.geekym.face_recognition_engage.utils.PermissionsUtil
import com.geekym.face_recognition_engage.utils.UtilsKt.jsonToClassPrompt
import com.google.android.material.snackbar.Snackbar

class QRScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrscannerBinding
    private lateinit var codeScanner: CodeScanner
    private var isFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrscannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        codeScanner = CodeScanner(this, binding.scannerView)

        checkForCameraPermissions()
        setupScanner()

        binding.flashImageView.setOnClickListener {
            isFlashOn = !isFlashOn
            toggleFlash(isFlashOn)
        }
        binding.closeImageView.setOnClickListener {
            onBackPressed()
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Handle Permission granted/rejected
            if (isGranted) {
                // Permission is granted
                codeScanner.startPreview()
            } else {
                // Permission is denied
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun toggleFlash(isFlashOn: Boolean) {
        binding.flashImageView.background = if (isFlashOn)
            AppCompatResources.getDrawable(this, com.budiyev.android.codescanner.R.drawable.ic_code_scanner_flash_on)
            else AppCompatResources.getDrawable(this, com.budiyev.android.codescanner.R.drawable.ic_code_scanner_flash_on)

        codeScanner.isFlashEnabled = isFlashOn
    }

    private fun toggleStatusView(isSuccess: Boolean, shouldReset: Boolean = false) {
        binding.scannerView.frameColor = ContextCompat.getColor(
            this,
            if (shouldReset) R.color.white else if (isSuccess) R.color.green_desat else R.color.red_desat
        )
//        binding.invalidQrTextView.isVisible = if (shouldReset) false else !isSuccess
        binding.scannerView.apply {
            invalidate()
            requestLayout()
        }
    }

    private fun setupScanner() {
        codeScanner.isAutoFocusEnabled = true
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.formats = CodeScanner.TWO_DIMENSIONAL_FORMATS
        codeScanner.decodeCallback = DecodeCallback {
            this.runOnUiThread {
                codeScanner.stopPreview()
                val text = it.text
                Log.d("", "Scanned data: $text")
                val classPrompt = text.jsonToClassPrompt()
                classPrompt?.let {
                    val intent = Intent(this, Attendance_Result_Activity::class.java)
                    intent.putExtra("classPrompt", classPrompt)
                    startActivity(intent)
                } ?: run {
                    Toast.makeText(this, "Invalid QR", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            this.runOnUiThread {
                toggleStatusView(false)
                it.message?.let { exceptionMessage ->
                    Snackbar.make(
                        binding.root,
                        exceptionMessage,
                        Snackbar.LENGTH_INDEFINITE
                    ).show()
                }
            }
        }
        toggleStatusView(true, shouldReset = true)
    }


    private fun checkForCameraPermissions() {
        val permissions = PermissionsUtil.cameraPermissions
        if (isHavingCameraPermissions().not()) {
            activityResultLauncher.launch(permissions[0])
        }
    }

    private fun isHavingCameraPermissions() =
        PermissionsUtil.checkPermissions(this, PermissionsUtil.cameraPermissions)

    override fun onPause() {
        if (isHavingCameraPermissions()) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isHavingCameraPermissions()) {
            toggleStatusView(true, shouldReset = true)
            codeScanner.startPreview()
        }
    }

    override fun onStop() {
        if (isHavingCameraPermissions()) {
            codeScanner.stopPreview()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (isHavingCameraPermissions()) {
            codeScanner.releaseResources()
        }
        super.onDestroy()
    }
}