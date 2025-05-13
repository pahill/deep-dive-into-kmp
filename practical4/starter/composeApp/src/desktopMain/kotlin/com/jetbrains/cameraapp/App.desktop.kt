package com.jetbrains.cameraapp

import com.jetbrains.cameraapp.permissions.PermissionsCheck
import com.jetbrains.cameraapp.permissions.PermissionsCheckImpl
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun KoinApplication.mobileModule(): Module = module {
    factory<PermissionsCheck> { PermissionsCheckImpl() }
}
