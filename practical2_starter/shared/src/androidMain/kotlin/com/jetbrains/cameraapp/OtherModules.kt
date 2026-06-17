package com.jetbrains.cameraapp

import org.koin.core.module.Module
import com.jetbrains.cameraapp.permissions.permissionsModule

actual fun otherModules(): List<Module> = listOf(permissionsModule())