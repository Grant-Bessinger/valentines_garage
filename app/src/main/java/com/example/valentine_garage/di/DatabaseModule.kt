package com.example.valentine_garage.di

import android.app.Application
import androidx.room.Room
import com.example.valentine_garage.database.dao.TruckDao
import com.example.valentine_garage.database.roomDatabase.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "valentineGarageApplication"
        ).build()
    }

    @Provides
    fun provideTruckDao(db: AppDatabase): TruckDao {
        return db.truckDao()
    }
}