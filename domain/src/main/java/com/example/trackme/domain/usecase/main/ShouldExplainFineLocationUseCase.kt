package com.example.trackme.domain.usecase.main

import com.example.trackme.domain.PrefsHelper
import com.example.trackme.domain.common.EmptyParams
import com.example.trackme.domain.usecase.UseCase
import javax.inject.Inject

class ShouldExplainFineLocationUseCase @Inject constructor(
    private val prefsHelper: PrefsHelper
) : UseCase<EmptyParams, Boolean>() {
    override fun createObservable(params: EmptyParams?): Boolean {
        return prefsHelper.shouldExplainFineLocation()
    }
}