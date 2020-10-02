package com.example.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trackme.data.base.ModelEntity
import java.util.*

@Entity(tableName = "workout_record_table")
data class WorkoutRecordEntity(
    @PrimaryKey(autoGenerate = true)
    var workoutID: Int = 0,
    var distance: Float,
    var speed: Float,
    var totalTime: String,
    val points: List<TrackLocationEntity>,
    var date: Date,
    var imageData: String
) : ModelEntity()
