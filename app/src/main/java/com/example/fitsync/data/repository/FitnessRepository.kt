package com.example.fitsync.data.repository

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class FitnessRepository(private val context: Context) {
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private var lastValidLocation: Location? = null
    private var isActuallyMoving = false
    private var totalDuration:Long = 0
    private var trackingStartTime:Long = 0
    private var MOVEMENT_THRESHOLD = 1.0f
    private val _routePoints = MutableLiveData<List<LatLng>>(emptyList())
    private val routePoints: LiveData<List<LatLng>> = _routePoints
    private val _totalDistance = MutableLiveData<Float>(0f)
    private val totalDistance: LiveData<Float> = _totalDistance
    private val _isTracking = MutableLiveData<Boolean>(false)
    private val isTracking: LiveData<Boolean> = _isTracking
    private val _currentLocation = MutableLiveData<LatLng?>(null)
    private val currentLocation: LiveData<LatLng?> = _currentLocation
    private var startTime: Long = 0
    private var activeMovementTime: Long = 0
    private var lastMovementTime:Long = 0
    private var lastLocationUpdateTime: Long = 0

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                newLocation ->
                Log.d("FitnessRepository", "New location received: ${newLocation.latitude}, ${newLocation.longitude}")
                val currentTime: Long = System.currentTimeMillis()
                _currentLocation.postValue(LatLng(newLocation.latitude, newLocation.longitude))

                if(newLocation.accuracy > 20f){
                    Log.d("FitnessRepository", "Location accuracy too poor")
                    isActuallyMoving = false
                    return
                }
                lastValidLocation?.let{
                    lastLocation ->
                    val distance: Float = newLocation.distanceTo(lastLocation)
                    val timeGap = currentTime - lastLocationUpdateTime
                    val speed = if(timeGap > 0) (distance*1000) / timeGap else 0f

                    isActuallyMoving = distance > MOVEMENT_THRESHOLD && speed < 8f && speed > 0.3f
                    if(isActuallyMoving){
                        updateMovementTime(currentTime)
                        updateLocationData(newLocation)
                        Log.d("FitnessRepository", "Valid movement: $distance meters, Speed: $speed m/s")
                    }
                }?:run{
                    lastValidLocation=newLocation
                    isActuallyMoving = false
                }
                lastLocationUpdateTime = currentTime
            }
        }

        private fun updateMovementTime(currentTime: Long) {
            if(lastMovementTime>0){
                activeMovementTime += currentTime - lastMovementTime
            }
            lastMovementTime = currentTime
        }

        fun gerCurentDuration(): Long {
            return if(_isTracking.value == true){
                System.currentTimeMillis() - trackingStartTime
            }else{
                totalDuration
            }
        }

        private fun updateLocationData(newLocation: Location) {}
    }
}