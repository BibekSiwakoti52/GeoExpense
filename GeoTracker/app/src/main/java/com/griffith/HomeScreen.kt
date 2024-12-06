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

    var expenses by remember {
        mutableStateOf(loadExpenses(sharedPreferences))
    }

    var expenseAmount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Other") }
    var customCategory by remember { mutableStateOf("") }
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
                        Location(location = currentLocation)
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