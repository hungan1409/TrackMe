package com.example.trackme.ui.track

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.example.trackme.domain.usecase.track.InsertTrackLocationUseCase
import com.example.trackme.mapper.TrackLocationItemMapper
import com.example.trackme.util.RxUtils
import com.google.android.gms.location.LocationResult
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

class TrackLocationUpdateBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var insertTrackLocationUseCase: InsertTrackLocationUseCase

    @Inject
    lateinit var trackLocationItemMapper: TrackLocationItemMapper

    @Inject
    lateinit var locationEvent: LocationEvent

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == ACTION_PROCESS_UPDATES) {
            val result = LocationResult.extractResult(intent)
            if (result != null) {
                val locations = result.locations
                Timber.i("Location update at broadcast ${locations.last().time}  position (${locations.last().latitude}, ${locations.last().longitude})")

                val trackLocation = trackLocationItemMapper.mapToTrackLocation(locations.last())
                insertTrackLocationUseCase
                    .createObservable(
                        params = InsertTrackLocationUseCase.Params(trackLocation)
                    )
                    .compose(RxUtils.applyCompletableScheduler())
                    .subscribe({
                        Timber.i("Saved location(${locations.last().latitude}, ${locations.last().longitude})")
                        locationEvent.saveLocationDone(true)
                    }, { throwable ->
                        Timber.e(throwable)
                        locationEvent.saveLocationDone(false)
                    })
            }
        }
    }

    companion object {
        private const val ACTION_PROCESS_UPDATES =
            "com.example.trackme.ui.track.action.PROCESS_UPDATES"

        fun newIntent(context: Context): Intent {
            return Intent(context, TrackLocationUpdateBroadcastReceiver::class.java).apply {
                action = ACTION_PROCESS_UPDATES
            }
        }
    }
}
