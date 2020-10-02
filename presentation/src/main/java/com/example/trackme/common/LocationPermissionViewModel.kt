package com.example.trackme.common

import com.example.trackme.base.BaseViewModel
import com.example.trackme.domain.usecase.main.ShouldExplainBackgroundLocationUseCase
import com.example.trackme.domain.usecase.main.ShouldExplainFineLocationUseCase
import javax.inject.Inject

class LocationPermissionViewModel @Inject constructor(
    private val shouldExplainFineLocationUseCase: ShouldExplainFineLocationUseCase,
    private val shouldExplainBackgroundLocationUseCase: ShouldExplainBackgroundLocationUseCase
) : BaseViewModel() {

    fun shouldExplainFineLocation(): Boolean {
        return shouldExplainFineLocationUseCase.createObservable()
    }

    fun shouldExplainBackgroundLocation(): Boolean {
        return shouldExplainBackgroundLocationUseCase.createObservable()
    }
}