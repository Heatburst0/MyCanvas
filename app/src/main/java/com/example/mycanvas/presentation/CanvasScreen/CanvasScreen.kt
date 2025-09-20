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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycanvas.data.CanvasText
import com.example.mycanvas.presentation.components.DraggableText


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CanvasEditorScreen() {
    // Undo / redo stacks of snapshots
    val undoStack = remember { mutableStateListOf<List<CanvasText>>() }
    val redoStack = remember { mutableStateListOf<List<CanvasText>>() }

    // Single source of truth for items on canvas
    var texts by remember { mutableStateOf(listOf<CanvasText>()) }

    fun pushToUndo() {
        // save deep copy snapshot
        undoStack.add(texts.map { it.copy() })
        // clear redo after new action
        redoStack.clear()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            // Draw all texts from the source-of-truth list
            texts.forEach { item ->
                DraggableText(
                    textItem = item,
                    onDragStart = { pushToUndo() }, // save snapshot before drag
                    onUpdate = { updated ->
                        texts = texts.map { if (it.id == updated.id) updated else it }
                    }
                )
            }

            if (texts.isEmpty()) {
                Text(text = "Canvas", fontSize = 32.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                pushToUndo()
                // add at some default position (you can compute center if you want)
                texts = texts + CanvasText(text = "Sample Text", x = 120f, y = 120f)
            }) {
                Text("Add Text")
            }

            Button(onClick = {
                if (undoStack.isNotEmpty()) {
                    // save current state to redo, then restore last undo snapshot
                    redoStack.add(texts.map { it.copy() })
                    texts = undoStack.removeLast()
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
}
