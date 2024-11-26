package com.example.myapplicationdssdsdsd.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.myapplicationdssdsdsd.R
import com.example.myapplicationdssdsdsd.ToolBox
import com.example.myapplicationdssdsdsd.decodeBase64ToBitmap
import com.example.myapplicationdssdsdsd.QrItemData
import com.example.myapplicationdssdsdsd.FolderItemData

@Composable
fun SavedScreenUI(navController: NavHostController, registrationSuccess: Boolean = false) {
    var qrItems by remember { mutableStateOf<List<QrItemData>>(emptyList()) }
    var folderItems by remember { mutableStateOf<List<FolderItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeleteMode by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isFolderDialogVisible by remember { mutableStateOf(false) }
    var folderId by remember { mutableStateOf("") }

    // Obtener los QR y carpetas cuando se carga la pantalla
    LaunchedEffect(Unit) {
        getUserQrItems { items ->
            qrItems = items
        }
        getUserFolders { items ->
            folderItems = items
        }
        isLoading = false
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7BE2F4), Color(0xFFFDFDFD)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        if (isLoading) {
            Text(text = "Cargando...", modifier = Modifier.align(Alignment.Center))
        } else {
            Image(
                painter = painterResource(id = R.drawable.hamburguer),
                contentDescription = "Menu",
                modifier = Modifier
                    .size(60.dp)
                    .padding(top = 16.dp, start = 16.dp)
                    .clickable { expanded = true }
                    .align(Alignment.TopStart),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .align(Alignment.BottomCenter)
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        { Text("Añadir carpeta") },
                        onClick = {
                            isFolderDialogVisible = true
                            expanded = false
                        },
                    )
                    DropdownMenuItem(
                        { Text("Compartir QR") },
                        onClick = { /* Acción para compartir QR */ },
                    )
                    DropdownMenuItem(
                        { Text("Eliminar QR o Carpeta") },
                        onClick = {
                            isDeleteMode = true // Activamos el modo de eliminación
                            expanded = false
                        },
                    )

                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 47.dp, start = 31.dp, end = 31.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "QR guardados",
                        fontSize = 48.sp,
                        fontFamily = FontFamily(Font(R.font.lalezar_regular)),
                        color = Color(0xCC000000)
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                }

                // Primero, muestra las carpetas si no estamos en el modo de eliminación
                items(folderItems) { folder ->
                    FolderItem(
                        folder = folder,
                        isDeleteMode = isDeleteMode,
                        onTapItem = { item ->
                            if (isDeleteMode) {
                                deleteFolder(item) { updatedFolderItems ->
                                    folderItems = updatedFolderItems
                                }
                                isDeleteMode = false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Luego, muestra los QR
                items(qrItems) { item ->
                    QrItem(
                        item = item,
                        isDeleteMode = isDeleteMode,
                        onTapItem = { item ->
                            if (isDeleteMode) {
                                deleteQrItem(item) { updatedQrItems ->
                                    qrItems = updatedQrItems
                                }
                                isDeleteMode = false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

        }
    }

    // Mostrar el diálogo de carpeta si está visible
    if (isFolderDialogVisible) {
        createFolderDialog(
            onFolderCreated = { createdFolderId ->
                folderId = createdFolderId  // Actualiza el estado de folderId correctamente
                isFolderDialogVisible = false
                getUserFolders { items -> folderItems = items } // Actualiza la lista de carpetas
            }
        )
    }

    ToolBox(navController)

}

@Composable
fun FolderItem(folder: FolderItemData, isDeleteMode: Boolean, onTapItem: (FolderItemData) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTapItem(folder) }
            .background(if (isDeleteMode) Color.Red else Color.Transparent)  // Agregar fondo rojo en modo eliminación
            .padding(vertical = 10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.folder_icon),
            contentDescription = "Carpeta",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = folder.name,
            fontSize = 20.sp,
            fontFamily = FontFamily.Default,
            color = Color(0x99000000)
        )
    }
}

@Composable
fun QrItem(item: QrItemData, isDeleteMode: Boolean, onTapItem: (QrItemData) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDeleteMode) Color.Red else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTapItem(item) }
                )
            }
            .clickable {
                onTapItem(item)
            }
    ) {
        val bitmap = remember { decodeBase64ToBitmap(item.imageUrl) }
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = item.link,
            fontSize = 20.sp,
            fontFamily = FontFamily.Default,
            color = Color(0x99000000)
        )
    }
}


