package com.example.trackme.domain.usecase.track

import com.example.trackme.domain.Constants
import com.example.trackme.domain.model.TrackLocation
import com.example.trackme.domain.repository.TrackLocationRepository
import com.example.trackme.domain.usecase.UseCase
import io.reactivex.Completable
import javax.inject.Inject

class InsertTrackLocationUseCase @Inject constructor(
    private val trackLocationRepository: TrackLocationRepository
) : UseCase<InsertTrackLocationUseCase.Params, Completable>() {
    override fun createObservable(params: Params?): Completable {
        return when (params) {
            null -> Completable.error { Throwable(Constants.PARAMS_ERROR_MSG) }
            else -> trackLocationRepository.insertTrackLocation(params.trackLocation)
        }
    }

    data class Params(
        val trackLocation: TrackLocation
    )
}