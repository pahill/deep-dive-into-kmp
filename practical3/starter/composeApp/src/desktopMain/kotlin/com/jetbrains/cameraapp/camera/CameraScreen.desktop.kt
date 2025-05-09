package com.jetbrains.cameraapp.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetbrains.cameraapp.navigation.CameraAppScreen
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path

@Composable
actual fun CameraScreen(navController: NavController, modifier: Modifier) {
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        title = "Pick an image"
    ) { file ->
        val filePath = file?.path
        if (filePath != null) {
            navController.navigate(CameraAppScreen.Picture(filePath))
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                launcher.launch()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Select Image")
        }
    }
}
