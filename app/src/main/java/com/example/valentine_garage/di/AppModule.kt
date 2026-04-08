package com.example.valentine_garage.di

import com.example.valentine_garage.database.dao.TruckDao
import com.example.valentine_garage.ui.repositories.TruckRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRepository(userDao: TruckDao): TruckRepository {
        return TruckRepository(userDao)
    }
}