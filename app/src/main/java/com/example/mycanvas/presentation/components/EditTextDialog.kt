package com.example.mycanvas.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mycanvas.data.CanvasText

@Composable
fun EditTextDialog(
    textItem: CanvasText,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedValue by remember(textItem.id) { mutableStateOf(textItem.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Text") },
        text = {
            TextField(
                value = editedValue,
                onValueChange = { editedValue = it }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(editedValue) }) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}