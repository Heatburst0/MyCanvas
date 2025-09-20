package com.example.mycanvas.presentation.CanvasScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycanvas.R
import com.example.mycanvas.data.CanvasText
import com.example.mycanvas.presentation.components.DraggableText


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CanvasEditorScreen() {
    val undoStack = remember { mutableStateListOf<List<CanvasText>>() }
    val redoStack = remember { mutableStateListOf<List<CanvasText>>() }

    var texts by remember { mutableStateOf(listOf<CanvasText>()) }
    var selectedTextId by remember { mutableStateOf<String?>(null) }
    var editingText by remember { mutableStateOf<CanvasText?>(null) }

    fun pushToUndo() {
        undoStack.add(texts.map { it.copy() })
        redoStack.clear()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            texts.forEach { item ->
                val currentText = texts.find { it.id == item.id }!! // always latest state
                DraggableText(
                    textItem = currentText,
                    isSelected = currentText.id == selectedTextId,
                    onDragStart = { pushToUndo() },
                    onUpdate = { updated ->
                        // preserve edited text and update only position
                        texts = texts.map {
                            if (it.id == updated.id)
                                it.copy(x = updated.x, y = updated.y, text = it.text)
                            else it
                        }
                    },
                    onSelect = { selected -> selectedTextId = selected.id }
                )
            }

            if (texts.isEmpty()) {
                Text(text = "Canvas", fontSize = 32.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Text action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val isTextSelected = selectedTextId != null
            IconButton(onClick = {
                editingText = texts.find { it.id == selectedTextId }
            }, enabled = isTextSelected) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { /* Bold logic */ }, enabled = isTextSelected) {
                Icon(painter = painterResource(R.drawable.ic_bold), contentDescription = "Bold")
            }
            IconButton(onClick = { /* Italic logic */ }, enabled = isTextSelected) {
                Icon(painter = painterResource(R.drawable.ic_italic), contentDescription = "Italic")
            }
            IconButton(onClick = { /* Underline logic */ }, enabled = isTextSelected) {
                Icon(painter = painterResource(R.drawable.ic_underlined), contentDescription = "Underline")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add / Undo / Redo row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                pushToUndo()
                texts = texts + CanvasText(text = "Sample Text", x = 120f, y = 120f)
            }) {
                Text("Add Text")
            }

            Button(onClick = {
                if (undoStack.isNotEmpty()) {
                    redoStack.add(texts.map { it.copy() })
                    texts = undoStack.removeLast()
                    if (texts.none { it.id == selectedTextId }) {
                        selectedTextId = null
                    }
                }
            }, enabled = undoStack.isNotEmpty()) {
                Text("←")
            }

            Button(onClick = {
                if (redoStack.isNotEmpty()) {
                    undoStack.add(texts.map { it.copy() })
                    texts = redoStack.removeLast()
                }
            }, enabled = redoStack.isNotEmpty()) {
                Text("→")
            }
        }
    }

    // Edit dialog
    editingText?.let { text ->
        var editedValue by remember { mutableStateOf(text.text) }
        AlertDialog(
            onDismissRequest = { editingText = null },
            title = { Text("Edit Text") },
            text = {
                TextField(
                    value = editedValue,
                    onValueChange = { editedValue = it }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    texts = texts.map {
                        if (it.id == text.id) it.copy(text = editedValue) else it
                    }
                    pushToUndo()
                    editingText = null
                }) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingText = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
