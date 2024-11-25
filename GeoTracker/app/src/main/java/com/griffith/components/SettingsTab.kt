package com.griffith.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTab(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    var isDarkModeChecked by remember { mutableStateOf(isDarkMode) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Dark Mode Toggle
        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Switch(
            checked = isDarkModeChecked,
            onCheckedChange = {
                isDarkModeChecked = it
                onDarkModeToggle(it)
            },
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = if (isDarkModeChecked) "Dark Mode" else "Light Mode",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        Button(onClick = { /* TODO: Add sign-out functionality */ }) {
            Text("Sign Out")
        }
    }
}
