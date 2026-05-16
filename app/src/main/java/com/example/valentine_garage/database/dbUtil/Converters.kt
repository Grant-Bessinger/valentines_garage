package com.example.valentine_garage.database.dbUtil

import androidx.room.TypeConverter
import com.example.valentine_garage.dto.JobTaskDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigInteger

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromJobTaskList(value: List<JobTaskDto>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toJobTaskList(value: String): List<JobTaskDto> {
        val listType = object : TypeToken<List<JobTaskDto>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
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