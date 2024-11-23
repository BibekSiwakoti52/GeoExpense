package com.griffith

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date

data class Expense(var title: String, var amount: Double, val location: String, var date: Date)

@Composable
fun HomeScreen(currentLocation: String) {
    // State for expenses list with dummy data
    //TODO: Fetch real data
    var expenses by remember {
        mutableStateOf(
            listOf(
                Expense("Groceries", 20.0, "Dublin", Date()),
                Expense("Rent", 500.0, "Dublin", Date())
            )
        )
    }

    // State for tab selection
    var selectedTab by remember { mutableStateOf(0) }
    var expenseAmount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Other") }
    var customCategory by remember { mutableStateOf("") }

    // Edit mode states
    var isEditing by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }

    // Dark Mode State
    var isDarkMode by remember { mutableStateOf(false) }

    // Category options
    val categories = listOf("Food", "Entertainment", "Transportation", "Shopping", "Other")

    // Function to add or edit an expense
    fun saveExpense() {
        val amount = expenseAmount.toDoubleOrNull()
        val finalCategory = if (selectedCategory == "Other" && customCategory.isNotEmpty()) customCategory else selectedCategory
        if (amount != null && finalCategory.isNotEmpty()) {
            if (isEditing && expenseToEdit != null) {
                expenseToEdit?.apply {
                    this.amount = amount
                    this.title = finalCategory
                }
                isEditing = false
                expenseToEdit = null
            } else {
                expenses = expenses + Expense(finalCategory, amount, currentLocation, Date())
            }
            expenseAmount = ""
            selectedCategory = "Other"
            customCategory = ""
        }
    }

    // Function to delete an expense
    fun deleteExpense(expense: Expense) {
        expenses = expenses - expense
    }

    // Set background and text color based on dark mode
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { saveExpense() }) {
                Text(if (isEditing) "Save" else "+")
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(backgroundColor) // Apply background color for the whole column
            ) {
                // Tab Row for selecting the tabs
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Expense", modifier = Modifier.padding(16.dp), color = textColor)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Location", modifier = Modifier.padding(16.dp), color = textColor)
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("Settings", modifier = Modifier.padding(16.dp), color = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (selectedTab) {
                    0 -> {
                        // Expense Input
                        Column {
                            TextField(
                                value = expenseAmount,
                                onValueChange = { expenseAmount = it },
                                label = { Text("Amount", color = textColor) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Dropdown for category
                            Box {
                                TextButton(onClick = { expanded = !expanded }) {
                                    Text("Category: $selectedCategory", color = textColor)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category, color = textColor) },
                                            onClick = {
                                                selectedCategory = category
                                                expanded = false
                                                customCategory = "" // Reset custom category if not 'Other'
                                            }
                                        )
                                    }
                                }
                            }

                            // Input when other is selected as category
                            if (selectedCategory == "Other") {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = customCategory,
                                    onValueChange = { customCategory = it },
                                    label = { Text("Enter Custom Category", color = textColor) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Recent Expenses",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Expense List
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(expenses) { expense ->
                                ExpenseItem(
                                    expense = expense,
                                    onDelete = { deleteExpense(expense) },
                                    onEdit = {
                                        isEditing = true
                                        expenseToEdit = expense
                                        expenseAmount = expense.amount.toString()
                                        selectedCategory = if (expense.title in categories) expense.title else "Other"
                                        customCategory = if (selectedCategory == "Other") expense.title else ""
                                    },
                                    textColor = textColor // Pass text color to the expense item
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    1 -> {
                        Location(location = currentLocation)
                    }
                    2 -> {
                        // Settings Tab
                        Settings(
                            isDarkMode = isDarkMode,
                            onDarkModeToggle = { isDarkMode = it },
                            textColor = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Settings(isDarkMode: Boolean, onDarkModeToggle: (Boolean) -> Unit, textColor: Color) {
    var isDarkModeChecked by remember { mutableStateOf(isDarkMode) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Dark Mode Toggle
        Text("Theme", style = MaterialTheme.typography.titleMedium, color = textColor)
        Switch(
            checked = isDarkModeChecked,
            onCheckedChange = {
                isDarkModeChecked = it
                onDarkModeToggle(it)
            },
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(if (isDarkModeChecked) "Dark Mode" else "Light Mode", style = MaterialTheme.typography.bodyMedium, color = textColor)

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        TextButton(onClick = {}) {  //TODO: Create an System for Authentication
            Text("Sign Out", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, onDelete: () -> Unit, onEdit: () -> Unit, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = expense.title, fontSize = 16.sp, color = textColor)
            Text(
                text = "€${expense.amount}",
                fontSize = 16.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Date: ${SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(expense.date)}",
                fontSize = 12.sp, color = Color.Gray)
            Text(text = "Location: ${expense.location}", fontSize = 12.sp, color = Color.Gray)
        }
        Row {
            TextButton(onClick = onEdit) {
                Text("Edit", color = Color.Blue)
            }
            TextButton(onClick = onDelete) {
                Text("Delete", color = Color.Red)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen() {
    HomeScreen("Dublin")
}