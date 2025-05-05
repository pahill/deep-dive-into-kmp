package com.jetbrains.cameraapp.filter

import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.File

class DesktopGaussianBlurFilter : GaussianBlurFilter {
    override fun filter(imagePath: String, radius: Float) {
        val file = File(imagePath)
        validateImage(file)

        val originalImage = loadImage(file)
        try {
            // Create Gaussian kernel
            val size = (radius * 2).toInt() + 1
            val kernel = createGaussianKernel(size, radius)

            // Apply convolution
            val convolveOp = ConvolveOp(
                Kernel(size, size, kernel),
                ConvolveOp.EDGE_NO_OP,
                null
            )

            // Create output image
            val blurredImage = BufferedImage(
                originalImage.width,
                originalImage.height,
                BufferedImage.TYPE_INT_ARGB
            )

            // Apply the filter
            convolveOp.filter(originalImage, blurredImage)

            // Save the result
            saveImage(blurredImage, file)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to process image: ${e.message}")
        }
    }

    private fun createGaussianKernel(size: Int, radius: Float): FloatArray {
        val kernel = FloatArray(size * size)
        val sigma = radius / 3f
        val twoSigmaSquare = 2f * sigma * sigma
        var sum = 0f
        val center = size / 2

        for (y in 0 until size) {
            for (x in 0 until size) {
                val xDistance = x - center
                val yDistance = y - center
                val distance = (xDistance * xDistance + yDistance * yDistance).toFloat()
                val index = y * size + x
                kernel[index] = Math.exp((-distance / twoSigmaSquare).toDouble()).toFloat()
                sum += kernel[index]
            }
        }

        // Normalize the kernel
        for (i in kernel.indices) {
            kernel[i] /= sum
        }

        return kernel
    }

    override fun filter(imagePath: String) {
        filter(imagePath, radius = 25f)
    }
}

actual fun getGaussianBlurFilter(): GaussianBlurFilter = DesktopGaussianBlurFilter()
