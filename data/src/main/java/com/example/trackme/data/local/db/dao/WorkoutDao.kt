package com.example.trackme.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trackme.data.model.WorkoutRecordEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WorkoutDao {
    @Insert
    fun insertWorkoutRecordEntity(workoutRecordEntity: WorkoutRecordEntity): Completable

    @Query("SELECT * FROM workout_record_table ORDER BY date DESC LIMIT :pagingSize OFFSET :from")
    fun getWorkoutRecordEntityList(from: Int, pagingSize: Int): Single<List<WorkoutRecordEntity>>
}