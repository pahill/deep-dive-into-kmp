package com.jetbrains.cameraapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.jetbrains.cameraapp.filter.getBlackAndWhiteFilter
import com.jetbrains.cameraapp.filter.getGaussianBlurFilter
import com.jetbrains.cameraapp.navigation.CameraAppScreen
import com.jetbrains.cameraapp.navigation.CameraAppScreen.Picture
import com.jetbrains.cameraapp.permissions.PermissionsCheck
import com.jetbrains.cameraapp.permissions.PermissionsScreen
import com.jetbrains.cameraapp.picture.PictureScreen
import com.jetbrains.cameraapp.picture.PictureScreenViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module

private fun appModule(context: PlatformContext) = module {
    single<PlatformContext> { context }
    factory<BlackAndWhiteFilter> { getBlackAndWhiteFilter() }
    factory<GaussianBlurFilter> { getGaussianBlurFilter() }
    viewModel { params ->
        PictureScreenViewModel(
            imagePath = params.get(),
            blackAndWhiteFilter = get(),
            blurFilter = get()
        )
    }
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
fun App(context: PlatformContext) {
    KoinApplication(configuration = koinConfiguration(declaration = {
        modules(
            appModule(context),
            *otherModules().toTypedArray()
        )
    }), content = {
        val permissionsCheck = koinInject<PermissionsCheck>()
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
    )
}