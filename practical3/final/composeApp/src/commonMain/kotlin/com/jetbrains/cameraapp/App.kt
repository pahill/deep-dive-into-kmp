package com.jetbrains.cameraapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun KoinApplication.mobileModule(): Module

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

@Composable
fun App(context: PlatformContext) = KoinApplication(
    application = { modules(appModule(context), mobileModule()) }
) {
    val navController: NavHostController = rememberNavController()
    val permissionsCheck = koinInject<PermissionsCheck>()
    val (permissionsAreGranted, setPermissionsAreGranted) = remember {
        mutableStateOf<Boolean?>(null)
    }

    LaunchedEffect(Unit) {
        setPermissionsAreGranted(permissionsCheck.isGranted())
    }
    if (permissionsAreGranted != null) {
        MaterialTheme {
            NavHost(
                navController = navController,
                startDestination = if (permissionsAreGranted) CameraAppScreen.Camera else CameraAppScreen.Permissions,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                composable<CameraAppScreen.Permissions> {
                    PermissionsScreen(navController)
                }
                composable<CameraAppScreen.Camera> {
                    CameraScreen(
                        navController,
                        modifier = Modifier
                    )
                }
                composable<Picture> { backStackEntry ->
                    val picture = backStackEntry.toRoute<Picture>()
                    PictureScreen(
                        imagePath = picture.imagePath,
                        navController = navController,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}