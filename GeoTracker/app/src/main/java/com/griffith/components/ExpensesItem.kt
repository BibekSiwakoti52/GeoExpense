package com.griffith.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griffith.Expense
import java.text.SimpleDateFormat

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = expense.title, fontSize = 16.sp)
            Text(
                text = "€${expense.amount}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Date: ${
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(expense.date)
                }",
                fontSize = 12.sp
            )
            Text(text = "Location: ${expense.location}", fontSize = 12.sp)
        }
        Row {
            TextButton(onClick = onEdit) {
                Text("Edit", color = MaterialTheme.colorScheme.primary)
            }
            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
