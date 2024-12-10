package com.example.myapplicationdssdsdsd.ui.theme

import android.os.Build
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.GlobalVariables
import com.example.myapplicationdssdsdsd.R
import com.example.myapplicationdssdsdsd.control.ToolBox

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRUI(navController: NavHostController) {
    var linkText by remember { mutableStateOf("") }
    var currentScreen by remember { mutableStateOf("GenerateQRUI") }

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
            ),
            singleLine = true
        )

        Button(
            onClick = {
                if (linkText.isNotEmpty()) {
                    GlobalVariables.qrCode = linkText
                    navController.navigate("QrResultFragment")
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

    }
    ToolBox(navController, currentScreen) {
        screen -> currentScreen = screen
    }
}
