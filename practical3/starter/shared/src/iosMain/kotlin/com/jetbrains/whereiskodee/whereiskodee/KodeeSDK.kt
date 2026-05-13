package com.jetbrains.whereiskodee.whereiskodee

class KodeeSDK() {
    private val repository: KodeeRepository = KodeeRepositoryImpl()

    fun locateKodeeWrapper(): SuspendWrapper<KodeeLocation> =
        SuspendWrapper { repository.locateKodee() }

    fun locationHistoryWrapper(): FlowWrapper<KodeeLocation> =
        FlowWrapper(flow = repository.locationHistory())
}
