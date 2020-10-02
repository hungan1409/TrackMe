package com.example.trackme.common

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.example.trackme.R
import com.example.trackme.base.BaseFragment
import com.example.trackme.base.BaseViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

abstract class LocationPermissionFragment<T : ViewDataBinding, V : BaseViewModel> :
    BaseFragment<T, V>() {

    private var explainLocationPermissionDialog: AlertDialog? = null

    private var explainLocationPermissionDeniedDialog: AlertDialog? = null

    private val locationPermissionViewModel: LocationPermissionViewModel by viewModels { viewModelFactory }

    override fun onDestroy() {
        explainLocationPermissionDialog?.dismiss()
        explainLocationPermissionDeniedDialog?.dismiss()
        super.onDestroy()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        super.onPermissionsGranted(requestCode, perms)
        onLocationPermissionGranted(perms)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        super.onPermissionsDenied(requestCode, perms)
        handleRequestLocationPermissionDenied(requestCode, perms)
    }

    open fun onLocationPermissionGranted(perms: List<String>) = Unit

    fun requestFineLocationPermission() {
        if (hasAccessFineLocationPermission()) {
            Timber.i("ACCESS_FINE_LOCATION permission was granted")
            onLocationPermissionGranted(listOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            Timber.i("Request ACCESS_FINE_LOCATION permission")
            if (locationPermissionViewModel.shouldExplainFineLocation()) {
                showExplainLocationPermissionDialog(getString(R.string.request_location_permission_explanation)) {
                    requestPermission(
                        getString(R.string.fine_location_permission_rationale),
                        REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            } else {
                requestPermission(
                    getString(R.string.fine_location_permission_rationale),
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (hasAccessBackgroundLocationPermission()) {
                Timber.i("ACCESS_BACKGROUND_LOCATION permission was granted")
                onLocationPermissionGranted(listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            } else {
                Timber.i("Request ACCESS_BACKGROUND_LOCATION permission")
                if (locationPermissionViewModel.shouldExplainBackgroundLocation()) {
                    showExplainLocationPermissionDialog(getString(R.string.request_background_permission_explanation)) {
                        requestPermission(
                            getString(R.string.background_location_permission_rationale),
                            REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    }
                } else {
                    requestPermission(
                        getString(R.string.background_location_permission_rationale),
                        REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
            }
        } else {
            requestFineLocationPermission()
        }
    }

    fun hasAccessFineLocationPermission(): Boolean = EasyPermissions.hasPermissions(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun hasAccessBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            hasAccessFineLocationPermission()
        }
    }

    private fun showExplainLocationPermissionDialog(message: String, requestAction: () -> Unit) {
        explainLocationPermissionDialog?.dismiss()
        explainLocationPermissionDialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                requestAction()
            }
            .setCancelable(false)
            .create()
        explainLocationPermissionDialog?.show()
    }

    private fun handleRequestLocationPermissionDenied(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE -> {
                Timber.i("ACCESS_FINE_LOCATION permission was denied")
                explainLocationPermissionDenied(
                    perms = perms,
                    resId = R.string.fine_permission_denied_explanation
                ) {
                    requestFineLocationPermission()
                }
            }

            REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE -> {
                Timber.i("ACCESS_BACKGROUND_LOCATION permission was denied")
                explainLocationPermissionDenied(
                    perms = perms,
                    resId = R.string.background_permission_denied_explanation
                ) {
                    requestBackgroundLocationPermission()
                }
            }
        }
    }

    private fun explainLocationPermissionDenied(
        perms: List<String>,
        resId: Int,
        action: () -> Unit
    ) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            explainLocationPermissionDeniedDialog?.dismiss()
            explainLocationPermissionDeniedDialog = AlertDialog.Builder(requireContext())
                .setMessage(resId)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.cancel()
                    AppSettingsDialog.Builder(this).build().show()
                }
                .setCancelable(false)
                .create()
            explainLocationPermissionDeniedDialog?.show()
        } else {
            action()
        }
    }

    companion object {
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 100
        private const val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 101
    }
}
