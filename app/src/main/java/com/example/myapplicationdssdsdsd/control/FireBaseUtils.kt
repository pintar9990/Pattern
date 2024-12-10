package com.example.myapplicationdssdsdsd.control

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myapplicationdssdsdsd.model.FolderItemData
import com.example.myapplicationdssdsdsd.model.QrItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.io.ByteArrayOutputStream
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
fun saveQrToFirebase(link: String, bitmap: Bitmap, auth: FirebaseAuth, database: DatabaseReference, onSuccess: () -> Unit) {
    val user = auth.currentUser
    user?.uid?.let { uid ->
        val qrId = UUID.randomUUID().toString()
        val qrRef = database.child("users").child(uid).child("qrs").child(qrId)

        // Convertir el bitmap a un string base64
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val qrImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

        val qrData = mapOf(
            "imageUrl" to "data:image/png;base64,$qrImage",
            "link" to link
        )

        qrRef.setValue(qrData)
            .addOnSuccessListener {
                Log.d("Firebase", "QR guardado exitosamente")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al guardar el QR", e)
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

fun deleteQrItem(item: QrItemData, onItemsUpdated: (List<QrItemData>) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().getReference("users")
        .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
        .child("qrs")
    dbRef.orderByChild("imageUrl").equalTo(item.imageUrl).addListenerForSingleValueEvent(object :
        ValueEventListener {
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