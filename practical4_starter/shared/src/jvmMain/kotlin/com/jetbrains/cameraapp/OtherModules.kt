package com.jetbrains.cameraapp

import com.jetbrains.cameraapp.permissions.PermissionsCheck
import org.koin.core.module.Module
import org.koin.dsl.module

fun permissionsModule(): Module =
    module {
        single<PermissionsCheck> {
            object : PermissionsCheck {
                override suspend fun isGranted(): Boolean = true
            }
        }
    }

actual fun otherModules(): List<Module> = listOf(permissionsModule())