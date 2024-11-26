package com.example.myapplicationdssdsdsd.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplicationdssdsdsd.decodeBase64ToBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun ChatScreen(contactId: String, contactName: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val messages = remember { mutableStateListOf<Pair<String, String>>() }
    var newMessage by remember { mutableStateOf(TextFieldValue("")) }

    val chatPath = chatId(currentUserId, contactId)

    DisposableEffect(chatPath) {
        val chatRef = database.child("chats").child(chatPath)

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val senderId = snapshot.child("senderId").value.toString()
                val message = snapshot.child("message").value.toString()

                Log.d("ChatScreen", "Nuevo mensaje recibido - Sender: $senderId, Message: $message")

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Chat con $contactName", modifier = Modifier.padding(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(messages) { (senderId, message) ->
                if (message.startsWith("QR:")) {
                    Log.d("ChatScreen", "Mensaje QR detectado: $message")

                    val qrData = message.removePrefix("QR:")
                    val parts = qrData.split(",", limit = 3)  // Limitar a 3 partes: base64Data, link y lo que sea que quede después

                    // Obtener base64Data (primera parte sin el prefijo)

                    // Obtener el link (segunda parte)
                    val link = parts.getOrNull(2)
                    val base64Data= parts.getOrNull(1)

                    if (base64Data.isNullOrBlank() || link.isNullOrBlank()) {
                        Log.e("DecodeError", "Mensaje QR mal formado: $qrData")
                        Text(
                            text = "Error: Mensaje QR inválido",
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        val bitmap = decodeBase64ToBitmap(base64Data)
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "${if (senderId == currentUserId) "Tú" else contactName}: $link",
                                modifier = Modifier.padding(4.dp)
                            )
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR Image",
                                    modifier = Modifier.size(100.dp)
                                )
                            } ?: Text(
                                text = "Error al cargar la imagen QR",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "${if (senderId == currentUserId) "Tú" else contactName}: $message",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f),
                label = { Text(text = "Escribe un mensaje") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newMessage.text.isNotBlank()) {
                    val chatRef = database.child("chats").child(chatPath)
                    val messageKey = chatRef.push().key ?: return@Button

                    chatRef.child(messageKey).setValue(
                        mapOf(
                            "senderId" to currentUserId,
                            "message" to newMessage.text
                        )
                    )

                    newMessage = TextFieldValue("")
                }
            }) {
                Text(text = "Enviar")
            }
        }
    }
}

fun chatId(user1: String, user2: String): String {
    return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
}
