package com.example.mycanvas.presentation.CanvasScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    // push a snapshot of CURRENT state *before* making a change
    fun pushUndoSnapshot() {
        undoStack.add(texts.map { it.copy() })
        redoStack.clear()
    }
    val availableFonts = listOf(
        "Default" to FontFamily.Default,
        "Serif" to FontFamily.Serif,
        "Sans" to FontFamily.SansSerif,
        "Monospace" to FontFamily.Monospace
    )

    fun undo() {
        if (undoStack.isNotEmpty()) {
            // save current to redo, then restore last undo snapshot
            redoStack.add(texts.map { it.copy() })
            texts = undoStack.removeLast()
            // clear selection if no longer valid
            if (texts.none { it.id == selectedTextId }) selectedTextId = null
            editingText = null
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(texts.map { it.copy() })
            texts = redoStack.removeLast()
            if (texts.none { it.id == selectedTextId }) selectedTextId = null
            editingText = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Canvas area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // iterate the current list directly (each item already latest)
            texts.forEach { item ->
                DraggableText(
                    textItem = item,
                    isSelected = item.id == selectedTextId,
                    onDragStart = {
                        // snapshot BEFORE drag so undo restores pre-drag state only
                        pushUndoSnapshot()
                    },
                    onUpdate = { updated ->
                        // update only position so we never clobber edited text content
                        texts = texts.map {
                            if (it.id == updated.id) it.copy(x = updated.x, y = updated.y) else it
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

        // Action toolbar
        val isTextSelected = selectedTextId != null && texts.any { it.id == selectedTextId }
        val currentText = texts.find { it.id == selectedTextId }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -------- First row: Fonts + Font size --------
            val currentText = texts.find { it.id == selectedTextId }
            val currentFont = currentText?.fontFamily ?: FontFamily.Default
            var fontMenuExpanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp), // ✅ consistent height
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Font dropdown
                Box {
                    Button(
                        onClick = { fontMenuExpanded = !fontMenuExpanded },
                        enabled = isTextSelected,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Fonts")
                        Icon(
                            imageVector = if (fontMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = fontMenuExpanded,
                        onDismissRequest = { fontMenuExpanded = false }
                    ) {
                        availableFonts.forEach { (name, font) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(name, fontFamily = font)
                                        if (font == currentFont) {
                                            Spacer(Modifier.width(8.dp))
                                            Text("✔", color = Color.Green)
                                        }
                                    }
                                },
                                onClick = {
                                    pushUndoSnapshot()
                                    texts = texts.map {
                                        if (it.id == currentText?.id) it.copy(fontFamily = font) else it
                                    }
                                    fontMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Font size controller
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isTextSelected) Color.LightGray else Color(0xFFE0E0E0), // ✅ dim when disabled
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        // Decrease
                        TextButton(
                            onClick = {
                                currentText?.let {
                                    if (it.fontSize > 8) {
                                        pushUndoSnapshot()
                                        texts = texts.map { t ->
                                            if (t.id == it.id) t.copy(fontSize = t.fontSize - 2) else t
                                        }
                                    }
                                }
                            },
                            enabled = isTextSelected,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-", fontSize = 18.sp, textAlign = TextAlign.Center)
                        }

                        Spacer(Modifier.width(8.dp))

                        // Display current size
                        Text(
                            "${currentText?.fontSize ?: 24}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isTextSelected) Color.Black else Color.Gray
                        )

                        Spacer(Modifier.width(8.dp))

                        // Increase
                        TextButton(
                            onClick = {
                                currentText?.let {
                                    pushUndoSnapshot()
                                    texts = texts.map { t ->
                                        if (t.id == it.id) t.copy(fontSize = t.fontSize + 2) else t
                                    }
                                }
                            },
                            enabled = isTextSelected,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("+", fontSize = 18.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // -------- Second row: Edit / Bold / Italic / Underline --------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { editingText = currentText },
                    enabled = isTextSelected
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }

                IconButton(
                    onClick = {
                        pushUndoSnapshot()
                        texts = texts.map {
                            if (it.id == selectedTextId) it.copy(isBold = !it.isBold) else it
                        }
                    },
                    enabled = isTextSelected
                ) {
                    Icon(painter = painterResource(R.drawable.ic_bold), contentDescription = "Bold")
                }

                IconButton(
                    onClick = {
                        pushUndoSnapshot()
                        texts = texts.map {
                            if (it.id == selectedTextId) it.copy(isItalic = !it.isItalic) else it
                        }
                    },
                    enabled = isTextSelected
                ) {
                    Icon(painter = painterResource(R.drawable.ic_italic), contentDescription = "Italic")
                }

                IconButton(
                    onClick = {
                        pushUndoSnapshot()
                        texts = texts.map {
                            if (it.id == selectedTextId) it.copy(isUnderlined = !it.isUnderlined) else it
                        }
                    },
                    enabled = isTextSelected
                ) {
                    Icon(painter = painterResource(R.drawable.ic_underlined), contentDescription = "Underline")
                }
            }
        }



        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                pushUndoSnapshot() // snapshot before adding
                val newItem = CanvasText(
                    // create with your own defaults - keep id unique
                    text = "Sample Text",
                    x = 0f,
                    y = 0f
                )
                texts = texts + newItem
//                selectedTextId = newItem.id
            }) {
                Text("Add Text")
            }

            Button(onClick = { undo() }, enabled = undoStack.isNotEmpty()) {
                Text("←")
            }

            Button(onClick = { redo() }, enabled = redoStack.isNotEmpty()) {
                Text("→")
            }
        }
    }

    // Edit dialog
    editingText?.let { text ->
        // remember keyed by text id so new edit sessions re-initialize editedValue
        var editedValue by remember(text.id) { mutableStateOf(text.text) }

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
                    // IMPORTANT: snapshot BEFORE applying the edit so undo reverts only the edit
                    pushUndoSnapshot()
                    texts = texts.map {
                        if (it.id == text.id) it.copy(text = editedValue) else it
                    }
                    // keep selection on edited item
                    selectedTextId = text.id
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
