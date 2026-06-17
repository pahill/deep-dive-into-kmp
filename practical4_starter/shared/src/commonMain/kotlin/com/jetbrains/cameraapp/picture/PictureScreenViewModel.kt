package com.jetbrains.cameraapp.picture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.cameraapp.filter.BlackAndWhiteFilter
import com.jetbrains.cameraapp.filter.Filter
import com.jetbrains.cameraapp.filter.GaussianBlurFilter
import com.mmk.kmpnotifier.notification.NotificationImage
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class PictureScreenViewModel(val imagePath: String,
    private val blackAndWhiteFilter: BlackAndWhiteFilter,
    private val blurFilter: GaussianBlurFilter
) : ViewModel() {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun applyBlackAndWhiteFilter() {
        applyFilter(imagePath, blackAndWhiteFilter)
    }

    fun applyGaussianBlurFilter() {
        applyFilter(imagePath, blurFilter)
    }

    fun upload() {
        val notifier = NotifierManager.getLocalNotifier()
        notifier.notify {
            id= Random.nextInt(0, Int.MAX_VALUE)
            title = "Uploaded!"
            body = "Your image looks like this."
            image = NotificationImage.File(imagePath)
        }
    }

    private fun applyFilter(imagePath: String, filter: Filter) {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                withContext(Dispatchers.Default) {
                    filter.filter(imagePath)
                }
            } catch (e: Exception) {
                println("Failed to apply filter: ${e.message}")
            } finally {
                _isProcessing.value = false
            }
        }
    }
}