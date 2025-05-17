package com.jetbrains.cameraapp

import dev.icerock.moko.permissions.PermissionsController

actual fun getPermissionController(context: PlatformContext): PermissionsController =
    dev.icerock.moko.permissions.ios.PermissionsController()

val imagepath = "wrong.png"