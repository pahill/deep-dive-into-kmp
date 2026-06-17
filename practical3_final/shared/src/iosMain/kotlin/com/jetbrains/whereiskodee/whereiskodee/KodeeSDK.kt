package com.jetbrains.whereiskodee.whereiskodee

class KodeeSDK() {
    private val repository: KodeeRepository = KodeeRepositoryImpl()

    suspend fun locateKodee() = repository.locateKodee()

    fun locationHistory() = repository.locationHistory()
}
