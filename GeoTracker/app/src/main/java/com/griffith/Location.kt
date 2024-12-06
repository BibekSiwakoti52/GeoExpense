package com.griffith

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LocationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val location = intent.getStringExtra("EXTRA_LOCATION") ?: "Unknown Location"

        setContent {
            Location(location = location)
        }
    }
}

@Composable
fun Location(location: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Location: $location",
            fontSize = 20.sp,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewLocation() {
    Location(location = "Dublin")
}
