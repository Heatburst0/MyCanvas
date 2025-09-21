package com.example.mycanvas.data

import androidx.compose.ui.text.font.FontFamily
import java.util.UUID

data class CanvasText(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val x: Float,
    val y: Float,
    val fontSize: Int = 24,
    val fontFamily: FontFamily = FontFamily.Default,
    val isEditing: Boolean = false,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderlined: Boolean = false
)

