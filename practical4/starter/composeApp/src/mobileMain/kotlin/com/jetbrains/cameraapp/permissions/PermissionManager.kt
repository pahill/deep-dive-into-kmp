package com.jetbrains.cameraapp.permissions

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController

class PermissionManager(val permissionsController: PermissionsController) {
    suspend fun requestPermission(
        permission: Permission,
        onComplete: (PermissionState) -> Unit
    ) {
        try {
            permissionsController.providePermission(permission)
            onComplete(permissionsController.getPermissionState(permission))
        } catch (dae: DeniedAlwaysException) {
            onComplete(PermissionState.DeniedAlways)
        } catch (de: DeniedException) {
            onComplete(PermissionState.Denied)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getPermissionState(permission: Permission): PermissionState {
        return permissionsController.getPermissionState(permission)
    }
}