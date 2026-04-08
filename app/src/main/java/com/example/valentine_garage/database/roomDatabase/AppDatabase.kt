package com.example.valentine_garage.database.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.valentine_garage.database.dao.TruckDao
import com.example.valentine_garage.database.dbUtil.Converters
import com.example.valentine_garage.database.entities.TruckEntity


@Database(
    entities = [
        TruckEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun truckDao(): TruckDao

}