package com.jetbrains.cameraapp.filter

import platform.Foundation.NSFileManager

internal fun validateImageFile(imagePath: String) {
    require(NSFileManager.defaultManager.fileExistsAtPath(imagePath)) {
        "Image file does not exist: $imagePath"
    }
    require(isImageFile(imagePath)) {
        "Unsupported file format: ${imagePath.substringAfterLast('.')}"
    }
}
