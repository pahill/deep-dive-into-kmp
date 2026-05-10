package com.jetbrains.cameraapp.filter

import com.jetbrains.cameraapp.imagepath
import platform.CoreImage.*
import platform.Foundation.*
import platform.UIKit.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class IOSGaussianBlurFilter : GaussianBlurFilter {
    override fun filter(imagePath: String, radius: Float) {
        // Validate file exists and is an image
        validateImageFile(imagePath)

        // Create CIContext
        val context = CIContext(null)

        // Load the image
        val url = NSURL.fileURLWithPath(imagepath)
        val inputImage = CIImage.imageWithContentsOfURL(url)
            ?: throw IllegalArgumentException("Failed to load image: $imagePath")

        try {
            // Create and configure Gaussian blur filter
            val filter = CIFilter.filterWithName("CIGaussianBlur")
            filter?.setValue(inputImage, "inputImage")
            filter?.setValue(NSNumber(radius), "inputRadius")

            // Get the output image
            val outputImage = filter?.outputImage
                ?: throw IllegalStateException("Failed to apply Gaussian blur")

            // Create CGImage from CIImage
            val cgImage = context.createCGImage(outputImage, fromRect = outputImage.extent)
                ?: throw IllegalStateException("Failed to create CGImage")

            // Convert to UIImage and save
            val uiImage = UIImage.imageWithCGImage(cgImage)
            val imageData = when (imagePath.substringAfterLast('.').lowercase()) {
                "png" -> UIImagePNGRepresentation(uiImage)
                else -> UIImageJPEGRepresentation(uiImage, 1.0)
            } ?: throw IllegalStateException("Failed to create image data")

            // Write the file
            imageData.writeToFile(imagePath, atomically = true)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to create blurred image: $e")
        }
    }

    override fun filter(imagePath: String) {
        filter(
            imagePath = imagePath,
            radius = 25f)
    }
}

actual fun getGaussianBlurFilter(): GaussianBlurFilter = IOSGaussianBlurFilter()
