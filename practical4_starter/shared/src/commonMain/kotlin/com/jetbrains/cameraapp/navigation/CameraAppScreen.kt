package com.jetbrains.cameraapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CameraAppScreen : NavKey {
    @Serializable object Permissions : CameraAppScreen
    @Serializable object Camera : CameraAppScreen
    @Serializable data class Picture(val imagePath: String) : CameraAppScreen
}