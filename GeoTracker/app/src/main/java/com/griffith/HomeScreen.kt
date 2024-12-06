package com.griffith

import android.content.Intent
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
import com.griffith.ui.theme.MyApplicationTheme
import java.util.Date
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun HomeScreen(currentLocation: String) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("expense_prefs", Context.MODE_PRIVATE)

    val allExpenses by remember {
        mutableStateOf(loadExpenses(sharedPreferences))
    }

    var expenses by remember {
        mutableStateOf(loadExpenses(sharedPreferences))
    }

    var expenseAmount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Other") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var customCategory by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableStateOf(0) }
    var isDarkMode by remember { mutableStateOf(false) }

    fun saveExpense() {
        val amount = expenseAmount.toDoubleOrNull()
        if (amount != null) {
            val finalCategory =
                if (selectedCategory == "Other" && customCategory.isNotEmpty()) customCategory else selectedCategory
            val newExpense = Expense(finalCategory, amount, currentLocation, Date())
            expenses = expenses + newExpense
            saveExpenses(sharedPreferences, expenses)

            expenseAmount = ""
            selectedCategory = "Other"
            customCategory = ""
        }
    }

    fun applyFilters() {
        val filteredExpenses = allExpenses.filter { expense ->
            val dateCondition = when (selectedDateFilter) {
                "Last 1 Day" -> expense.date.after(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
                "Last 7 Days" -> expense.date.after(Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000))
                "Last 15 Days" -> expense.date.after(Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000))
                else -> true
            }

            val categoryCondition = when (selectedCategoryFilter) {
                "All" -> true
                "Other" -> expense.title !in listOf("Food", "Entertainment", "Transportation", "Shopping")
                else -> expense.title == selectedCategoryFilter
            }

            dateCondition && categoryCondition
        }
        expenses = filteredExpenses
    }

    MyApplicationTheme(darkTheme = isDarkMode) {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
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

                when (selectedTab) {
                    0 -> {
                        ExpenseInput(
                            expenseAmount = expenseAmount,
                            selectedCategory = selectedCategory,
                            customCategory = customCategory,
                            onAmountChange = { expenseAmount = it },
                            onCategoryChange = { selectedCategory = it },
                            onCustomCategoryChange = { customCategory = it },
                            onSave = { saveExpense() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.padding(16.dp)) {
                            Column {
                                var expandedDate by remember { mutableStateOf(false) }
                                TextButton(onClick = { expandedDate = !expandedDate }) {
                                    Text("Date: $selectedDateFilter")
                                }
                                DropdownMenu(
                                    expanded = expandedDate,
                                    onDismissRequest = { expandedDate = false }
                                ) {
                                    listOf(
                                        "All",
                                        "Last 1 Day",
                                        "Last 7 Days",
                                        "Last 15 Days"
                                    ).forEach { filter ->
                                        DropdownMenuItem(
                                            text = { Text(filter) },
                                            onClick = {
                                                selectedDateFilter = filter
                                                expandedDate = false
                                                applyFilters()
                                            },
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                            }
                            Column {
                                var expandedCategory by remember { mutableStateOf(false) }
                                TextButton(onClick = { expandedCategory = !expandedCategory }) {
                                    Text("Category: $selectedCategoryFilter")
                                }
                                DropdownMenu(
                                    expanded = expandedCategory,
                                    onDismissRequest = { expandedCategory = false }
                                ) {
                                    listOf(
                                        "All",
                                        "Food",
                                        "Entertainment",
                                        "Transportation",
                                        "Shopping",
                                        "Other",
                                    ).forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category) },
                                            onClick = {
                                                selectedCategoryFilter = category
                                                expandedCategory = false
                                                applyFilters()
                                            }
                                        )
                                    }
                                }
                            }

                        }

                        LazyColumn {
                            items(expenses) { expense ->
                                ExpenseItem(
                                    expense = expense,
                                    onDelete = { expenses = expenses - expense },
                                    onEdit = {
                                        expenseAmount = expense.amount.toString()
                                        selectedCategory = expense.title
                                        customCategory =
                                            if (expense.title == "Other") expense.title else ""
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        Button(
                            onClick = {
                                val intent = Intent(context, LocationActivity::class.java)
                                intent.putExtra("EXTRA_LOCATION", currentLocation)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Location Detail")
                        }
                    }

                    2 -> {
                        SettingsTab(isDarkMode = isDarkMode, onDarkModeToggle = { isDarkMode = it })
                    }
                }
            }
        }
    }
}

private fun loadExpenses(sharedPreferences: SharedPreferences): List<Expense> {
    val json = sharedPreferences.getString("expenses", null) ?: return emptyList()
    val type = object : TypeToken<List<Expense>>() {}.type
    return Gson().fromJson(json, type)
}

private fun saveExpenses(sharedPreferences: SharedPreferences, expenses: List<Expense>) {
    val editor = sharedPreferences.edit()
    val json = Gson().toJson(expenses)
    editor.putString("expenses", json)
    editor.apply()
}
