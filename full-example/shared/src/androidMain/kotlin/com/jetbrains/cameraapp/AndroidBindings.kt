package com.jetbrains.cameraapp

import android.app.Activity
import android.app.Application
import android.content.Context
import com.jetbrains.cameraapp.filter.AndroidBlackAndWhiteFilter
import com.jetbrains.cameraapp.filter.AndroidGaussianBlurFilter
import com.jetbrains.cameraapp.filter.BlackAndWhiteFilter
import com.jetbrains.cameraapp.filter.GaussianBlurFilter
import com.jetbrains.cameraapp.permissions.PermissionManager
import com.jetbrains.cameraapp.permissions.PermissionsCheck
import com.jetbrains.cameraapp.permissions.PermissionsCheckImpl
import com.jetbrains.cameraapp.permissions.getPermissionController
import dev.icerock.moko.permissions.PermissionsController
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
object AndroidBindings {

    @Provides
    @SingleIn(AppScope::class)
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    fun providesPermissionManager(permissionsController: PermissionsController): PermissionManager =
        PermissionManager(permissionsController)

    @Provides
    fun providesPermissionsController(activity: Activity): PermissionsController =
        getPermissionController(activity)

    @Provides
    fun providesPermissionsCheck(permissionManager: PermissionManager): PermissionsCheck =
        PermissionsCheckImpl(permissionManager)

    @Provides
    fun providesBlackAndWhiteFilter(): BlackAndWhiteFilter =
        AndroidBlackAndWhiteFilter()

    @Provides
    fun providesBlurFilter(): GaussianBlurFilter =
        AndroidGaussianBlurFilter()

}