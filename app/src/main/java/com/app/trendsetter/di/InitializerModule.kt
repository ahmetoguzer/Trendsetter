package com.app.trendsetter.di

import com.app.trendsetter.app.initializer.AppInitializer
import com.app.trendsetter.app.initializer.TimberInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object InitializerModule {

    @IntoSet
    @Provides
    fun provideTimberInitializer(): AppInitializer = TimberInitializer()
}
