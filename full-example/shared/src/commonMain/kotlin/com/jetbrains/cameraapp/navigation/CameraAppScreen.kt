package com.jetbrains.cameraapp.navigation

import kotlinx.serialization.Serializable

object CameraAppScreen {
    @Serializable object Permissions
    @Serializable object Camera
    @Serializable data class Picture(val imagePath: String)
}