package com.example.trackme.data.mapper

import com.example.trackme.data.base.EntityMapper
import com.example.trackme.data.model.TrackLocationEntity
import com.example.trackme.domain.model.TrackLocation
import javax.inject.Inject

class TrackLocationEntityMapper @Inject constructor() :
    EntityMapper<TrackLocation, TrackLocationEntity> {
    override fun mapToDomain(entity: TrackLocationEntity) = TrackLocation(
        latitude = entity.latitude,
        longitude = entity.longitude,
        speed = entity.speed,
        time = entity.time
    )

    override fun mapToEntity(model: TrackLocation) = TrackLocationEntity(
        latitude = model.latitude,
        longitude = model.longitude,
        speed = model.speed,
        time = model.time
    )
}