package com.app.trendsetter.app.initializer


import com.app.trendsetter.app.GlobalApplication
import javax.inject.Inject

class AppInitializers @Inject constructor(
    private val initializers: Set<@JvmSuppressWildcards AppInitializer>
) {
    fun init(application: GlobalApplication) {
        initializers.forEach {
            it.init(application)
        }
    }
}