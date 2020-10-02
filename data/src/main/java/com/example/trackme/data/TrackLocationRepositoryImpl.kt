package com.example.trackme.data

import com.example.trackme.data.local.db.dao.TrackLocationDao
import com.example.trackme.data.mapper.TrackLocationEntityMapper
import com.example.trackme.domain.model.TrackLocation
import com.example.trackme.domain.repository.TrackLocationRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackLocationRepositoryImpl @Inject constructor(
    private val trackLocationDao: TrackLocationDao,
    private val trackLocationEntityMapper: TrackLocationEntityMapper
) : TrackLocationRepository {
    override fun insertTrackLocation(trackLocation: TrackLocation): Completable {
        val trackLocationEntity = trackLocationEntityMapper.mapToEntity(trackLocation)
        return trackLocationDao.insertTrackLocationEntity(trackLocationEntity)
    }

    override fun getTrackLocationList(): Single<List<TrackLocation>> {
        return trackLocationDao.getTrackLocationEntityList().map { trackLocationEntityList ->
            trackLocationEntityList.map { trackLocationEntityMapper.mapToDomain(it) }
        }
    }

    override fun getLastTrackLocation(): Single<TrackLocation> {
        return trackLocationDao.getLastTrackLocation().map { trackLocation ->
            trackLocationEntityMapper.mapToDomain(trackLocation)
        }
    }

    override fun clearAllTrackLocation(): Completable {
        return trackLocationDao.clearAllTrackLocation()
    }
}
