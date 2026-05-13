package com.jetbrains.cameraapp.filter

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal fun validateImage(file: File) {
    require(file.exists()) { "Image file does not exist: ${file.absolutePath}" }
    require(isImageFile(file.name)) { "Unsupported file format: ${file.extension}" }
}

internal fun loadImage(file: File): BufferedImage {
    return ImageIO.read(file)
        ?: throw IllegalArgumentException("Failed to load image: ${file.absolutePath}")

}

internal fun drawImage(image: BufferedImage, alteredImage: BufferedImage) {
    val graphics = alteredImage.createGraphics()
    graphics.drawImage(image, 0, 0, null)
    graphics.dispose()
}

internal fun saveImage(image: BufferedImage, file: File) {
    val format = when (file.extension.lowercase()) {
        "png" -> "png"
        else -> "jpg"
    }

    ImageIO.write(image, format, file)
}

internal fun fileExists(path: String): Boolean {
    return File(path).exists()
}