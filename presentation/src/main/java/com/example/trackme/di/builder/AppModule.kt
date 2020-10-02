package com.example.trackme.di.builder

import android.content.Context
import com.example.trackme.MainApplication
import com.example.trackme.data.di.NetworkModule
import com.example.trackme.data.di.RepositoryModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class,
        FragmentBuildersModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        BroadcastReceiverBuildersModule::class
    ]
)
class AppModule {

    @Singleton
    @Provides
    fun providerContext(application: MainApplication): Context {
        return application.applicationContext
    }
}
