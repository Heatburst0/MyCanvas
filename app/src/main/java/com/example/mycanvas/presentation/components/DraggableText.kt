package com.example.mycanvas.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.example.mycanvas.data.CanvasText
import kotlin.math.roundToInt

@Composable
fun DraggableText(
    textItem: CanvasText,
    onDragStart: () -> Unit,
    onUpdate: (CanvasText) -> Unit
) {
    // Always derive position from the textItem (single source of truth),
    // but inside pointerInput accumulate deltas into local vars so each delta is applied cumulatively.
    Text(
        text = textItem.text,
        fontSize = textItem.fontSize.sp,
        fontFamily = textItem.fontFamily,
        modifier = Modifier
            .offset { IntOffset(textItem.x.roundToInt(), textItem.y.roundToInt()) }
            .pointerInput(textItem.id) {
                // pointerInput block is re-created if textItem.id changes (safe)
                var startX = 0f
                var startY = 0f

                detectDragGestures(
                    onDragStart = {
                        // initialize starting coords at drag start (use current textItem's coords)
                        startX = textItem.x
                        startY = textItem.y
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // accumulate deltas
                        startX += dragAmount.x
                        startY += dragAmount.y
                        // update the shared model
                        onUpdate(textItem.copy(x = startX, y = startY))
                    },
                    onDragEnd = {
                        // optional: finalization if needed
                    }
                )
            }
    )
}