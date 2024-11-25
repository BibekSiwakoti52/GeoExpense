package com.griffith.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpenseInput(
    expenseAmount: String,
    selectedCategory: String,
    customCategory: String,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onCustomCategoryChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val categories = listOf("Food", "Entertainment", "Transportation", "Shopping", "Other")
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Amount Input
        TextField(
            value = expenseAmount,
            onValueChange = onAmountChange,
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Selector
        Box {
            TextButton(onClick = { expanded = !expanded }) {
                Text("Category: $selectedCategory")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategoryChange(category)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom Category Input (when "Other" is selected)
        if (selectedCategory == "Other") {
            TextField(
                value = customCategory,
                onValueChange = onCustomCategoryChange,
                label = { Text("Custom Category") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
    }
}
