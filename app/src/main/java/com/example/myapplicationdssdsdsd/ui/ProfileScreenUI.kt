package com.example.myapplicationdssdsdsd.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationdssdsdsd.R
import com.example.myapplicationdssdsdsd.R.font
import com.example.myapplicationdssdsdsd.components.ToolBox
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://patterns-3f17e-default-rtdb.europe-west1.firebasedatabase.app").reference

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }  // Cambiar contrasena estará vacío por defecto
    var newPassword by remember { mutableStateOf("") }  // Nueva contraseña
    var isSaving by remember { mutableStateOf(false) } // Indicador de carga
    var isLocked by remember { mutableStateOf(true) } // Nuevo estado para controlar el candado
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var changeSuccess by remember { mutableStateOf(false) } // Valor al cambiar el perfil
    var currentScreen by remember { mutableStateOf("ProfileScreenUI") }

    // Carga inicial de datos
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            database.child("users").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Modificado para solo obtener "email" y "username", ignorando "qrs"
                        val userData =
                            snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})

                        // Obtener solo los campos necesarios y manejar casos de error
                        username = userData?.get("username") as? String ?: "Username no disponible"
                        email =
                            userData?.get("email") as? String ?: "Correo electrónico no disponible"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Error al cargar datos: ${error.message}")
                    }
                })
        }
    }

    Box(
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
    ) {
        Column(
            modifier = Modifier
                .padding(top = 11.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { isLocked = !isLocked }) { // Cambiar estado del candado
                    Icon(
                        painter = if (isLocked) painterResource(id = R.drawable.lock_closed_icon)
                        else painterResource(id = R.drawable.lock_open_icon),
                        contentDescription = "Candado",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }


            Text(
                text = "Perfil",
                fontSize = 48.sp,
                fontFamily = FontFamily(Font(font.jaro_regular)),
                color = Color(0xCC000000),
                modifier = Modifier.offset(x = -20.dp) // Mueve el texto ligeramente a la derecha
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 26.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(40.dp) // Más espacio entre las imágenes
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_s_icon),
                        contentDescription = "Username Icon",
                        modifier = Modifier.size(58.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.email_icon),
                        contentDescription = "Email Icon",
                        modifier = Modifier
                            .size(56.dp)
                            .offset(y = 20.dp) // Ajusta el valor de y para bajar la imagen
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(40.dp) // Más espacio entre los textfields
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLocked, // Deshabilitar si el candado está cerrado
                        label = {
                            Text(
                                text = "Nombre de usuario",
                                style = TextStyle(
                                    fontSize = 20.sp, // Cambia el tamaño de la letra aquí
                                    fontFamily = FontFamily(Font(R.font.jaro_regular)),
                                    color = Color.Gray // Cambia el color si es necesario
                                )
                            )
                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            fontSize = 30.sp, // Cambia el tamaño de la letra aquí
                            fontFamily = FontFamily(Font(R.font.jaro_regular)),
                            color = Color.Black
                        ),
                        maxLines = 1
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLocked, // Deshabilitar si el candado está cerrado
                        label = {
                            Text(
                                text = "Correo electrónico",
                                style = TextStyle(
                                    fontSize = 20.sp, // Cambia el tamaño de la letra aquí
                                    fontFamily = FontFamily(Font(R.font.jaro_regular)),
                                    color = Color.Gray // Cambia el color si es necesario
                                )
                            )
                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            fontSize = 20.sp, // Cambia el tamaño de la letra aquí
                            fontFamily = FontFamily(Font(R.font.jaro_regular)),
                            color = Color.Black
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .fillMaxWidth(),
                enabled = !isLocked, // Deshabilitar si el candado está cerrado
                label = {
                    Text(
                        text = "Contraseña actual",
                        style = TextStyle(
                            fontSize = 20.sp, // Cambia el tamaño de la letra aquí
                            fontFamily = FontFamily(Font(R.font.jaro_regular)),
                            color = Color.Gray // Cambia el color si es necesario
                        )
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 30.sp, // Cambia el tamaño de la letra aquí
                    fontFamily = FontFamily(Font(R.font.jaro_regular)),
                    color = Color.Black
                ),
                maxLines = 1
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .fillMaxWidth(),
                enabled = !isLocked, // Deshabilitar si el candado está cerrado
                label = {
                    Text(
                        text = "Nueva contraseña",
                        style = TextStyle(
                            fontSize = 20.sp, // Cambia el tamaño de la letra aquí
                            fontFamily = FontFamily(Font(R.font.jaro_regular)),
                            color = Color.Gray // Cambia el color si es necesario
                        )
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 30.sp, // Cambia el tamaño de la letra aquí
                    fontFamily = FontFamily(Font(R.font.jaro_regular)),
                    color = Color.Black
                ),
                maxLines = 1
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .padding(top = 35.dp),
                thickness = 5.dp,
                color = Color(0xFF555555)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            if (changeSuccess) {
                Text(
                    text = "Se han guardado los datos correctamente.",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    changeSuccess = false
                    when {
                        username.isEmpty() -> {
                            errorMessage = "Por favor, escoja su nombre de usuario."
                            return@Button
                        }
                        email.isEmpty() -> {
                            errorMessage = "Por favor, escoja su correo electrónico."
                            return@Button
                        }
                    }

                    isSaving = true
                    user?.let { currentUser ->
                        if (password.isNotEmpty()) {
                            val credential = EmailAuthProvider.getCredential(currentUser.email!!, password)

                            // Reautenticación del usuario
                            currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                                if (reauthTask.isSuccessful) {
                                    currentUser.verifyBeforeUpdateEmail(email).addOnCompleteListener { emailTask ->
                                        if (emailTask.isSuccessful) {
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build()

                                            currentUser.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                                                if (profileTask.isSuccessful) {
                                                    val userData = mapOf("username" to username, "email" to email)
                                                    database.child("users").child(currentUser.uid).updateChildren(userData)
                                                        .addOnSuccessListener {
                                                            Log.d("Firebase", "Datos actualizados exitosamente")
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.e("Firebase", "Error al actualizar datos en la base: $e")
                                                        }
                                                    if (newPassword.isBlank()) {
                                                        password = ""
                                                        newPassword = ""
                                                        errorMessage = null
                                                        isLocked = true
                                                        changeSuccess = true
                                                    }

                                                } else {
                                                    Log.e("Firebase", "Error al actualizar el perfil: ${profileTask.exception}")
                                                }

                                                if (newPassword.isNotBlank()) {
                                                    currentUser.updatePassword(newPassword).addOnCompleteListener { passwordTask ->
                                                        if (passwordTask.isSuccessful) {
                                                            Log.d("Firebase", "Contraseña actualizada exitosamente")
                                                            password = ""
                                                            newPassword = ""
                                                            isLocked = true
                                                            errorMessage = null
                                                            changeSuccess = true
                                                        } else {
                                                            Log.e("Firebase", "Error al actualizar la contraseña: ${passwordTask.exception}")
                                                            errorMessage = "La nueva contraseña tiene un formato inválido."
                                                            isSaving = false
                                                        }
                                                        isSaving = false
                                                    }
                                                } else {
                                                    isSaving = false
                                                }
                                            }
                                        } else {
                                            Log.e("Firebase", "Error al enviar correo de verificación: ${emailTask.exception}")
                                            errorMessage = "Correo electrónico en uso, o inválido."
                                            isSaving = false
                                        }
                                    }
                                } else {
                                    Log.e("Firebase", "Error al reautenticar: ${reauthTask.exception}")
                                    errorMessage = "Contraseña actual incorrecta."
                                    isSaving = false
                                }
                            }
                        } else {
                            errorMessage = "Por favor, introduzca la contraseña para realizar cambios."
                            changeSuccess = false
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .padding(top = 60.dp)
                    .width(232.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaving) Color.Gray else Color(0xFF0048FF)
                ),
                enabled = !isSaving
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Guardar",
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(font.jaro_regular)),
                    color = Color(0xFFFFFFFF)
                )
            }
            Spacer(modifier = Modifier.height(69.dp))
        }
    }
    ToolBox(navController = navController, currentScreen) { screen ->
        currentScreen = screen
    }
}
