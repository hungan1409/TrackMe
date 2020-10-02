package com.example.trackme.domain.model

data class TrackLocation(
    val latitude: Double,
    val longitude: Double,
    val time: Long,
    val speed: Float
) : Model()