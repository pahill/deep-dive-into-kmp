package com.jetbrains.cameraapp.filter

import android.graphics.*
import java.io.File

internal fun validate(imagePath: String) {
    // Validate file exists and is an image
    val file = File(imagePath)
    require(file.exists()) { "Image file does not exist: $imagePath" }
    require(isImageFile(file.name)) { "Unsupported file format: ${file.extension}" }
}

internal fun loadOriginalBitmap(imagePath: String): Bitmap {
    return BitmapFactory.decodeFile(imagePath)
        ?: throw IllegalArgumentException("Failed to load image: $imagePath")
}

internal fun createBitmap(originalBitmap: Bitmap): Bitmap {
    val alteredBitmap = Bitmap.createBitmap(
        originalBitmap.width,
        originalBitmap.height,
        Bitmap.Config.ARGB_8888
    )

    return alteredBitmap
}

internal fun createCanvas(blackAndWhiteBitmap: Bitmap) = Canvas(blackAndWhiteBitmap)

internal fun saveBitmap(alteredBitmap: Bitmap, file: File) {
    alteredBitmap.compress(
        when (file.extension.lowercase()) {
            "png" -> Bitmap.CompressFormat.PNG
            else -> Bitmap.CompressFormat.JPEG
        },
        100,
        file.outputStream()
    )
}