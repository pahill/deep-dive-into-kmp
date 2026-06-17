package com.jetbrains.cameraapp

import com.jetbrains.cameraapp.permissions.permissionsModule
import org.koin.core.module.Module

actual fun otherModules(): List<Module> = listOf(permissionsModule())