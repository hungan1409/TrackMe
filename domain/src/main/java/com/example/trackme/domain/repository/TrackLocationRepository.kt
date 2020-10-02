package com.example.trackme.domain.repository

import com.example.trackme.domain.model.TrackLocation
import io.reactivex.Completable
import io.reactivex.Single

interface TrackLocationRepository : Repository {
    fun insertTrackLocation(trackLocation: TrackLocation): Completable
    fun getTrackLocationList(): Single<List<TrackLocation>>
    fun getLastTrackLocation(): Single<TrackLocation>
    fun clearAllTrackLocation(): Completable
}