package com.example.trackme.data

import android.content.Context
import com.example.trackme.domain.PrefsHelper
import javax.inject.Inject

class AppPrefs @Inject constructor(
    context: Context
) : PrefsHelper {

    private val sharedPreferences = context.getSharedPreferences(
        context.packageName,
        Context.MODE_PRIVATE
    )

    override fun shouldExplainFineLocation(): Boolean {
        if (!sharedPreferences.getBoolean(EXPLAINED_FINE_LOCATION, false)) {
            sharedPreferences.edit()
                .putBoolean(EXPLAINED_FINE_LOCATION, true)
                .apply()
            return true
        }
        return false
    }

    override fun shouldExplainBackgroundLocation(): Boolean {
        if (!sharedPreferences.getBoolean(EXPLAINED_BACKGROUND_LOCATION, false)) {
            sharedPreferences.edit()
                .putBoolean(EXPLAINED_BACKGROUND_LOCATION, true)
                .apply()
            return true
        }
        return false
    }

    companion object {

        const val EXPLAINED_FINE_LOCATION = "explained_fine_location"
        const val EXPLAINED_BACKGROUND_LOCATION = "explained_background_location"
    }
}