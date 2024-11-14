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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
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
                .padding(horizontal = 16.dp)
                .padding(top = 49.dp, bottom = 23.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.patterns),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 52.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(59.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Usuario", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFECF4F5), CircleShape),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.user_icon),
                        contentDescription = "User Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 4.dp) // Ajusta este valor según sea necesario
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(43.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        "Correo electrónico",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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

            Spacer(modifier = Modifier.height(43.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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

            Spacer(modifier = Modifier.height(56.dp))

            Button(
                onClick = { /* Handle registration */ },
                modifier = Modifier
                    .width(169.dp)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF3C78FE))
            ) {
                Text(
                    "Registrarse",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(41.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(29.dp))

            Button(
                onClick = { navController.navigate("login") }, // Navegar a LoginScreen
                modifier = Modifier
                    .width(169.dp)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    "Iniciar Sesión",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Navega como invitado",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0x99000000),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
