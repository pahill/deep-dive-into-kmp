package com.jetbrains.cameraapp

import android.app.Application
import androidx.activity.ComponentActivity
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionsControllerImpl

actual fun getPermissionController(context: PlatformContext): PermissionsController =
    PermissionsControllerImpl(context).also {
        it.bind(context as ComponentActivity)
    }

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.camera_splash,
                showPushNotification = true,
            )
        )
    }
}