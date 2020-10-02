package com.example.trackme.ui.main

import androidx.lifecycle.MutableLiveData
import com.example.trackme.base.BaseViewModel
import com.example.trackme.domain.usecase.track.GetWorkoutRecordListUseCase
import com.example.trackme.mapper.WorkoutRecordItemMapper
import com.example.trackme.model.WorkoutRecordItem
import com.example.trackme.util.RxUtils
import com.example.trackme.util.SingleLiveData
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getWorkoutRecordListUseCase: GetWorkoutRecordListUseCase,
    private val workoutRecordItemMapper: WorkoutRecordItemMapper
) : BaseViewModel() {
    var currentPage = 1
    private val currentList = mutableListOf<WorkoutRecordItem>()
    private var nextPage = -1


    val workouts = SingleLiveData<List<WorkoutRecordItem>>()

    val isRefresh = MutableLiveData<Boolean>().apply { value = false }

    val record = SingleLiveData<Unit>()

    fun init() {
        resetAllVariable()
        if (currentList.size == 0) {
            getWorkoutList()
        }
    }

    private fun resetAllVariable() {
        nextPage = -1
        currentPage = 1
        workouts.value = emptyList()
        currentList.clear()
        currentList.clear()
        isRefresh.value = false
    }

    fun refresh() {
        isRefresh.value = true
        nextPage = -1
        currentPage = 1
        workouts.value = emptyList()
        clearToRefresh()
        init()
    }

    fun loadMore() {
        if (currentList.size >= currentPage * PAGE_SIZE) {
            getWorkoutList(page = nextPage)
        }
    }

    private fun getWorkoutList(page: Int = FIRST_PAGE) {
        currentPage = page
        nextPage = currentPage + 1
        Timber.d("InsertWorkoutUseCase:page $currentPage - $nextPage")
        addDisposable(
            getWorkoutRecordListUseCase.createObservable(
                GetWorkoutRecordListUseCase.Params(
                    pageIndex = page,
                    pageSize = PAGE_SIZE
                )
            ).compose(
                RxUtils.applySingleScheduler()
            ).map {
                it.map { workRecord ->
                    workoutRecordItemMapper.mapToPresentation(workRecord)
                }
            }.subscribe({
                val listData = mutableListOf<WorkoutRecordItem>()
                val dataTemp = workouts.value ?: emptyList()
                listData.addAll(dataTemp)
                listData.addAll(it)
                currentList.clear()
                currentList.addAll(listData)
                workouts.value = listData
                Timber.d("InsertWorkoutUseCase: data ${listData.size}")
            }, {
                Timber.d("InsertWorkoutUseCase: error " + it.message)
            })
        )
    }

    private fun clearToRefresh() {
        if (isRefresh.value == true) {
            resetAllVariable()
        }
    }

    fun recordWorkout() {
        record.call()
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val PAGE_SIZE = 30
    }
}
