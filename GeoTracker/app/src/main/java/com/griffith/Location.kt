
package com.griffith

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Location(location: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Location: $location",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewLocation() {
    Location(location = "Dublin") //Using Dublin as a dummy location until the real location is fetched from the gps
}
