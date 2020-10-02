package com.example.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trackme.data.base.ModelEntity

@Entity(tableName = "track_location_table")
data class TrackLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val trackID: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val time: Long,
    val speed: Float
) : ModelEntity()