package com.griffith

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.griffith.components.SettingsTab
import com.griffith.components.ExpenseItem
import com.griffith.components.ExpenseInput
import com.griffith.ui.theme.MyApplicationTheme // Import the theme
import java.util.Date

@Composable
fun HomeScreen(currentLocation: String) {
    // State to track expenses
    var expenses by remember { mutableStateOf(listOf(Expense("Groceries", 20.0, "Dublin", Date()))) }

    // States for expense input
    var expenseAmount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Other") }
    var customCategory by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var isDarkMode by remember { mutableStateOf(false) }

    // Function to save a new expense
    fun saveExpense() {
        val amount = expenseAmount.toDoubleOrNull()
        if (amount != null) {
            val finalCategory = if (selectedCategory == "Other" && customCategory.isNotEmpty()) customCategory else selectedCategory
            expenses = expenses + Expense(finalCategory, amount, currentLocation, Date())
            // Reset input fields after saving
            expenseAmount = ""
            selectedCategory = "Other"
            customCategory = ""
        }
    }

    // Wrap the entire screen in MyApplicationTheme to apply the theme
    MyApplicationTheme(darkTheme = isDarkMode) {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // Tab Row
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Expenses", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Location", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("Settings", modifier = Modifier.padding(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Content Based on Selected Tab
                when (selectedTab) {
                    0 -> {
                        // Expense Input Form
                        ExpenseInput(
                            expenseAmount = expenseAmount,
                            selectedCategory = selectedCategory,
                            customCategory = customCategory,
                            onAmountChange = { expenseAmount = it },
                            onCategoryChange = { selectedCategory = it },
                            onCustomCategoryChange = { customCategory = it },
                            onSave = { saveExpense() } // Pass saveExpense as callback
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // List of Expenses
                        LazyColumn {
                            items(expenses) { expense ->
                                ExpenseItem(
                                    expense = expense,
                                    onDelete = { expenses = expenses - expense },
                                    onEdit = {
                                        expenseAmount = expense.amount.toString()
                                        selectedCategory = expense.title
                                        customCategory = if (expense.title == "Other") expense.title else ""
                                    }
                                )
                            }
                        }
                    }
                    1 -> {
                        // Future Location Tab
                        Location(location = currentLocation)
                    }
                    2 -> {
                        // Settings Tab
                        SettingsTab(isDarkMode = isDarkMode, onDarkModeToggle = { isDarkMode = it })
                    }
                }
            }
        }
    }
}
