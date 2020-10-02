package com.example.trackme.data.mapper

import com.example.trackme.data.base.EntityMapper
import com.example.trackme.data.model.WorkoutRecordEntity
import com.example.trackme.domain.model.WorkoutRecord
import javax.inject.Inject

class WorkoutRecordMapper @Inject constructor(
    private val trackLocationEntityMapper: TrackLocationEntityMapper
) : EntityMapper<WorkoutRecord, WorkoutRecordEntity> {
    override fun mapToDomain(entity: WorkoutRecordEntity) = WorkoutRecord(
        workoutID = entity.workoutID,
        distance = entity.distance,
        speed = entity.speed,
        totalTime = entity.totalTime,
        date = entity.date,
        points = entity.points.map {
            trackLocationEntityMapper.mapToDomain(it)
        },
        imageData = entity.imageData
    )

    override fun mapToEntity(model: WorkoutRecord) = WorkoutRecordEntity(
        workoutID = model.workoutID,
        distance = model.distance,
        speed = model.speed,
        totalTime = model.totalTime,
        date = model.date,
        points = model.points.map {
            trackLocationEntityMapper.mapToEntity(it)
        },
        imageData = model.imageData
    )
}
