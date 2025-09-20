package com.example.mycanvas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycanvas.data.CanvasText
import kotlin.math.roundToInt

@Composable
fun DraggableText(
    textItem: CanvasText,
    onDragStart: () -> Unit,
    onUpdate: (CanvasText) -> Unit,
    onRequestEdit: (CanvasText) -> Unit,
    onDone: (CanvasText) -> Unit
) {
    var offsetX by remember { mutableStateOf(textItem.x) }
    var offsetY by remember { mutableStateOf(textItem.y) }

    LaunchedEffect(textItem.id, textItem.x, textItem.y) {
        offsetX = textItem.x
        offsetY = textItem.y
    }

    val baseModifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .pointerInput(textItem.id) {
            detectDragGestures(
                onDragStart = { onDragStart() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                },
                onDragEnd = {
                    onUpdate(textItem.copy(x = offsetX, y = offsetY))
                }
            )
        }

    if (textItem.isEditing) {
        Row(
            modifier = baseModifier
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = textItem.text,
                onValueChange = { newText ->
                    onUpdate(textItem.copy(text = newText))
                },
                textStyle = TextStyle(
                    fontSize = textItem.fontSize.sp,
                    fontFamily = textItem.fontFamily
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onDone(textItem) }) {
                Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.Green)
            }
        }
    } else {
        Text(
            text = textItem.text,
            fontSize = textItem.fontSize.sp,
            fontFamily = textItem.fontFamily,
            modifier = baseModifier.clickable {
                onRequestEdit(textItem.copy(isEditing = true))
            }
        )
    }
}
