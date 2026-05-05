package com.jetbrains.cameraapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jetbrains.cameraapp.permissions.PermissionsCheck
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@ActivityKey
@ContributesIntoMap(AppScope::class, binding = binding<Activity>())
@Inject
public class MainActivity(private val permissionsCheck: PermissionsCheck, private val metroVmf: MetroViewModelFactory) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            CompositionLocalProvider(LocalMetroViewModelFactory provides metroVmf) {
                App(metroVmf, permissionsCheck)
            }
        }
    }
}