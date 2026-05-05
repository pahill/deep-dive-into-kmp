package com.jetbrains.cameraapp.filter

import android.content.Context
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import java.io.File

@Inject
class AndroidGaussianBlurFilter : GaussianBlurFilter {

    //Marton plz help: I don't want to use lateinit here
    private lateinit var context: Context

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

@Inject
actual fun getGaussianBlurFilter(): GaussianBlurFilter = AndroidGaussianBlurFilter()