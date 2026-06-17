package com.jetbrains.cameraapp.permissions

public interface PermissionsCheck {
    suspend fun isGranted(): Boolean
}