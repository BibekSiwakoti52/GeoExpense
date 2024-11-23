package com.griffith

import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import android.location.Geocoder
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: String by mutableStateOf("Unknown")
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault()) // Initialize geocoder

        // Request location permissions
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates()
            }
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            HomeScreen(currentLocation)
        }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000) // fastest interval (5 seconds)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    // Get location info asynchronously
                    getLocationInfo(location.latitude, location.longitude)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun getLocationInfo(latitude: Double, longitude: Double) {
        // Launch a coroutine to handle geocoding asynchronously
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(latitude, longitude, 1)
                }

                // Safely check if the address list is not null and not empty
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]

                    // Get the address parts from the geocoder result
                    val place = address.subLocality ?: "Unknown Place"
                    val city = address.locality ?: "Unknown City"
                    val country = address.countryName ?: "Unknown Country"

                    val formattedLocation = "$place, $city, $country"
                    currentLocation = formattedLocation

                } else {
                    currentLocation = "Location not found"
                    Log.d("LocationInfo", currentLocation) // Log when location is not found
                }
            } catch (e: Exception) {
                // Handle any errors (e.g., network issues or geocoding failures)
                Log.e("LocationInfo", "Failed to fetch location", e)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
