package com.jetbrains.cameraapp.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetbrains.cameraapp.navigation.CameraAppScreen
import dev.icerock.moko.permissions.PermissionState
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun PermissionsScreen(
    navController: NavController,
    modifier: Modifier
) {
    val viewModel: PermissionsViewModel = koinViewModel()
    viewModel.requestPermissions()

    val screenState = viewModel.screenState.collectAsState().value
    val permissionState = viewModel.permissionState.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!screenState.inited) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Loading...")
            }
        } else {
            if (permissionState.all { it == PermissionState.Granted }) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                    Button(onClick = {
                        navController.navigate(CameraAppScreen.Camera) {
                            val root = navController.currentBackStack.value
                                .firstOrNull { it.destination.route != null }
                                ?.destination?.route
                            if (root != null) {
                                popUpTo(root) { inclusive = true }
                            }
                        }
                    }) {
                        Text("Get Started")
                    }
                }
            } else {
                Column {
                    permissionState.forEachIndexed { index, permissionState ->
                        Text("Permission state for ${screenState.permissions[index]}: $permissionState")

                        if (permissionState == PermissionState.NotDetermined || permissionState == PermissionState.NotGranted || permissionState == PermissionState.Denied) {
                            Button(
                                onClick = {
                                    viewModel.onRequestPermissionButtonPressed(
                                        screenState.permissions[index]
                                    )
                                },
                                content = { Text("Grant permission") }
                            )
                        }
                    }
                }
            }
        }
    }
}