package com.example.trackme.ui.track

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationEvent @Inject constructor() {

    private val _event: MutableLiveData<Location> = MutableLiveData()

    val event: LiveData<Location> = _event

    fun update(location: Location) {
        _event.postValue(location)
    }

    private val _saveLocationDone: MutableLiveData<Boolean> = MutableLiveData()

    val saveLocationDone: LiveData<Boolean> = _saveLocationDone

    fun saveLocationDone(success: Boolean) {
        _saveLocationDone.postValue(success)
    }
}
