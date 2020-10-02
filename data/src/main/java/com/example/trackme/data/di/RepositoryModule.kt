package com.example.trackme.data.di

import android.content.Context
import androidx.room.Room
import com.example.trackme.data.AppPrefs
import com.example.trackme.data.Constants
import com.example.trackme.data.TrackLocationRepositoryImpl
import com.example.trackme.data.WorkoutRepositoryImpl
import com.example.trackme.data.local.db.AppDatabase
import com.example.trackme.data.local.db.dao.TrackLocationDao
import com.example.trackme.data.local.db.dao.WorkoutDao
import com.example.trackme.domain.PrefsHelper
import com.example.trackme.domain.repository.TrackLocationRepository
import com.example.trackme.domain.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String {
        return Constants.DATABASE_NAME
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@DatabaseInfo dbName: String, context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(appDatabase: AppDatabase): WorkoutDao {
        return appDatabase.workoutDao()
    }

    @Provides
    @Singleton
    fun provideTrackLocationDao(appDatabase: AppDatabase): TrackLocationDao {
        return appDatabase.trackLocationDao()
    }

    @Provides
    @Singleton
    fun providePrefsHelper(appPrefs: AppPrefs): PrefsHelper {
        return appPrefs
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(repository: WorkoutRepositoryImpl): WorkoutRepository {
        return repository
    }

    @Provides
    @Singleton
    fun provideTrackLocationRepository(repository: TrackLocationRepositoryImpl): TrackLocationRepository {
        return repository
    }
}
