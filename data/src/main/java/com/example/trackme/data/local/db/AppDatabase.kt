package com.example.trackme.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trackme.data.TrackDatabase
import com.example.trackme.data.local.db.dao.TrackLocationDao
import com.example.trackme.data.local.db.dao.WorkoutDao
import com.example.trackme.data.model.TrackLocationEntity
import com.example.trackme.data.model.WorkoutRecordEntity

@Database(
    entities = [WorkoutRecordEntity::class, TrackLocationEntity::class],
    version = TrackDatabase.VERSION_DATABASE,
    exportSchema = false
)
@TypeConverters(TrackTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun trackLocationDao(): TrackLocationDao
}