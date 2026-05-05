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
import dev.icerock.moko.permissions.PermissionState
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
actual fun PermissionsScreen(
    onNext: () -> Unit,
    modifier: Modifier
) {
    val viewModel: PermissionsViewModel = metroViewModel()
    viewModel.requestPermissions()

    val screenState = viewModel.screenState.collectAsState().value
    val permissionState = viewModel.permissionState.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (!screenState.inited) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Loading...")
            }
        } else {
            if (permissionState.all { it == PermissionState.Granted }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
                        onNext()
                    }) {
                        Text("Get Started")
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    permissionState.forEachIndexed { index, permissionState ->
                        Text("Permission state for ${screenState.permissions[index].name}: $permissionState")

                        if (permissionState == PermissionState.NotDetermined || permissionState == PermissionState.NotGranted || permissionState == PermissionState.Denied) {
                            Button(
                                onClick = {
                                    viewModel.onRequestPermissionButtonPressed(
                                        screenState.permissions[index].permission
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