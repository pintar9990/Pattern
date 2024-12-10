package com.example.myapplicationdssdsdsd.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun onLogin(email: String, password: String): String? {

    var errorMessage by remember { mutableStateOf<String?>("") }

    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Manejar errores
                errorMessage = when (task.exception?.message) {
                    "The email address is badly formatted." -> "La dirección de correo electrónico no tiene un formato válido."
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> "No existe una cuenta con este correo electrónico."
                    "The password is invalid or the user does not have a password." -> "La contraseña es incorrecta."
                    else -> "Error al iniciar sesión. Por favor, verifica tus credenciales."
            }
        }
    }
    return errorMessage
}