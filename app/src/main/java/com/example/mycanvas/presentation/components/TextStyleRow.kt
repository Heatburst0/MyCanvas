package com.example.mycanvas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.mycanvas.R
import com.example.mycanvas.data.CanvasText

@Composable
fun TextStyleRow(
    isTextSelected: Boolean,
    currentTextId: String?,
    texts: List<CanvasText>,
    onUpdateText: (CanvasText) -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = onEditClick,
            enabled = isTextSelected
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }

        IconButton(
            onClick = {
                currentTextId?.let { id ->
                    texts.find { it.id == id }?.let { text ->
                        onUpdateText(text.copy(isBold = !text.isBold))
                    }
                }
            },
            enabled = isTextSelected
        ) {
            Icon(painter = painterResource(R.drawable.ic_bold), contentDescription = "Bold")
        }

        IconButton(
            onClick = {
                currentTextId?.let { id ->
                    texts.find { it.id == id }?.let { text ->
                        onUpdateText(text.copy(isItalic = !text.isItalic))
                    }
                }
            },
            enabled = isTextSelected
        ) {
            Icon(painter = painterResource(R.drawable.ic_italic), contentDescription = "Italic")
        }

        IconButton(
            onClick = {
                currentTextId?.let { id ->
                    texts.find { it.id == id }?.let { text ->
                        onUpdateText(text.copy(isUnderlined = !text.isUnderlined))
                    }
                }
            },
            enabled = isTextSelected
        ) {
            Icon(painter = painterResource(R.drawable.ic_underlined), contentDescription = "Underline")
        }
    }
}