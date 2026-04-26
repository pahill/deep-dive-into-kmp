package com.jetbrains.cameraapp.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
expect fun CameraScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
)

