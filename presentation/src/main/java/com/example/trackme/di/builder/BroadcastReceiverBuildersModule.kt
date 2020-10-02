package com.example.trackme.di.builder

import com.example.trackme.ui.track.TrackLocationUpdateBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("Unused")
@Module
abstract class BroadcastReceiverBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeTrackLocationUpdateBroadcastReceiver(): TrackLocationUpdateBroadcastReceiver
}