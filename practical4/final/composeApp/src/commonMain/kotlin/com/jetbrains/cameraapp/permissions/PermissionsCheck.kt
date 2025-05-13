package com.jetbrains.cameraapp.permissions

interface PermissionsCheck {
    suspend fun isGranted(): Boolean
}