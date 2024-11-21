package com.example.myapplicationdssdsdsd.ui.theme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.myapplicationdssdsdsd.R
import com.example.myapplicationdssdsdsd.ToolBox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data class to represent QR data from Firebase
data class QrItemData(
    val imageUrl: String = "",
    val link: String = ""
)

@Composable
fun SavedScreenUI(navController: NavHostController, registrationSuccess: Boolean = false) {
    var qrItems by remember { mutableStateOf<List<QrItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedItems by remember { mutableStateOf(setOf<QrItemData>()) }
    var isSelectionMode by remember { mutableStateOf(false) }

    // Obtener los QR cuando se carga la pantalla
    LaunchedEffect(Unit) {
        getUserQrItems { items ->
            Log.d("SavedScreenUI", "Datos obtenidos: $items")
            qrItems = items
            isLoading = false
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
        if (isLoading) {
            // Muestra un cargando mientras se obtienen los datos
            Text(text = "Cargando...", modifier = Modifier.align(Alignment.Center))
        } else {
            Column {
                if (selectedItems.isNotEmpty()) {
                    SelectionActions(
                        selectedItems = selectedItems,
                        onClearSelection = {
                            selectedItems = emptySet()
                            isSelectionMode = false
                        },
                        onCreateFolder = { /* Acción para crear carpeta */ },
                        onShare = { /* Acción para compartir */ },
                        onDelete = { /* Acción para eliminar */ }
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 47.dp, start = 31.dp, end = 31.dp)
                ) {
                    item {
                        Text(
                            text = "QR guardados",
                            fontSize = 48.sp,
                            fontFamily = FontFamily(Font(R.font.lalezar_regular)),
                            color = Color(0xCC000000)
                        )
                        Spacer(modifier = Modifier.height(38.dp))
                    }

                    items(qrItems) { item ->
                        QrItem(
                            item = item,
                            isSelected = selectedItems.contains(item),
                            isSelectionMode = isSelectionMode,
                            onSelectItem = { selectedItem ->
                                if (isSelectionMode) {
                                    selectedItems = if (selectedItems.contains(selectedItem)) {
                                        selectedItems - selectedItem
                                    } else {
                                        selectedItems + selectedItem
                                    }
                                } else {
                                    selectedItems = setOf(selectedItem)
                                    isSelectionMode = true
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
    ToolBox(navController)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QrItem(item: QrItemData, isSelected: Boolean, isSelectionMode: Boolean, onSelectItem: (QrItemData) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if (!isSelectionMode) {
                            val job = coroutineScope.launch {
                                delay(2000) // Espera 2 segundos
                                onSelectItem(item)
                            }
                            tryAwaitRelease()
                            job.cancel()
                        } else {
                            onSelectItem(item)
                        }
                    }
                )
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
            text = item.link,  // Mostrar el enlace del QR
            fontSize = 20.sp,
            fontFamily = FontFamily.Default,
            color = Color(0x99000000)
        )
    }
}

@Composable
fun SelectionActions(
    selectedItems: Set<QrItemData>,
    onClearSelection: () -> Unit,
    onCreateFolder: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${selectedItems.size} seleccionados", color = Color.White)
        Row {
            IconButton(onClick = onCreateFolder) {
                Icon(painter = painterResource(id = R.drawable.folder_icon), contentDescription = "Crear carpeta")
            }
            IconButton(onClick = onShare) {
                Icon(painter = painterResource(id = R.drawable.message_icon), contentDescription = "Compartir")
            }
            IconButton(onClick = onDelete) {
                Icon(painter = painterResource(id = R.drawable.patterns), contentDescription = "Eliminar")
            }
            IconButton(onClick = onClearSelection) {
                Icon(painter = painterResource(id = R.drawable.password_icon), contentDescription = "Cancelar selección")
            }
        }
    }
}

// Función para obtener los QR guardados en Firebase
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
                    // Manejar errores
                    Log.e("FirebaseError", "Error al obtener datos: ${error.message}")
                }
            })
    }
}

// Función para decodificar la cadena base64 a un Bitmap
fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val base64Image = base64Str.split(",")[1]
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("DecodeError", "Error al decodificar la imagen: ${e.message}")
        null
    }
}
