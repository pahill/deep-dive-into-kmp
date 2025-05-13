package com.jetbrains.cameraapp.permissions

import dev.icerock.moko.permissions.Permission

actual val requiredPermissions: List<Permission> = listOf(Permission.CAMERA, Permission.REMOTE_NOTIFICATION)