package com.jetbrains.whereiskodee.whereiskodee

import kotlinx.coroutines.flow.Flow

interface KodeeRepository {
    suspend fun locateKodee(): KodeeLocation
    fun locationHistory(): Flow<KodeeLocation>
}