@Composable
fun createFolderDialog(onFolderCreated: (String) -> Unit) {
    var folderName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* Nada que hacer al descartar */ },
        title = { Text("Crear carpeta") },
        text = {
            TextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text("Nombre de la carpeta") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    createFolder(folderName) { folderId ->
                        onFolderCreated(folderId) // Pasa el ID de la carpeta creada
                        folderName = "" // Restablece el nombre de la carpeta
                    }
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = { /* Cancelar */ }) {
                Text("Cancelar")
            }
        }
    )
}

fun deleteSelectedItem(item: QrItemData, onItemsUpdated: (List<QrItemData>) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("qrs")
    dbRef.orderByChild("imageUrl").equalTo(item.imageUrl).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach {
                it.ref.removeValue() // Eliminar el QR
            }

            // Actualiza la lista de QR después de eliminarlo
            getUserQrItems { items ->
                onItemsUpdated(items)  // Actualiza la UI con la nueva lista
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error al eliminar QR: ${error.message}")
        }
    })
}
fun createFolder(name: String, onFolderCreated: (String) -> Unit) {
    val folderRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("folders")
        .push()

    folderRef.setValue(mapOf("name" to name, "qrs" to emptyList<String>())).addOnSuccessListener {
        onFolderCreated(folderRef.key ?: "") // Pasa el ID de la carpeta recién creada
    }
}

fun getUserQrItems(onComplete: (List<QrItemData>) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.uid?.let { uid ->
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        database.child("users").child(uid).child("qrs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val qrItems = mutableListOf<QrItemData>()
                    for (qrSnapshot in snapshot.children) {
                        val qr = qrSnapshot.getValue(QrItemData::class.java)
                        qr?.let {
                            qrItems.add(it)
                        }
                    }
                    onComplete(qrItems) // Devuelve los QR encontrados
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error al obtener datos: ${error.message}")
                }
            })
    }
}

fun getUserFolders(onComplete: (List<FolderItemData>) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.uid?.let { uid ->
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        database.child("users").child(uid).child("folders")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val folderItems = mutableListOf<FolderItemData>()
                    for (folderSnapshot in snapshot.children) {
                        val folder = folderSnapshot.getValue(FolderItemData::class.java)
                        folder?.let {
                            val folderWithId = it.copy(id = folderSnapshot.key ?: "") // Asignar el ID correctamente
                            folderItems.add(folderWithId)
                        }
                    }
                    onComplete(folderItems) // Devuelve las carpetas con el ID asignado correctamente
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error al obtener carpetas: ${error.message}")
                }
            })
    }
}


fun deleteFolder(folder: FolderItemData, onItemsUpdated: (List<FolderItemData>) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("folders")

    dbRef.child(folder.id).removeValue().addOnSuccessListener {
        // Actualiza la lista de carpetas después de eliminar la carpeta seleccionada
        getUserFolders { items ->
            onItemsUpdated(items)
        }
    }.addOnFailureListener {
        Log.e("FirebaseError", "Error al eliminar la carpeta: ${it.message}")
    }
}



fun deleteQrItem(item: QrItemData, onItemsUpdated: (List<QrItemData>) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("qrs")
    dbRef.orderByChild("imageUrl").equalTo(item.imageUrl).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach {
                it.ref.removeValue() // Eliminar el QR
            }

            // Actualiza la lista de QR después de eliminarlo
            getUserQrItems { items ->
                onItemsUpdated(items)  // Actualiza la UI con la nueva lista
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error al eliminar QR: ${error.message}")
        }
    })
}
