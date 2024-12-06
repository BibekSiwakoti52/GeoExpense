package com.griffith

import android.Manifest
import android.os.Bundle
import android.os.Looper
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
import androidx.core.app.ActivityCompat
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: String by mutableStateOf("Unknown")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates()
            }
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            HomeScreen(currentLocation)
        }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    getLocationInfo(location.latitude, location.longitude)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun getLocationInfo(latitude: Double, longitude: Double) {
        val client = OkHttpClient()
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&accept-language=en"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Expense Manager")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrEmpty()) {
                    val jsonObject = JsonParser.parseString(responseBody).asJsonObject
                    Log.d("LocationInfo", "API Response: $responseBody")
                    val address = jsonObject.getAsJsonObject("address")
                    val place = address.get("neighbourhood")?.asString ?: "Unknown Place"
                    val state = address.get("state")?.asString ?: address.get("town")?.asString ?: "Unknown City"
                    val country = address.get("country")?.asString ?: "Unknown Country"

                    val formattedLocation = "$place, $state, $country"
                    withContext(Dispatchers.Main) {
                        currentLocation = formattedLocation
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        currentLocation = "Location not found"
                    }
                }
            } catch (e: Exception) {
                Log.e("LocationInfo", "Failed to fetch location", e)
                withContext(Dispatchers.Main) {
                    currentLocation = "Failed to fetch location"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
