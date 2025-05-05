package com.jetbrains.cameraapp.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
expect fun PermissionsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
)