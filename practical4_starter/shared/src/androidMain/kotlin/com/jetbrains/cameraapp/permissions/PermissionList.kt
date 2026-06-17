package com.jetbrains.cameraapp.permissions

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION

actual val requiredPermissions: List<PermissionNameType> = listOf(
    PermissionNameType("Camera", Permission.CAMERA),
    PermissionNameType("Notifications", Permission.REMOTE_NOTIFICATION)
)