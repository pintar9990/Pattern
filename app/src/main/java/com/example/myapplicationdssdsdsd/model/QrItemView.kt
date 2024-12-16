package com.example.myapplicationdssdsdsd.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationdssdsdsd.control.decodeBase64ToBitmap
import com.example.myapplicationdssdsdsd.control.isValidUrl
import com.example.myapplicationdssdsdsd.control.openUrl


class QrItemView(
    private val item: QrItemData,
    private val isDeleteMode: Boolean,
    private val isSelected: Boolean,
    private val onSelect: (Boolean) -> Unit,
    private val onTapItem: (QrItemData) -> Unit,
    private val onRemoveFromFolder: (() -> Unit)? = null
) {
    @Composable
    fun Render() {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Código QR Escaneado") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val bitmap = remember { decodeBase64ToBitmap(item.imageUrl) }
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ClickableText(
                            text = AnnotatedString(item.link),
                            onClick = {
                                val url = item.link
                                if (isValidUrl(url)) {
                                    openUrl(context, url)
                                } else {
                                    val googleSearchUrl = "https://www.google.com/search?q=$url"
                                    openUrl(context, googleSearchUrl)
                                }
                            },
                            style = LocalTextStyle.current.copy(
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Default,
                                color = Color(0x99000000),
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )

        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) Color.LightGray else Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (!isDeleteMode) {
                                showDialog = true
                            } else {
                                onTapItem(item)
                            }
                        }
                    )
                }
                .padding(vertical = 10.dp)
        ) {
            val bitmap = remember { decodeBase64ToBitmap(item.imageUrl) }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = item.link,
                fontSize = 20.sp,
                fontFamily = FontFamily.Default,
                color = Color(0x99000000)
            )
            onRemoveFromFolder?.let {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = it) {
                    Icon(Icons.Default.RemoveCircle, contentDescription = "Sacar de la carpeta")
                }
            }
            if (isDeleteMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelect
                )
            }
        }
    }
}
