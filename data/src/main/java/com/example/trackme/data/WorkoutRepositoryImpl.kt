package com.example.trackme.data

import com.example.trackme.data.local.db.dao.WorkoutDao
import com.example.trackme.data.mapper.WorkoutRecordMapper
import com.example.trackme.domain.model.WorkoutRecord
import com.example.trackme.domain.repository.WorkoutRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutRecordMapper: WorkoutRecordMapper
) : WorkoutRepository {
    override fun insertWorkoutRecord(workoutRecord: WorkoutRecord): Completable {
        return workoutDao.insertWorkoutRecordEntity(workoutRecordMapper.mapToEntity(workoutRecord))
    }

    override fun getWorkoutRecordList(
        pagingIndex: Int,
        pagingSize: Int
    ): Single<List<WorkoutRecord>> {
        val from = (pagingIndex - 1) * pagingSize
        return workoutDao.getWorkoutRecordEntityList(
            from = from, pagingSize = pagingSize
        )
            .map { workoutRecordEntityList ->
                workoutRecordEntityList.map { workoutRecordEntity ->
                    workoutRecordMapper.mapToDomain(workoutRecordEntity)
                }
            }
    }
}