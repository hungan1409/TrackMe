package com.example.trackme.mapper

import android.location.Location
import com.example.trackme.base.ItemMapper
import com.example.trackme.domain.model.TrackLocation
import com.example.trackme.model.TrackLocationItem
import javax.inject.Inject

class TrackLocationItemMapper @Inject constructor() :
    ItemMapper<TrackLocation, TrackLocationItem> {
    override fun mapToPresentation(model: TrackLocation): TrackLocationItem = TrackLocationItem(
        latitude = model.latitude,
        longitude = model.longitude,
        speed = model.speed,
        time = model.time
    )

    override fun mapToDomain(modelItem: TrackLocationItem) = TrackLocation(
        latitude = modelItem.latitude,
        longitude = modelItem.longitude,
        speed = modelItem.speed,
        time = modelItem.time
    )

    fun mapToTrackLocation(location: Location): TrackLocation {
        return TrackLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            speed = location.speed,
            time = location.time
        )
    }
}
