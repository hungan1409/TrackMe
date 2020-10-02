package com.example.trackme.ui.track

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.trackme.R
import com.example.trackme.common.LocationPermissionFragment
import com.example.trackme.databinding.FragmentTrackLocationBinding
import com.example.trackme.model.TrackLocationItem
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import timber.log.Timber


class TrackLocationFragment :
    LocationPermissionFragment<FragmentTrackLocationBinding, TrackLocationViewModel>(),
    OnMapReadyCallback {

    override val layoutId = R.layout.fragment_track_location

    override val viewModel: TrackLocationViewModel by viewModels { viewModelFactory }

    companion object {
        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        /**
         * The max time before batched results are delivered by location services. Results may be
         * delivered sooner than this interval.
         */
        private val MAX_WAIT_TIME: Long = UPDATE_INTERVAL_IN_MILLISECONDS * 2

        /**
         * Distance min between two points for checking update location
         */
        private val MIN_DISTANCE_BETWEEN_TWO_POINTS = 10

        /**
         * Max time for count down
         */
        private val MAX_TIME_COUNT_DOWN: Long = 3600 * 24 * 365
    }

    /**
     * Constant used in the location settings dialog.
     */
    private val REQUEST_CHECK_SETTINGS = 0x1

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    lateinit var mLocationRequest: LocationRequest

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    private var googleMap: GoogleMap? = null

    // save the timer
    private var timerGo: Long = 0

    private var timer: CountDownTimer? = null

    private var mLocationList: MutableList<TrackLocationItem> = mutableListOf()

    private val callbackScreenShort = GoogleMap.SnapshotReadyCallback { bitmap ->
        viewModel.insertWorkRecord(bitmap, mLocationList, calculateAverageSpeed())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    removeLocationUpdates()
                    stopCountTimer()
                    findNavController().navigateUp()
                }
            })
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        subscribeUI()
        requestFineLocationPermission()
    }

    override fun onStop() {
        stopCountTimer()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.getRecordingStatus()) {
            if (viewModel.saveTime > 0) {
                timerGo += (SystemClock.elapsedRealtime() - viewModel.saveTime) / 1000
                viewModel.saveTime = 0
            }
            startCountTimer()
        }
    }

    override fun onLocationPermissionGranted(perms: List<String>) {
        super.onLocationPermissionGranted(perms)
        if (hasAccessFineLocationPermission() && !hasAccessBackgroundLocationPermission()) {
            requestBackgroundLocationPermission()
        }
        if (hasAccessBackgroundLocationPermission()) {
            // first clear all data before after that start tracking
            viewModel.clearAllTrackLocation()
        }
    }

    override fun onRationaleDenied(requestCode: Int) {
        super.onRationaleDenied(requestCode)
        requireActivity().finish()
    }

    private fun initLocationSetting() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mSettingsClient = LocationServices.getSettingsClient(requireActivity())
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = map
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    private fun subscribeUI() {
        viewModel.apply {
            pauseWorkout.observe(viewLifecycleOwner) {
                removeLocationUpdates()
            }

            stopWorkout.observe(viewLifecycleOwner) {
                removeLocationUpdates()
                stopCountTimer()
                googleMap?.snapshot(callbackScreenShort)
            }

            resumeWorkout.observe(viewLifecycleOwner) {
                startLocationUpdates()
            }

            saveRecordSuccess.observe(viewLifecycleOwner) {
                findNavController().navigateUp()
            }

            clearTrackSuccess.observe(viewLifecycleOwner) {
                if (it == true) {
                    initLocationSetting()
                    startLocationUpdates()
                }
            }

            locationUpdated.observe(viewLifecycleOwner) {
                Timber.d("Location update trigger ${it}")
                if (it == true) {
                    getTrackLocationItemList()
                }
            }

            trackLocationItemList.observe(viewLifecycleOwner) {
                Timber.d("trackLocationItemList trigger ${it.count()}")
                if (it.count() > 0) {
                    mLocationList.clear()
                    mLocationList.addAll(it)
                    if (mLocationList.count() > 1) {
                        val previousLocation = mLocationList.get(mLocationList.count() - 2)
                        val distanceBetween =
                            calculateDistanceTwoLocations(previousLocation, mLocationList.last())
                        // check if user stand alone long time
                        Timber.d("Distance with previous location ${distanceBetween}")
                        // Check distance is greater than 10m
                        if (distanceBetween > MIN_DISTANCE_BETWEEN_TWO_POINTS) {
                            calculateTotalDistance()
                            updateDrawPathInMap()
                        }
                    } else {
                        updateDrawPathInMap()
                    }
                    //set speed and convert from m/s to km/h
                    setCurrentSpeed(mLocationList.last().speed * 3.6f)

                    Timber.i("Location update at fragment ${mLocationList.last().time}  position (${mLocationList.last().latitude}, ${mLocationList.last().longitude})")
                }
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
//        mLocationRequest.maxWaitTime = MAX_WAIT_TIME
    }

    /**
     * Uses a [com.google.android.gms.location.LocationSettingsRequest.Builder] to build
     * a [com.google.android.gms.location.LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    private fun getPendingIntent(): PendingIntent {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        val intent = TrackLocationUpdateBroadcastReceiver.newIntent(requireContext())
        return PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun requestLocationUpdates() {
        try {
            viewModel.setRecording(true)
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, getPendingIntent())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun removeLocationUpdates() {
        mFusedLocationClient?.removeLocationUpdates(getPendingIntent())
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient?.let {
            it.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener {
                    requestLocationUpdates()
                    startCountTimer()
                }
                .addOnFailureListener { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Timber.d("Location settings are not satisfied. Attempting to upgrade location settings")

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(
                                    requireActivity(),
                                    REQUEST_CHECK_SETTINGS
                                )
                            } catch (sie: SendIntentException) {
                                Timber.d("PendingIntent unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    private fun updateDrawPathInMap() {
        googleMap?.let {
            val locationLatLng =
                mLocationList.map { location -> LatLng(location.latitude, location.longitude) }

            // clear and draw again
            it.clear()

            val options = PolylineOptions().width(12f).color(Color.BLUE).geodesic(true)

            locationLatLng.forEach {
                Timber.d("Latitue ${it.latitude} , Longitue ${it.longitude}")
                options.add(it)
            }

            val polyline = it.addPolyline(options)

            //add start marker
            it.addMarker(
                MarkerOptions()
                    .position(locationLatLng.first())
                    .title("Start")
            )

            if (locationLatLng.count() > 1) {
                //add end marker
                val bitmap =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_current)
                        ?.toBitmap()

                it.addMarker(
                    MarkerOptions()
                        .position(locationLatLng.last())
                        .title("Current")
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .anchor(0.5f, 0.5f)
                )

                // and set the zoom factor so most of end points shows on the screen.
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng.last(), 12f))

            } else {
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng.first(), 12f))
            }
        }
    }

    fun calculateAverageSpeed(): Float {
        var sumSpeed = 0.0f
        mLocationList.forEach {
            sumSpeed += it.speed
        }

        return (sumSpeed * 3.6f) / mLocationList.count()
    }

    fun calculateTotalDistance() {
        if (mLocationList.count() > 1) {
            val len = mLocationList.count() - 2
            var sumDistance = 0.0
            // currently for sum from begin because when running in background some point can saved and we need to read it for calculate
            for (i in 0..len) {
                sumDistance += calculateDistanceTwoLocations(mLocationList[i], mLocationList[i + 1])
            }
            viewModel.setDistance(sumDistance.toFloat() / 1000) //convert m to km
        }
    }

    fun calculateDistanceTwoLocations(item1: TrackLocationItem, item2: TrackLocationItem): Double {
        return SphericalUtil.computeDistanceBetween(
            LatLng(item1.latitude, item1.longitude),
            LatLng(item2.latitude, item2.longitude)
        )
    }

    fun startCountTimer() {
        if (timer == null) {
            timer = object : CountDownTimer(MAX_TIME_COUNT_DOWN, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (viewModel.getRecordingStatus() == true) {
                        timerGo += 1
                        val timeString = String.format(
                            "%02d:%02d:%02d",
                            timerGo / 3600,
                            (timerGo % 3600) / 60,
                            timerGo % 60
                        )
                        viewModel.setTotalTime(timeString)
                    }
                }

                override fun onFinish() {}
            }
            timer?.start()
        }
    }

    fun stopCountTimer() {
        viewModel.saveTime = SystemClock.elapsedRealtime()
        timer?.cancel()
        timer = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Timber.d("User agreed to make required location settings changes.")
                    if (hasAccessFineLocationPermission()) {
                        startLocationUpdates()
                    } else {
                        requireActivity().finish()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Timber.d("User chose not to make required location settings changes.")
                }
            }
        }
    }

    override fun onReturnedFromAppSettings() {
        super.onReturnedFromAppSettings()
        if (!hasAccessFineLocationPermission()) {
            requireActivity().finish()
        }
    }
}
