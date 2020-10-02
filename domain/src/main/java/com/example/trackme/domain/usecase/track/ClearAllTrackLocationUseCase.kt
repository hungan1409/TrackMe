package com.example.trackme.domain.usecase.track

import com.example.trackme.domain.common.EmptyParams
import com.example.trackme.domain.repository.TrackLocationRepository
import com.example.trackme.domain.usecase.UseCase
import io.reactivex.Completable
import javax.inject.Inject

class ClearAllTrackLocationUseCase @Inject constructor(
    private val trackLocationRepository: TrackLocationRepository
) : UseCase<EmptyParams, Completable>() {
    override fun createObservable(params: EmptyParams?): Completable {
        return trackLocationRepository.clearAllTrackLocation()
    }
}