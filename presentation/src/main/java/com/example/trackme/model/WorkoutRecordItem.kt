package com.example.trackme.model

import android.graphics.Bitmap
import com.example.trackme.base.ModelItem
import java.util.*

class WorkoutRecordItem(
    val workoutID: Int = 0,
    val distance: Float,
    val speed: Float,
    val totalTime: String,
    val points: List<TrackLocationItem>,
    val date: Date,
    val bitmap: Bitmap?
) : ModelItem()





