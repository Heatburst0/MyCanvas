package com.example.mycanvas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycanvas.R
import com.example.mycanvas.data.CanvasText


@Composable
fun FontToolbar(
    isTextSelected: Boolean,
    currentText: CanvasText?,
    availableFonts: List<Pair<String, FontFamily>>,
    pushUndoSnapshot: () -> Unit,
    onUpdateText: (CanvasText) -> Unit
) {
    var fontMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
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
                val currentFont = currentText?.fontFamily ?: FontFamily.Default
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
                            currentText?.let { text ->
                                pushUndoSnapshot()
                                onUpdateText(text.copy(fontFamily = font))
                            }
                            fontMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Font size controller
        Surface(
            shape = RoundedCornerShape(25),
            color = if (isTextSelected) Color.LightGray else Color(0xFFE0E0E0),
            modifier = Modifier.height(40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(5.dp)
            ) {
                Button(
                    onClick = {
                        currentText?.let {
                            if (it.fontSize > 8) {
                                pushUndoSnapshot()
                                onUpdateText(it.copy(fontSize = it.fontSize - 2))
                            }
                        }
                    },
                    enabled = isTextSelected,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_remove),
                        contentDescription = "Decrease Font",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    "${currentText?.fontSize ?: 24}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isTextSelected) Color.Black else Color.Gray
                )

                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        currentText?.let {
                            pushUndoSnapshot()
                            onUpdateText(it.copy(fontSize = it.fontSize + 2))
                        }
                    },
                    enabled = isTextSelected,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Increase Font",
                        tint = Color.White
                    )
                }
            }
        }
    }
}