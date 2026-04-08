package com.example.valentine_garage.database.dbUtil

import androidx.room.TypeConverter
import java.math.BigInteger

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromBigInteger(value: BigInteger): String {
        return value.toString()
    }

    @TypeConverter
    fun toBigInteger(value: String): BigInteger {
        return BigInteger(value)
    }
}