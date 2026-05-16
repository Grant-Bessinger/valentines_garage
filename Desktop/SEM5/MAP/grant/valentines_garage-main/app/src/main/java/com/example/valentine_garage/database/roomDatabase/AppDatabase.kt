package com.example.valentine_garage.database.roomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.valentine_garage.database.dao.*
import com.example.valentine_garage.database.dbUtil.Converters
import com.example.valentine_garage.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        ClientEntity::class,
        VehicleEntity::class,
        TruckEntity::class,
        JobEntity::class,
        InvoiceEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun truckDao(): TruckDao
    abstract fun userDao(): UserDao
    abstract fun clientDao(): ClientDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun jobDao(): JobDao
    abstract fun invoiceDao(): InvoiceDao

}
