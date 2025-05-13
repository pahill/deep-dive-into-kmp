package com.jetbrains.cameraapp.filter

import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class IOSBlackAndWhiteFilter : BlackAndWhiteFilter{
    override fun filter(imagePath: String) {
        validateImageFile(imagePath)

        // Load the image
        val originalImage = UIImage.imageWithContentsOfFile(imagePath)
            ?: throw IllegalArgumentException("Failed to load image: $imagePath")

        // Get the image size
        val imageSize = originalImage.size
        val width = imageSize.useContents { width.toInt() }
        val height = imageSize.useContents { height.toInt() }

        // Create color space and context for grayscale
        val colorSpace = CGColorSpaceCreateDeviceGray()
        val context = CGBitmapContextCreate(
            null,
            width.toULong(),
            height.toULong(),
            8u,
            (width * 4).toULong(),
            colorSpace,
            CGImageAlphaInfo.kCGImageAlphaNone.value.toUInt()
        )

        if (context != null) {
            // Draw the image in the grayscale context
            val rect = CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble())
            CGContextDrawImage(context, rect, originalImage.CGImage)

            // Get the grayscale image
            val grayscaleImage = CGBitmapContextCreateImage(context)
            if (grayscaleImage != null) {
                // Create UIImage from grayscale CGImage
                val blackAndWhiteImage = UIImage.imageWithCGImage(grayscaleImage)

                // Convert to data
                val data = when {
                    imagePath.endsWith(".png", ignoreCase = true) ->
                        UIImagePNGRepresentation(blackAndWhiteImage)

                    else ->
                        UIImageJPEGRepresentation(blackAndWhiteImage, 1.0)
                }

                if (data != null) {
                    // Save the processed image
                    data.writeToFile(imagePath, atomically = true)
                } else {
                    throw IllegalStateException("Failed to encode processed image")
                }
                //CGImageRelease(grayscaleImage)
            } else {
                throw IllegalStateException("Failed to create grayscale image")
            }
            //CGContextRelease(context)
        } else {
            throw IllegalStateException("Failed to create graphics context")
        }
    }
}

actual fun getBlackAndWhiteFilter(): BlackAndWhiteFilter = IOSBlackAndWhiteFilter()