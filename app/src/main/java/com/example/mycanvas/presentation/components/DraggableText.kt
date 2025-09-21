package com.example.mycanvas.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycanvas.data.CanvasText
import kotlin.math.roundToInt

@Composable
fun DraggableText(
    textItem: CanvasText,
    isSelected: Boolean,
    canvasWidth: Float,
    canvasHeight: Float,
    onDragStart: () -> Unit,
    onUpdate: (CanvasText) -> Unit,
    onSelect: (CanvasText) -> Unit
) {
    var offsetX by remember { mutableStateOf(textItem.x) }
    var offsetY by remember { mutableStateOf(textItem.y) }

    LaunchedEffect(textItem.id, textItem.x, textItem.y) {
        offsetX = textItem.x
        offsetY = textItem.y
    }

    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(textItem.text),
        style = TextStyle(fontSize = textItem.fontSize.sp, fontFamily = textItem.fontFamily)
    )

    // Use raw pixels, no unnecessary conversion
    val textWidth = textLayoutResult.size.width.toFloat()
    val textHeight = textLayoutResult.size.height.toFloat()

    val baseModifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .pointerInput(textItem.id) {
            detectDragGestures(
                onDragStart = { onDragStart() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, canvasWidth - textWidth)
                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, canvasHeight - textHeight)
                },
                onDragEnd = {
                    onUpdate(textItem.copy(x = offsetX, y = offsetY))
                }
            )
        }
        .clickable { onSelect(textItem) }

    Box(
        modifier = if (isSelected) {
            baseModifier.border(2.dp, Color.Blue, RoundedCornerShape(4.dp))
        } else baseModifier
    ) {
        Text(
            text = textItem.text,
            fontSize = textItem.fontSize.sp,
            fontFamily = textItem.fontFamily,
            fontWeight = if (textItem.isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (textItem.isItalic) FontStyle.Italic else FontStyle.Normal,
            textDecoration = if (textItem.isUnderlined) TextDecoration.Underline else TextDecoration.None,
            color = Color.Black
        )
    }
}



