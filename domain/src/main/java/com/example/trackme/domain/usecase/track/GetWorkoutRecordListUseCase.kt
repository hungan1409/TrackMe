package com.example.trackme.domain.usecase.track

import com.example.trackme.domain.Constants
import com.example.trackme.domain.model.WorkoutRecord
import com.example.trackme.domain.repository.WorkoutRepository
import com.example.trackme.domain.usecase.UseCase
import io.reactivex.Single
import javax.inject.Inject

open class GetWorkoutRecordListUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : UseCase<GetWorkoutRecordListUseCase.Params, Single<List<WorkoutRecord>>>() {

    override fun createObservable(params: Params?): Single<List<WorkoutRecord>> {
        return when (params) {
            null -> Single.error { Throwable(Constants.PARAMS_ERROR_MSG) }
            else -> workoutRepository.getWorkoutRecordList(
                pagingIndex = params.pageIndex,
                pagingSize = params.pageSize
            )
        }
    }

    data class Params(
        val pageIndex: Int,
        val pageSize: Int
    )
}