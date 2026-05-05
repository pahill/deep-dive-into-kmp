package com.jetbrains.cameraapp

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph, ViewModelGraph, MetroAppComponentProviders {

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides application: Application): AndroidAppGraph
    }
}