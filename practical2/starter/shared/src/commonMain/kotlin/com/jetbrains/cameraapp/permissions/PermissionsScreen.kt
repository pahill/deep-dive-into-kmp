package com.jetbrains.cameraapp.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PermissionsScreen(
    onNext: () -> Unit,
    modifier: Modifier = Modifier
)