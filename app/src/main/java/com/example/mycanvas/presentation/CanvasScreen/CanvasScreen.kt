package com.example.mycanvas.presentation.CanvasScreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycanvas.R
import com.example.mycanvas.data.CanvasText
import com.example.mycanvas.presentation.components.DraggableText
import com.example.mycanvas.presentation.components.EditTextDialog
import com.example.mycanvas.presentation.components.FontToolbar
import com.example.mycanvas.presentation.components.TextStyleRow


@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CanvasEditorScreen() {

    val undoStack = remember { mutableStateListOf<List<CanvasText>>() }
    val redoStack = remember { mutableStateListOf<List<CanvasText>>() }

    var texts by remember { mutableStateOf(listOf<CanvasText>()) }
    var selectedTextId by remember { mutableStateOf<String?>(null) }
    var editingText by remember { mutableStateOf<CanvasText?>(null) }
    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }
    val currentDensity = LocalDensity.current

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
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.TopStart // Use TopStart so 0,0 is top-left
        ) {
            canvasWidth = constraints.maxWidth.toFloat()
            canvasHeight = constraints.maxHeight.toFloat()

            texts.forEach { item ->
                key(item.id) {
                    DraggableText(
                        textItem = item,
                        isSelected = item.id == selectedTextId,
                        canvasWidth = canvasWidth,
                        canvasHeight = canvasHeight,
                        onDragStart = { pushUndoSnapshot() },
                        onUpdate = { updated ->
                            texts = texts.map {
                                if (it.id == updated.id) it.copy(x = updated.x, y = updated.y) else it
                            }
                        },
                        onSelect = { selected -> selectedTextId = selected.id }
                    )
                }

            }

            if (texts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Canvas", fontSize = 32.sp, color = Color.LightGray)
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        // Action toolbar
        val isTextSelected = selectedTextId != null && texts.any { it.id == selectedTextId }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -------- First row: Fonts + Font size --------
            val currentText = texts.find { it.id == selectedTextId }

            FontToolbar(
                isTextSelected = isTextSelected,
                currentText = currentText,
                availableFonts = availableFonts,
                pushUndoSnapshot = { pushUndoSnapshot() },
                onUpdateText = { updatedText ->
                    texts = texts.map { if (it.id == updatedText.id) updatedText else it }
                }
            )

            Spacer(Modifier.height(8.dp))

            // -------- Second row: Edit / Bold / Italic / Underline --------
            TextStyleRow(
                isTextSelected = isTextSelected,
                currentTextId = selectedTextId,
                texts = texts,
                onUpdateText = { updatedText ->
                    pushUndoSnapshot()
                    texts = texts.map { if (it.id == updatedText.id) updatedText else it }
                },
                onEditClick = { editingText = currentText }
            )

        }



        Spacer(modifier = Modifier.height(8.dp))

        // Adding texts buttons + Undo and Redo buttons

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val defaultFontSize = 24.sp
            val textApproxWidth = 100f
            val textApproxHeight by remember { derivedStateOf { currentDensity.run { defaultFontSize.toPx() } } }
            Button(onClick = {
                pushUndoSnapshot() // snapshot before adding

                val newItem = CanvasText(
                    text = "Sample Text",
                    fontSize = 24,
                    x = canvasWidth / 2 - textApproxWidth / 2,
                    y = canvasHeight / 2 - textApproxHeight / 2
                )

                texts = texts + newItem
            }) {
                Text("Add Text")
            }
            Button(
                onClick = { undo() },
                enabled = undoStack.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_undo),
                    contentDescription = "Undo")
            }

            // Redo Button
            Button(
                onClick = { redo() },
                enabled = redoStack.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_redo),
                    contentDescription = "Redo")
            }
        }
    }

    // Edit dialog
    editingText?.let { text ->
        EditTextDialog(
            textItem = text,
            onDismiss = { editingText = null },
            onConfirm = { newText ->
                pushUndoSnapshot()
                texts = texts.map {
                    if (it.id == text.id) it.copy(text = newText) else it
                }
                selectedTextId = text.id
                editingText = null
            }
        )
    }

}
