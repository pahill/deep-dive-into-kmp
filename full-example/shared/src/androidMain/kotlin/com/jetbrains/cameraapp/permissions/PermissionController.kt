package com.jetbrains.cameraapp.permissions

import android.app.Activity
import androidx.activity.ComponentActivity
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionsControllerImpl

fun getPermissionController(context: Activity): PermissionsController =
    PermissionsControllerImpl(context).also {
        it.bind(context as ComponentActivity)
    }
