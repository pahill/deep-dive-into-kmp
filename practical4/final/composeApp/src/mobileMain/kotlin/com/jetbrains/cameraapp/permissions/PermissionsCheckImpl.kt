package com.jetbrains.cameraapp.permissions

import dev.icerock.moko.permissions.PermissionState

class PermissionsCheckImpl(private val manager: PermissionManager) : PermissionsCheck {
    override suspend fun isGranted(): Boolean = requiredPermissions.all {
        manager.getPermissionState(it) == PermissionState.Granted
    }
}