package com.example.myapplicationdssdsdsd.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplicationdssdsdsd.R
import com.example.myapplicationdssdsdsd.decodeBase64ToBitmap
import com.example.myapplicationdssdsdsd.QrItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(contactId: String, contactName: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val messages = remember { mutableStateListOf<Pair<String, String>>() }
    var newMessage by remember { mutableStateOf(TextFieldValue("")) }
    var showQrPicker by remember { mutableStateOf(false) }

    val chatPath = chatId(currentUserId, contactId)

    DisposableEffect(chatPath) {
        val chatRef = database.child("chats").child(chatPath)

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val senderId = snapshot.child("senderId").value.toString()
                val message = snapshot.child("message").value.toString()

                if (message.isNotEmpty()) {
                    messages.add(senderId to message)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatScreen", "Error al escuchar mensajes: ${error.message}")
            }
        }

        chatRef.addChildEventListener(listener)

        onDispose {
            chatRef.removeEventListener(listener)
        }
    }

    // Desplazar automáticamente la lista de mensajes hacia abajo
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7BE2F4), Color.White)
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Chat con $contactName",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.lalezar_regular))
                ),
                modifier = Modifier.padding(8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState, // Usar el estado del scroll para la lista
                reverseLayout = false
            ) {
                items(messages) { (senderId, message) ->
                    if (message.startsWith("QR:")) {
                        val qrData = message.removePrefix("QR:")
                        val parts = qrData.split(",", limit = 3)

                        val link = parts.getOrNull(2)
                        val base64Data = parts.getOrNull(1)

                        if (base64Data.isNullOrBlank() || link.isNullOrBlank()) {
                            MessageBubble(
                                message = "Error: Mensaje QR inválido",
                                isSentByCurrentUser = senderId == currentUserId,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            val bitmap = decodeBase64ToBitmap(base64Data)
                            Column(
                                horizontalAlignment = if (senderId == currentUserId) Alignment.End else Alignment.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MessageBubble(
                                    message = link,
                                    isSentByCurrentUser = senderId == currentUserId,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "QR Image",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(100.dp)
                                            .align(if (senderId == currentUserId) Alignment.End else Alignment.Start)
                                    )
                                }
                            }
                        }
                    } else {
                        MessageBubble(
                            message = message,
                            isSentByCurrentUser = senderId == currentUserId,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Row(modifier =
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, Color(0xFF0048FF), RoundedCornerShape(16.dp))  // Border azul
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color(0xFF0048FF),
                        unfocusedIndicatorColor = Color(0xFF0048FF),
                        containerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { showQrPicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_icon_big),
                        contentDescription = "Seleccionar QR",
                        modifier = Modifier
                            .size(48.dp) // Ajuste del tamaño de la imagen para que no se recorte
                    )
                }

                Button(
                    onClick = {
                        val chatRef = database.child("chats").child(chatPath).push()
                        if (newMessage.text.isNotEmpty()) {
                            chatRef.setValue(
                                mapOf(
                                    "senderId" to currentUserId,
                                    "message" to newMessage.text
                                )
                            )
                            newMessage = TextFieldValue("")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0048FF))
                ) {
                    Text("Enviar")
                }
            }
        }
    }

    // Desplazar hacia abajo cuando se envíe el mensaje o seleccione un QR
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    if (showQrPicker) {
        QrPickerDialog(
            onDismiss = { showQrPicker = false },
            onQrSelected = { qrItem ->
                val chatRef = database.child("chats").child(chatPath).push()
                val message = "QR:${qrItem.imageUrl},${qrItem.link}"
                chatRef.setValue(
                    mapOf(
                        "senderId" to currentUserId,
                        "message" to message
                    )
                )
                showQrPicker = false
            }
        )
    }
}

@Composable
fun MessageBubble(message: String, isSentByCurrentUser: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSentByCurrentUser) Color(0xFF0048FF) else Color.White
            ),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isSentByCurrentUser) Color.White else Color.Black
            )
        }
    }
}

fun chatId(user1: String, user2: String): String {
    return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
}

@Composable
fun QrPickerDialog(onDismiss: () -> Unit, onQrSelected: (QrItemData) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    var qrItems by remember { mutableStateOf<List<QrItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            database.child("users").child(uid).child("qrs")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val items = mutableListOf<QrItemData>()
                        for (qrSnapshot in snapshot.children) {
                            val qr = qrSnapshot.getValue(QrItemData::class.java)
                            qr?.let { items.add(it) }
                        }
                        qrItems = items
                        isLoading = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("QrPickerDialog", "Error al cargar los QR: ${error.message}")
                    }
                })
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona un QR") },
        text = {
            if (isLoading) {
                Text("Cargando...")
            } else {
                LazyColumn {
                    items(qrItems) { qrItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onQrSelected(qrItem) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val bitmap = decodeBase64ToBitmap(qrItem.imageUrl)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR",
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = qrItem.link)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
