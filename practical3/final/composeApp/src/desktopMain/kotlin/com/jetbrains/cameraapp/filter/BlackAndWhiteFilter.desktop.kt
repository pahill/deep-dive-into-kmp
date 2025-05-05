package com.jetbrains.cameraapp.filter

import java.awt.image.BufferedImage
import java.io.File

class DesktopBlackAndWhiteFilter : BlackAndWhiteFilter {
    override fun filter(imagePath: String) {
        val file = File(imagePath)
        validateImage(file)

        val originalImage = loadImage(file)
        try {
            // Create grayscale image
            val width = originalImage.width
            val height = originalImage.height
            val blackAndWhiteImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

            // Draw the original image into the grayscale image
            drawImage(originalImage, blackAndWhiteImage)

            // Save the processed image
            saveImage(blackAndWhiteImage, file)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to process image: ${e.message}")
        }
    }
}

actual fun getBlackAndWhiteFilter(): BlackAndWhiteFilter = DesktopBlackAndWhiteFilter()