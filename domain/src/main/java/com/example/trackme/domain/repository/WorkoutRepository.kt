package com.example.trackme.domain.repository

import com.example.trackme.domain.model.WorkoutRecord
import io.reactivex.Completable
import io.reactivex.Single

interface WorkoutRepository : Repository {
    fun insertWorkoutRecord(workoutRecord: WorkoutRecord): Completable
    fun getWorkoutRecordList(
        pagingIndex: Int,
        pagingSize: Int
    ): Single<List<WorkoutRecord>>
}