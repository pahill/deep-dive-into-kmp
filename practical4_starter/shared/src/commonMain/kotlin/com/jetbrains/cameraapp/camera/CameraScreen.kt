package com.jetbrains.cameraapp.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraScreen (
    onNext: (String) -> Unit,
    modifier: Modifier = Modifier,
)

