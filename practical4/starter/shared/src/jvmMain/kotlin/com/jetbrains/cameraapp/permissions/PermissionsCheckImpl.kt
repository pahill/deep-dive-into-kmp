package com.jetbrains.cameraapp.permissions

class PermissionsCheckImpl : PermissionsCheck {
    override suspend fun isGranted(): Boolean = true
}