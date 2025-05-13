package com.jetbrains.cameraapp.filter

fun isImageFile(fileName: String): Boolean {
    val extension = fileName.substringAfterLast('.', "").lowercase()
    return extension in setOf("jpg", "jpeg", "png")
}