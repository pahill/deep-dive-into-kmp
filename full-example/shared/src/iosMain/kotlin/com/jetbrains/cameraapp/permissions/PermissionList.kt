package com.jetbrains.cameraapp.permissions

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.gallery.GALLERY
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION

actual val requiredPermissions: List<Permission> = listOf(Permission.CAMERA, Permission.REMOTE_NOTIFICATION, Permission.GALLERY)