package com.jetbrains.cameraapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mmk.kmpnotifier.extensions.composeDesktopResourcesPath
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import io.github.vinceglb.filekit.FileKit
import java.io.File

fun main() = application {
    FileKit.init(appId = "MyApplication")

    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = true,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "resources/splash.png"
        )
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinConfCameraApp",
    ) {
        App(DesktopContext)
    }
}