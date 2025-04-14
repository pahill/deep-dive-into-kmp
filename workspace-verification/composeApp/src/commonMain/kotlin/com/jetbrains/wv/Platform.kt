package com.jetbrains.wv

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform