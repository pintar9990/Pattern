package com.example.myapplicationdssdsdsd.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(contactId: String, contactName: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val messages = remember { mutableStateListOf<Pair<String, String>>() } // Pair(senderId, message)
    var newMessage by remember { mutableStateOf(TextFieldValue("")) }

    // Obtener mensajes
    LaunchedEffect(contactId) {
        database.child("chats").child(chatId(currentUserId, contactId)).get().addOnSuccessListener {
            it.children.forEach { snapshot ->
                val senderId = snapshot.child("senderId").value.toString()
                val message = snapshot.child("message").value.toString()
                messages.add(senderId to message)
            }
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
                Text(
                    text = "${if (senderId == currentUserId) "Tú" else contactName}: $message",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val chatRef = database.child("chats").child(chatId(currentUserId, contactId)).push()
                chatRef.setValue(mapOf("senderId" to currentUserId, "message" to newMessage.text))
                messages.add(currentUserId to newMessage.text)
                newMessage = TextFieldValue("")
            }) {
                Text("Enviar")
            }
        }
    }
}

fun chatId(user1: String, user2: String): String {
    return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
}
