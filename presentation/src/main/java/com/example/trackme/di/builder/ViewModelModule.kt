package com.example.trackme.di.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.ViewModelProviderFactory
import com.example.trackme.common.LocationPermissionViewModel
import com.example.trackme.di.annotation.ViewModelKey
import com.example.trackme.ui.main.MainViewModel
import com.example.trackme.ui.track.TrackLocationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(providerFactory: ViewModelProviderFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocationPermissionViewModel::class)
    abstract fun bindLocationPermissionViewModel(viewModel: LocationPermissionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrackLocationViewModel::class)
    abstract fun bindTrackLocationViewModel(viewModel: TrackLocationViewModel): ViewModel
}
