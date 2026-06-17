package com.jetbrains.cameraapp.permissions

import dev.icerock.moko.permissions.Permission

expect val requiredPermissions: List<PermissionNameType>

data class PermissionNameType(val name: String, val permission: Permission)