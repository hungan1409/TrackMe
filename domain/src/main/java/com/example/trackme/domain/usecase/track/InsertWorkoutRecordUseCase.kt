package com.example.trackme.domain.usecase.track

import com.example.trackme.domain.Constants
import com.example.trackme.domain.model.WorkoutRecord
import com.example.trackme.domain.repository.WorkoutRepository
import com.example.trackme.domain.usecase.UseCase
import io.reactivex.Completable
import javax.inject.Inject

open class InsertWorkoutRecordUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : UseCase<InsertWorkoutRecordUseCase.Params, Completable>() {

    override fun createObservable(params: Params?): Completable {
        return when (params) {
            null -> Completable.error(Throwable(Constants.PARAMS_ERROR_MSG))
            else -> workoutRepository.insertWorkoutRecord(params.workoutRecord)
        }
    }

    data class Params(
        val workoutRecord: WorkoutRecord
    )
}