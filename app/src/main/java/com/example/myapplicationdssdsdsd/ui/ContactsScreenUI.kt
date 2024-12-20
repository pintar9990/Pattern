package com.example.myapplicationdssdsdsd.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationdssdsdsd.components.ToolBox
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navController: NavController) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val contacts = remember { mutableStateListOf<Pair<String, String>>() } // UID y username de contactos
    val requests = remember { mutableStateListOf<Pair<String, String>>() } // UID y username de solicitudes
    var showDialog by remember { mutableStateOf(false) }  // Estado para mostrar el diálogo
    var currentScreen by remember { mutableStateOf("ContactsScreen") }

    // Obtener contactos y solicitudes pendientes desde la base de datos
    LaunchedEffect(Unit) {
        // Obtener contactos
        database.child("users").child(currentUserId).child("contacts").get().addOnSuccessListener {
            it.children.forEach { snapshot ->
                val contactUID = snapshot.key.toString()
                getUsernameFromUID(contactUID, database) { username ->
                    username?.let { contacts.add(contactUID to it) }
                }
            }
        }
        // Obtener solicitudes pendientes
        database.child("users").child(currentUserId).child("requests").get().addOnSuccessListener {
            it.children.forEach { snapshot ->
                val requestUID = snapshot.key.toString()
                getUsernameFromUID(requestUID, database) { username ->
                    username?.let { requests.add(requestUID to it) }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF7BE2F4), Color.White)
            ))
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

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(requests) { (uid, username) ->
                    ContactItem(contactName = username, isRequest = true, onAccept = {
                        acceptRequest(currentUserId, uid, database)
                        requests.remove(uid to username)
                        contacts.add(uid to username) // Añadir a contactos
                    }, onReject = {
                        rejectRequest(currentUserId, uid, database)
                        requests.remove(uid to username)
                    })
                }

                items(contacts) { (uid, username) ->
                    ContactItem(contactName = username, isRequest = false, onAccept = {}, onReject = {}, onRemove = {
                        removeContact(currentUserId, uid, database)
                        contacts.remove(uid to username) // Eliminar de la lista local
                    }, onClick = {
                        // Navegar a la pantalla de chat con el contacto
                        navController.navigate("ChatScreen/$uid/$username")
                    })
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

        ToolBox(navController, currentScreen){
            screen -> currentScreen = screen
        } // Aquí se incluye el ToolBox para navegar
    }
}

fun getUsernameFromUID(uid: String, database: DatabaseReference, onResult: (String?) -> Unit) {
    database.child("users").child(uid).child("username").get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                onResult(snapshot.value.toString())
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("GetUsername", "Error fetching username: $exception")
            onResult(null)
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
fun ContactItem(contactName: String, isRequest: Boolean, onAccept: () -> Unit, onReject: () -> Unit, onRemove: (() -> Unit)? = null, onClick: (() -> Unit)? = null) {
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
            Text(text = contactName, fontSize = 20.sp, modifier = Modifier.clickable { onClick?.invoke() })
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
            IconButton(onClick = { onRemove?.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Eliminar contacto",
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

// Función para buscar usuarios
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
            // Enviar la solicitud de amistad solo al destinatario
            database.child("users").child(recipientUID).child("requests").child(currentUserId).setValue(true)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e("FriendRequest", "Error al enviar la solicitud: $exception")
                }
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

fun removeContact(currentUserId: String, contactId: String, database: DatabaseReference) {
    // Eliminar del remitente
    database.child("users").child(currentUserId).child("contacts").child(contactId).removeValue()
        .addOnSuccessListener {
            Log.d("RemoveContact", "Contacto eliminado correctamente del usuario actual.")
        }
        .addOnFailureListener { exception ->
            Log.e("RemoveContact", "Error al eliminar el contacto: $exception")
        }

    // Eliminar del destinatario
    database.child("users").child(contactId).child("contacts").child(currentUserId).removeValue()
        .addOnSuccessListener {
            Log.d("RemoveContact", "Contacto eliminado correctamente del otro usuario.")
        }
        .addOnFailureListener { exception ->
            Log.e("RemoveContact", "Error al eliminar el contacto del otro usuario: $exception")
        }
}
