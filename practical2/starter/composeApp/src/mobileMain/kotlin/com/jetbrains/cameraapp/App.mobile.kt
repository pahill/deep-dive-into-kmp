package com.jetbrains.cameraapp

import com.jetbrains.cameraapp.permissions.PermissionManager
import com.jetbrains.cameraapp.permissions.PermissionsCheck
import com.jetbrains.cameraapp.permissions.PermissionsCheckImpl
import com.jetbrains.cameraapp.permissions.PermissionsViewModel
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun getPermissionController(context: PlatformContext): PermissionsController

actual fun KoinApplication.mobileModule(): Module = module {
    factory<PermissionsController> { getPermissionController(get()) }
    factory<PermissionManager> { PermissionManager(get()) }
    factory<PermissionsCheck> { PermissionsCheckImpl(get()) }
    viewModel { PermissionsViewModel(get()) }
}