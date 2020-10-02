package com.example.trackme.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.trackme.data.model.TrackLocationEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TrackLocationDao {

    @Insert
    fun insertTrackLocationEntity(trackLocationEntity: TrackLocationEntity): Completable

    @Query("SELECT * FROM track_location_table")
    fun getTrackLocationEntityList(): Single<List<TrackLocationEntity>>

    @Query("SELECT * FROM track_location_table ORDER BY trackID DESC LIMIT 1")
    fun getLastTrackLocation(): Single<TrackLocationEntity>

    @Query("DELETE FROM track_location_table")
    fun clearAllTrackLocation(): Completable
}
