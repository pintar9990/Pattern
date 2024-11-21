package com.example.myapplicationdssdsdsd.ui.theme

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationdssdsdsd.ToolBox
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navController: NavController) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val contacts = remember { mutableStateListOf<String>() } // Para contactos confirmados
    val requests = remember { mutableStateListOf<String>() } // Para solicitudes pendientes
    var showDialog by remember { mutableStateOf(false) }  // Nuevo estado para mostrar el diálogo

    // Obtener contactos y solicitudes pendientes desde la base de datos
    LaunchedEffect(Unit) {
        // Obtener contactos
        database.child("users").child(currentUserId).child("contacts").get().addOnSuccessListener {
            it.children.forEach { snapshot ->
                contacts.add(snapshot.key.toString())
            }
        }
        // Obtener solicitudes pendientes
        database.child("users").child(currentUserId).child("requests").get().addOnSuccessListener {
            it.children.forEach { snapshot ->
                requests.add(snapshot.key.toString())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Fondo azul similar
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Contactos",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar solicitudes pendientes
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(requests) { request ->
                    ContactItem(contactName = request, isRequest = true, onAccept = {
                        acceptRequest(currentUserId, request, database)
                    }, onReject = {
                        rejectRequest(currentUserId, request, database)
                    })
                }

                items(contacts) { contact ->
                    ContactItem(contactName = contact, isRequest = false, onAccept = {}, onReject = {})
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AddContactButton(onClick = { showDialog = true })
            }

            if (showDialog) {
                openAddContactDialog(navController, currentUserId, onDismiss = { showDialog = false })
            }
        }

        ToolBox(navController) // Aquí se incluye el ToolBox para navegar
    }
}

@Composable
fun AddContactButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Agregar contacto",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun ContactItem(contactName: String, isRequest: Boolean, onAccept: () -> Unit, onReject: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Icono de contacto",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = contactName, fontSize = 20.sp)
        }

        if (isRequest) {
            Row {
                IconButton(onClick = onAccept) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Aceptar solicitud",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onReject) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Rechazar solicitud",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            IconButton(onClick = { /* Acción para abrir el chat */ }) {
                Icon(
                    imageVector = Icons.Filled.Message,
                    contentDescription = "Abrir chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun acceptRequest(currentUserId: String, requestId: String, database: DatabaseReference) {
    // Mover la solicitud a contactos
    database.child("users").child(currentUserId).child("contacts").child(requestId).setValue(true)
    database.child("users").child(requestId).child("contacts").child(currentUserId).setValue(true)

    // Eliminar la solicitud de la lista de solicitudes pendientes
    database.child("users").child(currentUserId).child("requests").child(requestId).removeValue()
    database.child("users").child(requestId).child("requests").child(currentUserId).removeValue()
}

fun rejectRequest(currentUserId: String, requestId: String, database: DatabaseReference) {
    // Eliminar la solicitud de la lista de solicitudes pendientes
    database.child("users").child(currentUserId).child("requests").child(requestId).removeValue()
    database.child("users").child(requestId).child("requests").child(currentUserId).removeValue()
}

@Composable
fun openAddContactDialog(navController: NavController, currentUserId: String, onDismiss: () -> Unit) {
    var userSearch by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buscar usuarios") },
        text = {
            Column {
                TextField(
                    value = userSearch,
                    onValueChange = { userSearch = it },
                    label = { Text("Nombre de usuario") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                LaunchedEffect(userSearch) {
                    if (userSearch.isNotEmpty()) {
                        searchUsers(userSearch, currentUserId) { results ->
                            searchResults = results
                        }
                    } else {
                        searchResults = emptyList()
                    }
                }

                if (searchResults.isNotEmpty()) {
                    LazyColumn {
                        items(searchResults) { user ->
                            TextButton(onClick = {
                                Log.d("FriendRequest", "Enviando solicitud de amistad a: $user")
                                sendFriendRequest(currentUserId, user, FirebaseDatabase.getInstance().reference) {
                                    Toast.makeText(navController.context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("Enviar solicitud de amistad a $user")
                            }
                        }
                    }
                } else if (userSearch.isNotEmpty()) {
                    Text("No se encontraron usuarios para $userSearch")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

fun searchUsers(userSearch: String, currentUserId: String, onResults: (List<String>) -> Unit) {
    FirebaseDatabase.getInstance().reference
        .child("users")
        .orderByChild("username")
        .equalTo(userSearch)
        .get()
        .addOnSuccessListener { snapshot ->
            val results = mutableListOf<String>()
            if (snapshot.exists()) {
                snapshot.children.forEach { userSnapshot ->
                    val username = userSnapshot.child("username").value.toString()
                    if (username != currentUserId) {
                        results.add(username)
                    }
                }
            }
            onResults(results)
        }
        .addOnFailureListener { exception ->
            Log.e("SearchError", "Error fetching users: ", exception)
        }
}

fun sendFriendRequest(currentUserId: String, recipientUsername: String, database: DatabaseReference, onSuccess: () -> Unit) {
    searchUserByUsername(recipientUsername) { recipientUID ->
        if (recipientUID != null) {
            // Enviar la solicitud de amistad usando el UID
            database.child("users").child(currentUserId).child("requests").child(recipientUID).setValue(true)
            database.child("users").child(recipientUID).child("requests").child(currentUserId).setValue(true)
            onSuccess()
        } else {
            Log.e("FriendRequest", "No se encontró el usuario con nombre $recipientUsername")
        }
    }
}

fun searchUserByUsername(username: String, onResult: (String?) -> Unit) {
    FirebaseDatabase.getInstance().reference
        .child("users")
        .orderByChild("username")
        .equalTo(username)
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userUID = snapshot.children.firstOrNull()?.key
                onResult(userUID)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("UserSearch", "Error searching for user: $exception")
            onResult(null)
        }
}
