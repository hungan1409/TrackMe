package com.example.trackme.model

import com.example.trackme.base.ModelItem

data class TrackLocationItem(
    val latitude: Double,
    val longitude: Double,
    val time: Long,
    val speed: Float
) : ModelItem()