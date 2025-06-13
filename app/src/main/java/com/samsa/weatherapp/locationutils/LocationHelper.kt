package com.samsa.weatherapp.locationutils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 20051306 // Любой уникальный код
    }

    suspend fun getSingleLocation(
        onSuccess: (latitude: Double, longitude: Double) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val location = getLastLocation() ?:
            return
            onSuccess(location.latitude, location.longitude)
        } catch (e: SecurityException) {
            onFailure(e)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            // Проверяем разрешение ещё раз, внутри корутины
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        requestSingleLocationUpdate(continuation)
                    }
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
            } else {
                continuation.resumeWithException(SecurityException("Location permission not granted"))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestSingleLocationUpdate(continuation: kotlinx.coroutines.CancellableContinuation<Location?>) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
            .setWaitForAccurateLocation(true)
            .setMaxUpdateAgeMillis(5000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    continuation.resume(location)
                    fusedLocationClient.removeLocationUpdates(this)
                } ?: continuation.resume(null)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (!locationAvailability.isLocationAvailable) {
                    continuation.resume(null)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        continuation.invokeOnCancellation {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}