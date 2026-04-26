package com.jetbrains.cameraapp.filter

import android.graphics.*
import android.media.ExifInterface
import java.io.File

internal fun validate(imagePath: String) {
    // Validate file exists and is an image
    val file = File(imagePath)
    require(file.exists()) { "Image file does not exist: $imagePath" }
    require(isImageFile(file.name)) { "Unsupported file format: ${file.extension}" }
}

internal fun loadOriginalBitmap(imagePath: String): Bitmap {
    // Load the bitmap
    val bitmap = BitmapFactory.decodeFile(imagePath)
        ?: throw IllegalArgumentException("Failed to load image: $imagePath")

    // Get the EXIF orientation
    val exif = ExifInterface(imagePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    // Rotate the bitmap if needed
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.preScale(-1f, 1f)
            matrix.postRotate(270f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.preScale(-1f, 1f)
            matrix.postRotate(90f)
        }
    }

    // Apply the rotation if needed
    return if (matrix.isIdentity) {
        bitmap
    } else {
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        // Recycle the original bitmap if we created a new one
        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        rotatedBitmap
    }
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