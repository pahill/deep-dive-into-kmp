package com.jetbrains.cameraapp.filter

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import java.io.File

class AndroidBlackAndWhiteFilter : BlackAndWhiteFilter {
    override fun filter(imagePath: String) {
        validate(imagePath)

        var originalBitmap = loadOriginalBitmap(imagePath)
        var blackAndWhiteBitmap: Bitmap? = null
        try {
            blackAndWhiteBitmap = createBitmap(originalBitmap)

            val canvas = createCanvas(blackAndWhiteBitmap)

            val paint = Paint().apply {
                // Create a color matrix that converts to grayscale
                val colorMatrix = ColorMatrix().apply {
                    setSaturation(0f)
                }
                colorFilter = ColorMatrixColorFilter(colorMatrix)
            }

            // Draw the original bitmap using the grayscale paint
            canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

            saveBitmap(blackAndWhiteBitmap, File(imagePath))

            // Clean up
            blackAndWhiteBitmap.recycle()
            originalBitmap.recycle()
        } catch (e: Exception) {
            blackAndWhiteBitmap?.recycle()
            originalBitmap.recycle()
            throw IllegalStateException("Failed to process image: ${e.message}")
        }
    }
}

actual fun getBlackAndWhiteFilter(): BlackAndWhiteFilter = AndroidBlackAndWhiteFilter()

