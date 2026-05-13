package com.jetbrains.whereiskodee.whereiskodee

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SuspendWrapper<T>(
    private val scope: CoroutineScope = MainScope(),
    private val suspendFunction: suspend () -> T
) {
    fun subscribe(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ): Job = scope.launch {
        try {
            onSuccess(suspendFunction())
        } catch (e: Exception) {
            onError(e)
        }
    }
}
