package com.example.myapplicationdssdsdsd.ui.theme

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationdssdsdsd.R

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7BE2F4), Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Patterns",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF676767)
                ),
                modifier = Modifier.padding(top = 38.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 75.dp)
                    .background(Color(0xFFECF4F5), CircleShape),
                shape = CircleShape
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 33.dp)
                    .background(Color(0xFFECF4F5), CircleShape),
                visualTransformation = PasswordVisualTransformation(),
                shape = CircleShape
            )

            Button(
                onClick = { /* Handle login */ },
                modifier = Modifier
                    .padding(top = 68.dp)
                    .width(169.dp)
                    .height(56.dp)
                    .shadow(4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                )
            }

            Divider(
                color = Color(0xFF555555),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 41.dp)
            )

            Button(
                onClick = { /* Handle registration */ },
                modifier = Modifier
                    .padding(top = 29.dp)
                    .width(169.dp)
                    .height(56.dp)
                    .shadow(4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF3C78FE))
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }

            Text(
                text = "Navega como invitado",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    color = Color(0x99000000)
                ),
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}
