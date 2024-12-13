package com.example.myapplicationdssdsdsd

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.UUID


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QRCodeScreen(
    navController: NavController,
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isSaved by remember { mutableStateOf(false) }
    val linkText = GlobalVariables.qrCode
    val auth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance("https://patterns-3f17e-default-rtdb.europe-west1.firebasedatabase.app").reference
    var buttonText by remember { mutableStateOf("Guardar QR") }

    var currentScreen by remember { mutableStateOf("QrResultFragment") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7BE2F4),
                        Color(0xFFFDFDFD)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 93.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            qrBitmap = generateQRCode(linkText)
            qrBitmap?.let { bitmap ->

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Generated QR Code",
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(200.dp)
                )

                // Texto clickeable
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

                OpenUrlClickableText(linkText)


                Button(
                    onClick = {
                        if (linkText.isNotEmpty()) {
                            if (!isSaved) {
                                saveQrToFirebase(linkText, bitmap, auth, database) {
                                    buttonText = "QR Guardado"
                                    isSaved = true
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .width(250.dp)
                        .height(80.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF555555),
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.DarkGray
                    ),
                    enabled = !isSaved
                ) {
                    Text(
                        text = buttonText,
                        fontFamily = FontFamily(Font(R.font.jaro_regular)),
                        fontSize = 32.sp
                    )
                }
            }
            ToolBox(navController, currentScreen) { screen ->
                currentScreen = screen
            }
        }
    }
}


fun generateQRCode(text: String): Bitmap {
    val size = 512 // tamaño del código QR
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

@RequiresApi(Build.VERSION_CODES.O)
fun saveQrToFirebase(link: String, bitmap: Bitmap, auth: FirebaseAuth, database: DatabaseReference, onSuccess: () -> Unit) {
    val user = auth.currentUser
    user?.uid?.let { uid ->
        val qrId = UUID.randomUUID().toString()
        val qrRef = database.child("users").child(uid).child("qrs").child(qrId)

        // Convertir el bitmap a un string base64
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val qrImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

        val qrData = mapOf(
            "imageUrl" to "data:image/png;base64,$qrImage",
            "link" to link
        )

        qrRef.setValue(qrData)
            .addOnSuccessListener {
                Log.d("Firebase", "QR guardado exitosamente")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al guardar el QR", e)
            }
    }
}