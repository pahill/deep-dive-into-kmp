package com.jetbrains.cameraapp.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetbrains.cameraapp.navigation.CameraAppScreen
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.fileDataRepresentation
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.writeToURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.OptIn
import kotlin.Throwable
import kotlin.Unit
import kotlin.let
import kotlin.random.Random
import kotlin.toString

private class PhotoCaptureDelegate(val onPicture: (String) -> Unit) : NSObject(),
    AVCapturePhotoCaptureDelegateProtocol {
    val captureSession = AVCaptureSession()
    val previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)
    val photoOutput = AVCapturePhotoOutput()

    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: platform.Foundation.NSError?
    ) {
        if (error == null) {
            val photoData = didFinishProcessingPhoto.fileDataRepresentation()
            val tempDir = NSTemporaryDirectory()
            val fileName = "photo-${Random.nextInt()}.jpg"
            val fileURL = NSURL.fileURLWithPath("$tempDir$fileName")

            if (photoData?.writeToURL(fileURL, true) == true) {
                onPicture(fileURL.path.toString())
            }
        }
    }
}

@Composable
actual fun CameraScreen(navController: NavController, modifier: Modifier) {
    val onPicture: (String) -> Unit = { picture ->
        navController.navigate(CameraAppScreen.Picture(picture))
    }
    SimulatorScreenContents(onPicture, modifier)
//        CameraScreenContents(onPicture, modifier)
}

@Composable
fun SimulatorScreenContents(onPicture: (String) -> Unit, modifier: Modifier) {
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        title = "Pick an image"
    ) { file ->
        val filePath = file?.path
        if (filePath != null) {
            onPicture(filePath)
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

@OptIn(ExperimentalForeignApi::class)
@Composable
fun CameraScreenContents(onPicture: (String) -> Unit, modifier: Modifier){
    val callback by rememberUpdatedState { onPicture }
    val capture = remember { PhotoCaptureDelegate(callback.invoke()) }

    DisposableEffect(Unit) {
        val captureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        try {
            captureDevice?.let { device ->
                val input =
                    AVCaptureDeviceInput.deviceInputWithDevice(device = device, error = null)
                if (input != null && capture.captureSession.canAddInput(input)) {
                    capture.captureSession.addInput(input)
                }
                if (capture.captureSession.canAddOutput(capture.photoOutput)) {
                    capture.captureSession.addOutput(capture.photoOutput)
                }
                capture.captureSession.startRunning()
            }
        } catch (e: Throwable) {
            println("Failed to setup camera: ${e.message}")
        }

        onDispose {
            capture.captureSession.stopRunning()
        }
    }

    Box {
        UIKitView<UIView>(
            factory = {
                val cameraContainer = object : UIView(frame = CGRectZero.readValue()) {
                    override fun layoutSubviews() {
                        CATransaction.begin()
                        CATransaction.setValue(true, kCATransactionDisableActions)
                        layer.setFrame(frame)
                        capture.previewLayer.setFrame(frame)
                        CATransaction.commit()
                    }
                }
                cameraContainer.layer.addSublayer(capture.previewLayer)
                capture.previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
                capture.captureSession.startRunning()
                cameraContainer
            },
            modifier = modifier.fillMaxSize()
        )

        Button(
            onClick = {
                val settings = AVCapturePhotoSettings.photoSettings()
                capture.photoOutput.capturePhotoWithSettings(settings, capture)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Take picture")
        }
    }
}