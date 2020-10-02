package com.example.trackme.ui.track

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.trackme.base.BaseViewModel
import com.example.trackme.domain.usecase.track.ClearAllTrackLocationUseCase
import com.example.trackme.domain.usecase.track.GetTrackLocationListUseCase
import com.example.trackme.domain.usecase.track.InsertWorkoutRecordUseCase
import com.example.trackme.mapper.TrackLocationItemMapper
import com.example.trackme.mapper.WorkoutRecordItemMapper
import com.example.trackme.model.TrackLocationItem
import com.example.trackme.model.WorkoutRecordItem
import com.example.trackme.util.RxUtils
import com.example.trackme.util.SingleLiveData
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TrackLocationViewModel @Inject constructor(
    private val insertWorkoutUseCase: InsertWorkoutRecordUseCase,
    private val getTrackLocationListUseCase: GetTrackLocationListUseCase,
    private val clearAllTrackLocationUseCase: ClearAllTrackLocationUseCase,
    private val workoutRecordItemMapper: WorkoutRecordItemMapper,
    private val trackLocationItemMapper: TrackLocationItemMapper,
    private val locationEvent: LocationEvent
) : BaseViewModel() {

    val locationUpdated = locationEvent.saveLocationDone

    val trackLocationItemList = MutableLiveData<List<TrackLocationItem>>()

    val distance = MutableLiveData<Float>(0f)
    val speed = MutableLiveData<Float>(0f)
    val totalTime = MutableLiveData<String>("00:00:00")
    var saveTime: Long = 0

    val isRecording = MutableLiveData<Boolean>(false)

    val pauseWorkout = SingleLiveData<Unit>()
    val resumeWorkout = SingleLiveData<Unit>()
    val stopWorkout = SingleLiveData<Unit>()
    val saveRecordSuccess = SingleLiveData<Boolean>()
    val clearTrackSuccess = SingleLiveData<Boolean>()

    fun insertWorkRecord(bitmap: Bitmap, points: List<TrackLocationItem>, averageSpeed: Float) {
        val workoutRecordItem = WorkoutRecordItem(
            distance = distance.value!!,
            totalTime = totalTime.value!!,
            points = points,
            speed = averageSpeed,
            bitmap = bitmap,
            date = Date()
        )
        addDisposable(
            insertWorkoutUseCase.createObservable(
                InsertWorkoutRecordUseCase.Params(
                    workoutRecord = workoutRecordItemMapper.mapToDomain(workoutRecordItem)
                )
            ).compose(
                RxUtils.applyCompletableScheduler()
            ).subscribe({
                Timber.d("InsertWorkoutUseCase: success")
                saveRecordSuccess.value = true
            }, {
                Timber.d("InsertWorkoutUseCase: " + it.message)
                setThrowable(it)
            })
        )
    }

    fun getTrackLocationItemList() {
        // reset status save location to false
        locationEvent.saveLocationDone(false)
        addDisposable(
            getTrackLocationListUseCase.createObservable().compose(
                RxUtils.applySingleScheduler()
            ).subscribe({ trackLocationList ->
                trackLocationItemList.value = trackLocationList.map { trackLocation ->
                    trackLocationItemMapper.mapToPresentation(trackLocation)
                }
            }, { throwable ->
                Timber.e(throwable)
            })
        )
    }

    fun clearAllTrackLocation() {
        addDisposable(
            clearAllTrackLocationUseCase.createObservable().compose(
                RxUtils.applyCompletableScheduler()
            ).subscribe({
                clearTrackSuccess.value = true
            }, { throwable ->
                Timber.e(throwable)
            })
        )
    }

    fun setTotalTime(timeString: String) {
        totalTime.postValue(timeString)
    }

    fun setCurrentSpeed(currentSpeed: Float) {
        speed.postValue(currentSpeed)
    }

    fun setDistance(totalDistance: Float) {
        distance.postValue(totalDistance)
    }

    fun setRecording(isRecord: Boolean) {
        isRecording.postValue(isRecord)
    }

    fun getRecordingStatus(): Boolean {
        return isRecording.value!!
    }

    fun pauseWorkout() {
        pauseWorkout.call()
        isRecording.value = false
    }

    fun resumeWorkout() {
        resumeWorkout.call()
        isRecording.value = true
    }

    fun stopWorkout() {
        stopWorkout.call()
        isRecording.value = false
    }
}
