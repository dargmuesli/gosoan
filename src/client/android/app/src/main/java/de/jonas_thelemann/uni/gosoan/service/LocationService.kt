package de.jonas_thelemann.uni.gosoan.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import de.jonas_thelemann.uni.gosoan.R
import de.jonas_thelemann.uni.gosoan.showSnackbar
import timber.log.Timber
import java.util.concurrent.TimeUnit

const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

class LocationService {
    companion object {
        fun checkPermissions(context: Context) =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        fun requestPermissions(activity: AppCompatActivity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Timber.i("Displaying permission rationale to provide additional context.")
                showSnackbar(
                    activity,
                    R.string.permission_rationale,
                    android.R.string.ok,
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    // Request permission
                    startLocationPermissionRequest(activity)
                }
            } else {
                Timber.i("Requesting permission")
                startLocationPermissionRequest(activity)
            }
        }

        private fun startLocationPermissionRequest(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private lateinit var locationRequest: LocationRequest

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            println(locationResult.lastLocation)
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun onCreate(context: Context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun registerListener(measurementFrequency: Int) {
        if (this::fusedLocationProviderClient.isInitialized) {
            locationRequest = LocationRequest.create().apply {
                interval = TimeUnit.MICROSECONDS.toMillis(measurementFrequency.toLong())
                fastestInterval = TimeUnit.MICROSECONDS.toMillis(measurementFrequency.toLong())
//        maxWaitTime = TimeUnit.MINUTES.toMillis(2)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun unregisterListener() {
        if (this::fusedLocationProviderClient.isInitialized && this::locationRequest.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}