package com.griffith

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun Location(location: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Location: $location",
            fontSize = 16.sp,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewLocation() {
    Location(location = "Dublin") // Using Dublin as a dummy location until the real location is fetched from the GPS
}
