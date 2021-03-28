package de.jonas_thelemann.uni.gosoan.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.showSnackbar
import timber.log.Timber

const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

class LocationService {
    companion object {
        fun checkPermissions(context: Context) =
            ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        fun requestPermissions(activity: AppCompatActivity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) {
                // Provide an additional rationale to the user. This would happen if the user denied the
                // request previously, but didn't check the "Don't ask again" checkbox.
                Timber.i("Displaying permission rationale to provide additional context.")
                showSnackbar(activity, R.string.permission_rationale, android.R.string.ok, Snackbar.LENGTH_INDEFINITE
                ) {
                    // Request permission
                    startLocationPermissionRequest(activity)
                }

            } else {
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                Timber.i("Requesting permission")
                startLocationPermissionRequest(activity)
            }
        }

        private fun startLocationPermissionRequest(activity: Activity) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
}