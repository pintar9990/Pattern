package com.example.myapplicationdssdsdsd.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QRCodeScreen(
    qrCodeUrl: String,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit
) {
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
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .background(Color(0xFF555555))
            )

            Text(
                text = qrCodeUrl,
                fontSize = 32.sp,
                fontFamily = FontFamily.Default,
                color = Color(0xFF555555),
                modifier = Modifier.padding(top = 57.dp)
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .padding(top = 57.dp)
                    .width(232.dp)
                    .height(72.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = "Guardar",
                    fontSize = 32.sp,
                    color = Color(0xFF555555)
                )
            }

            Button(
                onClick = onShareClick,
                modifier = Modifier
                    .padding(top = 43.dp)
                    .width(232.dp)
                    .height(66.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = "Compartir",
                    fontSize = 32.sp,
                    color = Color(0xFF555555)
                )
            }

            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Footer image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 43.dp),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}