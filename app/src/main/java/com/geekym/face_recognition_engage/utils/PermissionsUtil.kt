package com.geekym.face_recognition_engage.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionsUtil {

    private const val REQUEST_CODE = 1

    val cameraPermissions = getCameraPermissions()
    val locationPermissions = getLocationPermission()

    /**
     * Retrieves the location permissions required for accessing fine and coarse location.
     *
     * @return An array of location-related permissions.
     */
    private fun getLocationPermission() = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    /**
     * Retrieves the appropriate camera permissions based on the device's API level.
     *
     * On devices with API level higher than Android 9 (P), only the camera permission is included
     * in the array of permissions.
     *
     * On devices with API level lower than or equal to Android 9 (P), both camera, write external storage,
     * and read external storage permissions are included in the array of permissions.
     *
     * @return An array of camera-related permissions based on the device's API level.
     */
    @JvmName("getCameraPermissions1")
    private fun getCameraPermissions(): Array<String> {
        var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            permissions = arrayOf(Manifest.permission.CAMERA)
        }
        return permissions
    }


    /**
     * Checks if the given permissions are granted.
     *
     * @param context The context object representing the current state of the application.
     * @param permissions An array of permission strings that need to be checked.
     * @return Returns true if all the permissions are granted, false otherwise.
     */
    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * Requests multiple permissions from the user using the convenient registerForActivityResult API.
     *
     * @param permissions An array of permission strings that need to be requested.
     * @param onPermissionGranted Callback function to be invoked when permissions are granted.
     * @param onPermissionDenied Callback function to be invoked when permissions are denied.
     */


//    fun Activity.requestPermissionsUtil(
//        permissions: Array<String>,
//        onPermissionGranted: PermissionGrantedCallback,
//        onPermissionDenied: PermissionDeniedCallback
//    ) {
//        val permissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissionsResult ->
//            val grantedPermissions = mutableListOf<String>()
//            val deniedPermissions = mutableListOf<String>()
//            var permanentlyDenied = false
//
//            permissionsResult.entries.forEach { (permission, isGranted) ->
//                if (isGranted) {
//                    grantedPermissions.add(permission)
//                } else {
//                    deniedPermissions.add(permission)
//                    if (!shouldShowRequestPermissionRationale(permission)) {
//                        permanentlyDenied = true
//                    }
//                }
//            }
//
//            if (grantedPermissions.isNotEmpty()) {
//                onPermissionGranted(grantedPermissions)
//            }
//
//            if (deniedPermissions.isNotEmpty()) {
//                onPermissionDenied(deniedPermissions, permanentlyDenied)
//            }
//        }
//
//        val missingPermissions = mutableListOf<String>()
//
//        // Check for permissions that are not granted
//        permissions.forEach { permission ->
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    permission
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                missingPermissions.add(permission)
//            }
//        }
//
//        // Request permissions only if there are missing permissions
//        if (missingPermissions.isNotEmpty()) {
//            permissionLauncher.launch(missingPermissions.toTypedArray())
//        } else {
//            // All permissions are already granted
//            onPermissionGranted(permissions.toList())
//        }
//    }

    /**
     * Displays a dialog informing the user that permission has been denied and provides options to either go back or go to settings.
     *
     * @param message The message to be displayed in the dialog.
     * @param onGoBack Callback function to be invoked when the user chooses to go back.
     * @param onGoToSettings Callback function to be invoked when the user chooses to go to settings.
     */
    fun Activity.showPermissionDeniedDialog(
        message: String,
        negativeButtonText: String,
        positiveButtonText: String,
        onNegativeButtonClickListener: () -> Unit,
        onPositiveButtonClickListener: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveButtonClickListener()
            }
            .setNegativeButton(negativeButtonText) { _, _ ->
                onNegativeButtonClickListener()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Opens the app settings page for the current application to allow the user to manage app permissions.
     */
    fun Activity.gotoAppSettingsPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CODE)
    }
}