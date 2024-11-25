package com.griffith

import java.util.Date

data class Expense(
    val title: String,
    var amount: Double,
    val location: String,
    val date: Date
)
