package com.example.myapplicationdssdsdsd

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

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



// Data class to represent QR data from Firebase
data class QrItemData(
    val imageUrl: String = "",
    val link: String = ""
)