package com.app.trendsetter.app.initializer

import com.app.trendsetter.app.GlobalApplication
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : AppInitializer {
    override fun init(application: GlobalApplication) {
        Timber.plant(Timber.DebugTree())
    }
}
