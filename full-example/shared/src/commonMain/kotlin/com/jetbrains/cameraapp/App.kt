package com.jetbrains.cameraapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.jetbrains.cameraapp.camera.CameraScreen
import com.jetbrains.cameraapp.filter.BlackAndWhiteFilter
import com.jetbrains.cameraapp.filter.GaussianBlurFilter
import com.jetbrains.cameraapp.navigation.CameraAppScreen
import com.jetbrains.cameraapp.navigation.CameraAppScreen.Picture
import com.jetbrains.cameraapp.permissions.PermissionsCheck
import com.jetbrains.cameraapp.permissions.PermissionsScreen
import com.jetbrains.cameraapp.picture.PictureScreen
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

interface AppGraph {
    val permissionsCheck: PermissionsCheck
    val blackAndWhiteFilter: BlackAndWhiteFilter
    val blurFilter: GaussianBlurFilter
}

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(CameraAppScreen.Permissions::class, CameraAppScreen.Permissions.serializer())
            subclass(CameraAppScreen.Camera::class, CameraAppScreen.Camera.serializer())
            subclass(CameraAppScreen.Picture::class, CameraAppScreen.Picture.serializer())
        }
    }
}

@Composable
@Inject
fun App(metroVmf: MetroViewModelFactory, permissionsCheck: PermissionsCheck) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides metroVmf) {
        val (permissionsAreGranted, setPermissionsAreGranted) = remember {
            mutableStateOf<Boolean?>(null)
        }

        LaunchedEffect(Unit) {
            setPermissionsAreGranted(permissionsCheck.isGranted())
        }

        if (permissionsAreGranted != null) {
            val startRoute =
                if (permissionsAreGranted) CameraAppScreen.Camera else CameraAppScreen.Permissions
            val backStack = rememberNavBackStack(config, startRoute)

            MaterialTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize(),
                    entryProvider = entryProvider {
                        entry<CameraAppScreen.Permissions> {
                            PermissionsScreen({ backStack.add(CameraAppScreen.Camera) })
                        }
                        entry<CameraAppScreen.Camera> {
                            CameraScreen(onNext = { absoluteFilePath ->
                                backStack.add(
                                    CameraAppScreen.Picture(absoluteFilePath)
                                )
                            })
                        }
                        entry<Picture> { key ->
                            PictureScreen(
                                imagePath = key.imagePath,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                    })
            }
        }
    }
}