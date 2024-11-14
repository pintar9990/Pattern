package com.example.myapplicationdssdsdsd.ui.theme

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
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
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF676767),
                fontFamily = FontFamily(Font(R.font.lalezar_regular)), // Usa la fuente Lalezar
                modifier = Modifier.padding(top = 38.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.patterns),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 52.dp),
                contentScale = ContentScale.Fit
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 75.dp)
                    .background(Color(0xFFECF4F5), CircleShape),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.email_icon),
                        contentDescription = "Email Icon",
                        modifier = Modifier
                            .size(27.dp)
                            .padding(top = 4.dp) // Ajusta este valor según sea necesario
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 33.dp)
                    .background(Color(0xFFECF4F5), CircleShape),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password_icon),
                        contentDescription = "Password Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(top = 4.dp) // Ajusta este valor según sea necesario
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Button(
                onClick = { navController.navigate("registration") }, // Navegar a RegistrationScreen
                modifier = Modifier
                    .padding(top = 68.dp)
                    .width(169.dp)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontSize = 20.sp)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 41.dp),
                thickness = 1.dp,
                color = Color(0xFF555555)
            )

            Button(
                onClick = { navController.navigate("registration") }, // Navegar a RegistrationScreen
                modifier = Modifier
                    .padding(top = 29.dp)
                    .width(169.dp)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF3C78FE))
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontSize = 20.sp)
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
