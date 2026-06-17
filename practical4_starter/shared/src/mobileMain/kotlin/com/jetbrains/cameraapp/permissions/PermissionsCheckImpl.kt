package com.jetbrains.cameraapp.permissions

import com.jetbrains.cameraapp.permissions.PermissionsCheck
import dev.icerock.moko.permissions.PermissionState

class PermissionsCheckImpl(private val manager: PermissionManager) : PermissionsCheck {
    override suspend fun isGranted(): Boolean = requiredPermissions.all {
        manager.getPermissionState(it.permission) == PermissionState.Granted
    }
}