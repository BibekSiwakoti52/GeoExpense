package com.griffith

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Settings(onSignOut: () -> Unit) {
    var isDarkMode by remember { mutableStateOf(false) } // Dark mode state

    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor) // Apply background color
    ) {
        // Theme title text
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Dark Mode Toggle Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isDarkMode) "Dark Mode" else "Light Mode",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Switch(
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.Gray,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        TextButton(onClick = { onSignOut() }) {
            Text(
                text = "Sign Out",
                color = Color.Red
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSettings() {
    Settings(onSignOut = {})
}
