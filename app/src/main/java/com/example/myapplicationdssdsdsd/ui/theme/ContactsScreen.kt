package com.example.myapplicationdssdsdsd.ui.theme

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navController: NavController) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val contacts = remember { mutableStateListOf<Pair<String, String>>() }
    var showDialog by remember { mutableStateOf(false) }

    // Cargar contactos al iniciar
    LaunchedEffect(Unit) {
        database.child("users").child(currentUserId).child("contacts").get().addOnSuccessListener {
            it.children.forEach { snapshot ->
                val contactUID = snapshot.key.toString()
                getUsernameFromUID(contactUID, database) { username ->
                    username?.let { contacts.add(contactUID to it) }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB))
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

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(contacts) { (uid, username) ->
                    ContactItem(
                        contactName = username,
                        onClick = { navController.navigate("ChatScreen/$uid/$username") },
                        onRemove = {
                            removeContact(currentUserId, uid, database) {
                                contacts.removeIf { it.first == uid } // Actualiza la lista local
                            }
                        }
                    )
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
    }
}

@Composable
fun ContactItem(
    contactName: String,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null
) {
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
            Text(
                text = contactName,
                fontSize = 20.sp,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
        IconButton(onClick = { onRemove?.invoke() }) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Eliminar contacto",
                modifier = Modifier.size(24.dp),
                tint = Color.Red
            )
        }
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

fun removeContact(currentUserId: String, contactId: String, database: DatabaseReference, onComplete: () -> Unit) {
    database.child("users").child(currentUserId).child("contacts").child(contactId).removeValue()
        .addOnSuccessListener {
            Log.d("RemoveContact", "Contacto eliminado correctamente del usuario actual.")
            onComplete()
        }
        .addOnFailureListener { exception ->
            Log.e("RemoveContact", "Error al eliminar el contacto: $exception")
        }

    database.child("users").child(contactId).child("contacts").child(currentUserId).removeValue()
        .addOnSuccessListener {
            Log.d("RemoveContact", "Contacto eliminado correctamente del otro usuario.")
        }
        .addOnFailureListener { exception ->
            Log.e("RemoveContact", "Error al eliminar el contacto del otro usuario: $exception")
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
