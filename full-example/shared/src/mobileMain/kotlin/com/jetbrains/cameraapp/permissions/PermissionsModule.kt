package com.jetbrains.cameraapp.permissions

import com.jetbrains.cameraapp.PlatformContext
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun getPermissionController(context: PlatformContext): PermissionsController

fun permissionsModule(): Module = module {
    factory<PermissionsController> { getPermissionController(get()) }
    factory<PermissionManager> { PermissionManager(get()) }
    factory<PermissionsCheck> { PermissionsCheckImpl(get<PermissionManager>()) }
    viewModel { PermissionsViewModel(get()) }
}