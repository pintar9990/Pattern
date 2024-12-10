@file:Suppress("DEPRECATION")

package com.example.myapplicationdssdsdsd.control


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Función para decodificar la cadena base64 a un Bitmap
fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
    return try {
        // Verificar si la cadena tiene el prefijo "data:image"
        val base64Image = if (base64Str.startsWith("data:image")) {
            // Extraer solo la parte después de ","
            val parts = base64Str.split(",")
            if (parts.size > 1 && parts[1].isNotEmpty()) {
                parts[1]  // Tomar solo los datos base64
            } else {
                throw IllegalArgumentException("Cadena base64 inválida: no contiene datos base64 después de la coma.")
            }
        } else {
            base64Str
        }

        // Verificar que la cadena Base64 no esté vacía antes de decodificar
        if (base64Image.isBlank()) {
            throw IllegalArgumentException("Cadena base64 vacía.")
        }

        // Decodificar la cadena base64 a un arreglo de bytes
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

        // Convertir los bytes en un Bitmap
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("DecodeError", "Error al decodificar la imagen: ${e.message}")
        null
    }
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Necesario para que funcione fuera de una actividad
    context.startActivity(intent)
}


@Composable
fun OpenUrlClickableText(linkText: String) {
    val context = LocalContext.current

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color(0x99000000), fontSize = 32.sp)) {
            append("Abrir: ")
        }
        pushStringAnnotation(tag = "URL", annotation = linkText)
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(linkText)
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        modifier = Modifier.padding(top = 44.dp),
        onClick = { offset ->
            val url = annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.item

            Log.d("QRCodeScreen", "URL a abrir: $url") // Verificar qué URL estamos recibiendo

            if (url != null) {
                // Verificar si es una URL válida (comienza con http:// o https://)
                if (isValidUrl(url)) {
                    try {
                        openUrl(context, url) // Intentar abrir la URL
                    } catch (e: Exception) {
                        Log.e("QRCodeScreen", "Error al abrir la URL: ${e.message}", e)
                    }
                } else {
                    // Si no es una URL, hacer una búsqueda en Google
                    val googleSearchUrl = "https://www.google.com/search?q=$url"
                    try {
                        openUrl(context, googleSearchUrl) // Realizar la búsqueda en Google
                    } catch (e: Exception) {
                        Log.e("QRCodeScreen", "Error al realizar la búsqueda: ${e.message}", e)
                    }
                }
            } else {
                Log.e("QRCodeScreen", "URL no encontrada o es nula.")
            }
        }
    )
}

// Función para validar si un texto es una URL válida
fun isValidUrl(url: String): Boolean {
    return url.startsWith("http://") || url.startsWith("https://")
}


