package com.jetbrains.cameraapp.permissions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class PermissionsViewModel(
    private val permissionManager: PermissionManager
) : ViewModel() {

    val screenState: MutableStateFlow<PermissionScreenState> =
        MutableStateFlow(PermissionScreenState(inited = false, permissions = listOf()))

    val permissionState = MutableStateFlow(mutableStateListOf<PermissionState>())

    fun onRequestPermissionButtonPressed(permission: Permission) {
        viewModelScope.launch {
            requestPermission(permission)
        }
    }

    private suspend fun requestPermission(permission: Permission) {
        val index = screenState.value.permissions.indexOfFirst { permission == it.permission }
        if (index != -1) {
            permissionManager.requestPermission(permission) { state ->
                permissionState.update { it[index] = state; it }
            }
        }
    }

    fun requestPermissions() {
        if (!screenState.value.inited) {
            screenState.value.inited = true
            val permissions = requiredPermissions
            screenState.value.permissions = permissions
            viewModelScope.launch {
                val stateList = MutableList(permissions.size) { PermissionState.NotDetermined }
                permissionState.value = stateList.toMutableStateList()
                for (index in 0..<permissions.size) {
                    permissionState.value[index] =
                        permissionManager.getPermissionState(permissions[index].permission)
                }
            }
        }
    }
}


class PermissionScreenState(
    var inited: Boolean = false,
    var permissions: List<PermissionNameType>
)