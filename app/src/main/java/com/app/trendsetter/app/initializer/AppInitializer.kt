package com.app.trendsetter.app.initializer

import com.app.trendsetter.app.GlobalApplication


interface AppInitializer {
    fun init(application: GlobalApplication)
}