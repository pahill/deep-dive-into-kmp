package com.jetbrains.cameraapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform