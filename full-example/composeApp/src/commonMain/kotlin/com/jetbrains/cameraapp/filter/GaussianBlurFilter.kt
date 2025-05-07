package com.jetbrains.cameraapp.filter

interface GaussianBlurFilter : Filter{

    fun filter(imagePath: String, radius: Float = 25f)

}

expect fun getGaussianBlurFilter(): GaussianBlurFilter
