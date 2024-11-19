package com.example.myapplicationdssdsdsd.ui.theme

import android.graphics.Bitmap
import android.os.Build
import android.graphics.Color as AndroidColor
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRUI(navController: NavHostController) {
    var linkText by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val auth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance("https://patterns-3f17e-default-rtdb.europe-west1.firebasedatabase.app").reference

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7BE2F4),
                        Color(0xFFFDFDFD)
                    )
                )
            )
            .padding(top = 117.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.patterns),
            contentDescription = "QR Code Icon",
            modifier = Modifier.size(138.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Introduce Enlace",
            fontSize = 32.sp,
            fontFamily = FontFamily(Font(R.font.jaro_regular)),
            color = Color(0x99000000),
            modifier = Modifier.padding(top = 44.dp)
        )

        OutlinedTextField(
            value = linkText,
            onValueChange = { linkText = it },
            modifier = Modifier
                .padding(top = 33.dp)
                .width(350.dp)
                .background(Color(0xFFECF4F5), CircleShape),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Button(
            onClick = {
                if (linkText.isNotEmpty()) {
                    qrBitmap = generateQRCode(linkText)
                    qrBitmap?.let { bitmap ->
                        saveQrToFirebase(linkText, bitmap, auth, database)
                    }
                }
            },
            modifier = Modifier
                .padding(top = 68.dp)
                .width(250.dp)
                .height(80.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color.White)
        ) {
            Text(
                text = "Generar QR",
                fontFamily = FontFamily(Font(R.font.jaro_regular)),
                fontSize = 32.sp,
                color = Color(0xFF555555)
            )
        }

        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated QR Code",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(200.dp)
            )
        }
    }
    ToolBox(navController)
}

fun generateQRCode(text: String): Bitmap {
    val size = 512 // tamaño del código QR
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
        }
    }
    return bitmap
}

@RequiresApi(Build.VERSION_CODES.O)
fun saveQrToFirebase(link: String, bitmap: Bitmap, auth: FirebaseAuth, database: DatabaseReference) {
    val user = auth.currentUser
    user?.uid?.let { uid ->
        val qrId = UUID.randomUUID().toString()
        val qrRef = database.child("users").child(uid).child("qrs").child(qrId)

        // Convertir el bitmap a un string base64
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val qrImage = Base64.getEncoder().encodeToString(baos.toByteArray())

        val qrData = mapOf(
            "imageUrl" to "data:image/png;base64,$qrImage",
            "link" to link
        )

        qrRef.setValue(qrData)
            .addOnSuccessListener {
                Log.d("Firebase", "QR guardado exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al guardar el QR", e)
            }
    }
}
