package com.example.myapplicationdssdsdsd.ui.theme

import android.util.Log
import android.widget.CheckBox
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.FolderItemData
import com.example.myapplicationdssdsdsd.QrItemData
import com.example.myapplicationdssdsdsd.R
import com.example.myapplicationdssdsdsd.ToolBox
import com.example.myapplicationdssdsdsd.decodeBase64ToBitmap
import com.example.myapplicationdssdsdsd.isValidUrl
import com.example.myapplicationdssdsdsd.openUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.myapplicationdssdsdsd.openUrl
import com.example.myapplicationdssdsdsd.isValidUrl


@Composable
fun SavedScreenUI(navController: NavHostController, registrationSuccess: Boolean = false) {
    var qrItems by remember { mutableStateOf<List<QrItemData>>(emptyList()) }
    var folderItems by remember { mutableStateOf<List<FolderItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeleteMode by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isFolderDialogVisible by remember { mutableStateOf(false) }
    var folderId by remember { mutableStateOf("") }
    var currentScreen by remember { mutableStateOf("SavedScreenUI") }
    var searchText by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Filtrado según la búsqueda
    val filteredFolders = folderItems.filter { it.name.contains(searchText, ignoreCase = true) }
    val filteredQrItems = qrItems.filter { it.link.contains(searchText, ignoreCase = true) }

    LaunchedEffect(Unit) {        getUserFolders { items ->
        folderItems = items
    }
        getUserQrItems { items ->
            val folderQrIds = folderItems.flatMap { it.qrs }
            qrItems = items.filterNot { folderQrIds.contains(it.imageUrl) } // Excluir QR en carpetas
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
            .background(gradient) // Fondo general
    ) {
        if (isLoading) {
            Text(text = "Cargando...", modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Menú desplegable y búsqueda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hamburguer),
                        contentDescription = "Menu",
                        modifier = Modifier
                            .size(60.dp)
                            .padding(16.dp)
                            .clickable { expanded = true },
                        contentScale = ContentScale.Fit
                    )
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        placeholder = { Text("Buscar...") }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                        { Text("Eliminar QR o Carpeta") },
                        onClick = {
                            isDeleteMode = true // Activamos el modo de eliminación
                            expanded = false
                        },
                    )
                }

                // Lista de carpetas y QR
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Deja espacio para el botón en la parte inferior
                        .padding(horizontal = 16.dp)
                ) {
                    // Mostrar carpetas filtradas
                    items(filteredFolders) { folder ->
                        FolderItem(
                            folder = folder,
                            isDeleteMode = isDeleteMode,
                            isSelected = selectedItems.contains(folder.id),
                            onSelect = { isSelected ->
                                selectedItems = if (isSelected) {
                                    selectedItems + folder.id
                                } else {
                                    selectedItems - folder.id
                                }
                            },
                            navController = navController,
                            onTapItem = { item ->
                                if (isDeleteMode) {
                                    selectedItems = selectedItems + item.id
                                } else {
                                    navController.navigate("FolderView/${folder.id}")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Mostrar códigos QR filtrados
                    items(filteredQrItems) { item ->
                        QrItem(
                            item = item,
                            isDeleteMode = isDeleteMode,
                            isSelected = selectedItems.contains(item.imageUrl),
                            onSelect = { isSelected ->
                                selectedItems = if (isSelected) {
                                    selectedItems + item.imageUrl
                                } else {
                                    selectedItems - item.imageUrl
                                }
                            },
                            onTapItem = { qrItem ->
                                if (isDeleteMode) {
                                    selectedItems = selectedItems + qrItem.imageUrl
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            // Botón para eliminar, visible sólo en modo eliminación
            if (isDeleteMode) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter) // Asegura que esté en la parte inferior
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    IconButton(

                        onClick = {
                            isDeleteMode = false
                            selectedItems = emptySet()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_arrow_icon),
                            contentDescription = "Desactivar modo de selección"
                        )
                    }

                    Button(
                        onClick = {
                            // Eliminar los elementos seleccionados
                            selectedItems.forEach { id ->
                                folderItems.find { it.id == id }?.let {
                                    deleteFolder(it) { updatedFolders -> folderItems = updatedFolders }
                                }
                                qrItems.find { it.imageUrl == id }?.let {
                                    deleteQrItem(it) { updatedQrs -> qrItems = updatedQrs }
                                }
                            }
                            isDeleteMode = false
                            selectedItems = emptySet()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar seleccionados")
                    }
                }
            }
        }
    }


    // Mostrar el diálogo de carpeta si está visible
    if (isFolderDialogVisible) {
        createFolderDialog(
            onFolderCreated = { createdFolderId ->
                folderId = createdFolderId
                isFolderDialogVisible = false
                getUserFolders { items -> folderItems = items }
            },
            onDismiss = {isFolderDialogVisible = false}
        )
    }
    ToolBox(navController, currentScreen) { screen ->
        currentScreen = screen
    }
}

@Composable
fun FolderItem(
    folder: FolderItemData,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onSelect: (Boolean) -> Unit,
    navController: NavHostController,
    onTapItem: (FolderItemData) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!isDeleteMode) {
                    navController.navigate("FolderView/${folder.id}")
                }
            }

        .background(if (isSelected) Color.Red else Color.Transparent)
    ) {
        if (isDeleteMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelect
            )
        }
        // Contenido de FolderItem
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
fun FolderView(folderId: String, navController: NavHostController) {
    var qrItems by remember { mutableStateOf<List<QrItemData>>(emptyList()) }
    var availableQrItems by remember { mutableStateOf<List<QrItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var folderName by remember { mutableStateOf("") }
    var showAddQrDialog by remember { mutableStateOf(false) }
    var isDeleteMode by remember { mutableStateOf(false) }

    // Cargar los QR y el nombre de la carpeta
    LaunchedEffect(folderId) {
        val folderRef = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect)
            .child("folders")
            .child(folderId)

        folderRef.get().addOnSuccessListener { snapshot ->
            folderName = snapshot.child("name").getValue(String::class.java) ?: "Sin Nombre"
            val qrIds = snapshot.child("qrs").getValue<List<String>>() ?: emptyList()
            getUserQrItems { allQrs ->
                qrItems = allQrs.filter { qrIds.contains(it.imageUrl) }
                availableQrItems = allQrs.filterNot { qrIds.contains(it.imageUrl) }
                isLoading = false
            }
        }.addOnFailureListener {
            Log.e("FirebaseError", "Error al cargar la carpeta: ${it.message}")
        }
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Sección superior: Título y botón de navegación
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Botón para volver atrás
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow_icon),
                        contentDescription = "Volver"
                    )
                }

                // Mostrar el nombre de la carpeta
                Text(
                    text = folderName,
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.lalezar_regular)),
                    color = Color(0xCC000000),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Contenido principal
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Cargando...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(qrItems) { item ->
                        QrItem(
                            item = item,
                            isDeleteMode = isDeleteMode,
                            isSelected = false,
                            onSelect = {},
                            onTapItem = { qrItem ->
                                if (isDeleteMode) {
                                    removeQrFromFolder(folderId, qrItem.imageUrl) {
                                        qrItems = qrItems - qrItem
                                    }
                                }
                            },
                            onRemoveFromFolder = {
                                removeQrFromFolder(folderId, item.imageUrl) {
                                    qrItems = qrItems - item
                                    // Añadir el QR de nuevo a la lista principal
                                    getUserQrItems { allQrs ->
                                        availableQrItems = allQrs
                                    }
                                }
                            }
                        )
                    }

                }
            }
        }

        // Botón para añadir QR
        IconButton(
            onClick = { showAddQrDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.qr_icon),
                contentDescription = "Añadir QR"
            )
        }

        // Mostrar diálogo para seleccionar QR
        if (showAddQrDialog) {
            SelectQrDialog(
                availableQrItems = availableQrItems,
                onQrSelected = { qr ->
                    addQrToFolder(folderId, qr.imageUrl) {
                        qrItems = qrItems + qr
                        availableQrItems = availableQrItems - qr
                        showAddQrDialog = false
                    }
                },
                onDismiss = { showAddQrDialog = false }
            )
        }
    }

}
@Composable
fun QrItem(
    item: QrItemData,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onSelect: (Boolean) -> Unit,
    onTapItem: (QrItemData) -> Unit,
    onRemoveFromFolder: (() -> Unit)? = null
) {
    val context = LocalContext.current // Obtén el contexto aquí

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) Color.Red else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!isDeleteMode) {
                            val url = item.link
                            if (isValidUrl(url)) {
                                openUrl(context, url)  // Abre la URL directamente
                            } else {
                                val googleSearchUrl = "https://www.google.com/search?q=$url"
                                openUrl(context, googleSearchUrl)
                            }
                        } else {
                            onTapItem(item)
                        }
                    }
                )
            }
            .padding(vertical = 10.dp)
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
        onRemoveFromFolder?.let {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = it) {
                Icon(Icons.Default.RemoveCircle, contentDescription = "Sacar de la carpeta")
            }
        }
        if (isDeleteMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelect
            )
        }
    }
}



