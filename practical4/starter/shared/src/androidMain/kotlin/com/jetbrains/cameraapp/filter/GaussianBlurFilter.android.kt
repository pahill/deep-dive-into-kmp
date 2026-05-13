package com.jetbrains.cameraapp.filter

import android.content.Context
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class AndroidGaussianBlurFilter : GaussianBlurFilter, KoinComponent {
    private val context: Context by inject()

    override fun filter(imagePath: String, radius: Float) {
        validate(imagePath)

        // Load the original bitmap
        val originalBitmap = loadOriginalBitmap(imagePath)

        try {
            val blurredBitmap = createBitmap(originalBitmap)

            // Create RenderScript context
            val rs = RenderScript.create(context)

            // Create allocations for input and output
            val input = Allocation.createFromBitmap(rs, originalBitmap)
            val output = Allocation.createFromBitmap(rs, blurredBitmap)

            // Create the blur script
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setRadius(radius.coerceIn(0.0f, 25.0f))
            blurScript.setInput(input)

            // Execute the script
            blurScript.forEach(output)
            output.copyTo(blurredBitmap)

            saveBitmap(blurredBitmap, File(imagePath))

            // Clean up resources
            blurScript.destroy()
            input.destroy()
            output.destroy()
            rs.destroy()
            blurredBitmap.recycle()
            originalBitmap.recycle()
        } catch (e: Exception) {
            originalBitmap.recycle()
            throw IllegalStateException("Failed to process image: ${e.message}")
        }
    }

    override fun filter(imagePath: String) {
        filter(imagePath, radius = 25f)
    }
}

actual fun getGaussianBlurFilter(): GaussianBlurFilter = AndroidGaussianBlurFilter()
