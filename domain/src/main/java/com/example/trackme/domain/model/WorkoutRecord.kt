package com.example.trackme.domain.model

import java.util.*

class WorkoutRecord(
    var workoutID: Int,
    var distance: Float,
    var speed: Float,
    var totalTime: String,
    val points: List<TrackLocation>,
    var date: Date,
    var imageData: String
) : Model()
