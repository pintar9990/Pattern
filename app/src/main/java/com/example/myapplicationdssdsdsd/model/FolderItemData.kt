package com.example.myapplicationdssdsdsd.model

data class FolderItemData(
    val id: String = "",       // Id único de la carpeta, que puedes generar con push()
    val name: String = "",     // Nombre de la carpeta
    val qrs: List<String> = listOf()  // Lista de IDs de QR asociados a la carpeta
)
