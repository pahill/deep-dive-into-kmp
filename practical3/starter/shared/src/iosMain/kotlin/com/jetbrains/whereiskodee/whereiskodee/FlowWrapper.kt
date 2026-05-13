package com.jetbrains.whereiskodee.whereiskodee

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FlowWrapper<T>(
    private val scope: CoroutineScope = MainScope(),
    private val flow: Flow<T>
) {
    fun subscribe(
        onEach: (T) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ): Job = scope.launch {
        try {
            flow.collect { onEach(it) }
            onComplete()
        } catch (e: Exception) {
            onError(e)
        }
    }
}
