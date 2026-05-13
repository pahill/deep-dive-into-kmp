package com.jetbrains.cameraapp.permissions

import androidx.activity.ComponentActivity
import com.jetbrains.cameraapp.PlatformContext
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionsControllerImpl

actual fun getPermissionController(context: PlatformContext): PermissionsController =
    PermissionsControllerImpl(context).also {
        it.bind(context as ComponentActivity)
    }