@Composable
fun createFolderDialog(onFolderCreated: (String) -> Unit, onDismiss: () -> Unit) {
    var folderName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
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
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
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

fun addQrToFolder(folderId: String, qrId: String, onComplete: () -> Unit) {
    val folderRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("folders")
        .child(folderId)
        .child("qrs")

    folderRef.get().addOnSuccessListener { snapshot ->
        val currentQrIds = snapshot.getValue<List<String>>() ?: emptyList()
        val updatedQrIds = currentQrIds + qrId

        folderRef.setValue(updatedQrIds).addOnSuccessListener {
            onComplete()
        }.addOnFailureListener {
            Log.e("FirebaseError", "Error al añadir QR a la carpeta: ${it.message}")
        }
    }
}

fun removeQrFromFolder(folderId: String, qrId: String, onComplete: () -> Unit) {
    val folderRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("folders")
        .child(folderId)
        .child("qrs")

    folderRef.get().addOnSuccessListener { snapshot ->
        val currentQrIds = snapshot.getValue<List<String>>() ?: emptyList()
        val updatedQrIds = currentQrIds - qrId

        folderRef.setValue(updatedQrIds).addOnSuccessListener {
            onComplete()
        }.addOnFailureListener {
            Log.e("FirebaseError", "Error al eliminar QR de la carpeta: ${it.message}")
        }
    }
}

@Composable
fun SelectQrDialog(
    availableQrItems: List<QrItemData>,
    onQrSelected: (QrItemData) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar QR") },
        text = {
            LazyColumn {
                items(availableQrItems) { qrItem ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onQrSelected(qrItem) }
                            .padding(vertical = 8.dp)
                    ) {
                        val bitmap = decodeBase64ToBitmap(qrItem.imageUrl)
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(qrItem.link, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}



