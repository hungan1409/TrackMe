package com.example.trackme.di.builder

import com.example.trackme.MainActivity
import com.example.trackme.ui.main.MainFragment
import com.example.trackme.ui.track.TrackLocationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeTrackLocationFragment(): TrackLocationFragment
}
