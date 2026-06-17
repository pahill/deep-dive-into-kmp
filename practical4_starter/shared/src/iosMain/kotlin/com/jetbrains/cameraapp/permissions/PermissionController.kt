package com.jetbrains.cameraapp.permissions

import com.jetbrains.cameraapp.PlatformContext
import dev.icerock.moko.permissions.PermissionsController

actual fun getPermissionController(context: PlatformContext): PermissionsController =
    dev.icerock.moko.permissions.ios.PermissionsController()