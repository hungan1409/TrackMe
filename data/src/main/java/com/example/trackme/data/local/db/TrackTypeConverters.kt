package com.example.trackme.data.local.db

import androidx.room.TypeConverter
import com.example.trackme.data.model.TrackLocationEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class TrackTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromString(value: String): List<TrackLocationEntity>? {
        val type: Type = object : TypeToken<List<TrackLocationEntity>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromList(list: List<TrackLocationEntity>): String? {
        return Gson().toJson(list)
    }
}