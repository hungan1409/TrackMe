package com.example.trackme.domain.usecase.track

import com.example.trackme.domain.common.EmptyParams
import com.example.trackme.domain.model.TrackLocation
import com.example.trackme.domain.repository.TrackLocationRepository
import com.example.trackme.domain.usecase.UseCase
import io.reactivex.Single
import javax.inject.Inject

class GetLastTrackLocationUseCase @Inject constructor(
    private val trackLocationRepository: TrackLocationRepository
) : UseCase<EmptyParams, Single<TrackLocation>>() {
    override fun createObservable(params: EmptyParams?): Single<TrackLocation> {
        return trackLocationRepository.getLastTrackLocation()
    }

}