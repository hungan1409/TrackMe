package com.example.trackme.mapper

import com.example.trackme.base.ItemMapper
import com.example.trackme.domain.model.WorkoutRecord
import com.example.trackme.extension.string
import com.example.trackme.extension.toBitmap
import com.example.trackme.model.WorkoutRecordItem
import javax.inject.Inject

open class WorkoutRecordItemMapper @Inject constructor(
    private val trackLocationItemMapper: TrackLocationItemMapper
) : ItemMapper<WorkoutRecord, WorkoutRecordItem> {
    override fun mapToPresentation(model: WorkoutRecord): WorkoutRecordItem = WorkoutRecordItem(
        workoutID = model.workoutID,
        distance = model.distance,
        speed = model.speed,
        totalTime = model.totalTime,
        date = model.date,
        points = model.points.map {
            trackLocationItemMapper.mapToPresentation(it)
        },
        bitmap = model.imageData.toBitmap()
    )

    override fun mapToDomain(modelItem: WorkoutRecordItem) = WorkoutRecord(
        workoutID = modelItem.workoutID,
        distance = modelItem.distance,
        speed = modelItem.speed,
        totalTime = modelItem.totalTime,
        date = modelItem.date,
        points = modelItem.points.map {
            trackLocationItemMapper.mapToDomain(it)
        },
        imageData = modelItem.bitmap?.string().orEmpty()
    )
}