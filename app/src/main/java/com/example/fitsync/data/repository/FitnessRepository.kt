package com.example.fitsync.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class FitnessRepository(private val context: Context) {
    private var cumulativeCalories: Float = 0f
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private var lastValidLocation: Location? = null
    private var isActuallyMoving = false
    private var totalDuration:Long = 0
    private var trackingStartTime:Long  = 0
    private var MOVEMENT_THRESHOLD = 3.0f
    private val _routePoints = MutableLiveData<List<LatLng>>(emptyList())
    val routePoints: LiveData<List<LatLng>> = _routePoints
    private val _totalDistance = MutableLiveData<Float>(0f)
    val totalDistance: LiveData<Float> = _totalDistance
    private val _isTracking = MutableLiveData<Boolean>(false)
    val isTracking: LiveData<Boolean> = _isTracking
    private val _currentLocation = MutableLiveData<LatLng?>(null)
    val currentLocation: LiveData<LatLng?> = _currentLocation
    private var startTime: Long = 0
    private var activeMovementTime: Long = 0
    private var lastMovementTime:Long = 0
    private var lastLocationUpdateTime: Long = 0

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { newLocation ->
                Log.d(
                    "FitnessRepository",
                    "New location received: ${newLocation.latitude}, ${newLocation.longitude}"
                )
                val currentTime: Long = System.currentTimeMillis()
                _currentLocation.postValue(LatLng(newLocation.latitude, newLocation.longitude))

                if (newLocation.accuracy > 20f) {
                    Log.d("FitnessRepository", "Location accuracy too poor")
                    isActuallyMoving = false
                    return
                }
                lastValidLocation?.let { lastLocation ->
                    val distance: Float = newLocation.distanceTo(lastLocation)
                    val timeGap = currentTime - lastLocationUpdateTime
                    val speed = if (timeGap > 0) (distance * 1000) / timeGap else 0f

                    isActuallyMoving = distance > MOVEMENT_THRESHOLD && speed < 8f && speed > 0.5f
                    if (isActuallyMoving) {
                        updateMovementTime(currentTime)
                        updateLocationData(newLocation)
                        Log.d(
                            "FitnessRepository",
                            "Valid movement: $distance meters, Speed: $speed m/s"
                        )
                    }
                } ?: run {
                    lastValidLocation = newLocation
                    isActuallyMoving = false
                }
                lastLocationUpdateTime = currentTime
            }
        }
    }

    private fun updateMovementTime(currentTime: Long) {
        if (lastMovementTime > 0) {
            activeMovementTime += currentTime - lastMovementTime
        }
        lastMovementTime = currentTime
    }

//    fun getCurrentDuration(): Long {
//        return if (_isTracking.value == true) {
//            System.currentTimeMillis() - trackingStartTime
//        } else {
//            totalDuration
//        }
//    }

    private fun updateLocationData(newLocation: Location) {
        if (!isActuallyMoving) {
            Log.d("FitnessRepository", "Not Actually Moving")
            return
        }
        val latling = LatLng(newLocation.latitude, newLocation.longitude)
        val currentPoints = _routePoints.value?.toMutableList() ?: mutableListOf()
        if (currentPoints.isEmpty()) {
            currentPoints.add(latling)
            _routePoints.postValue(currentPoints)
            Log.d("FitnessRepository", "First point added")
            return
        }
        val distanceFromLastPoint = calculateDistance(currentPoints.last(), latling)
        if (distanceFromLastPoint > MOVEMENT_THRESHOLD / 1000f) {
            currentPoints.add(latling)
            _routePoints.postValue(currentPoints)

            val currentTotal = totalDistance.value ?: 0f
            val newTotal = currentTotal + distanceFromLastPoint
            _totalDistance.postValue(newTotal)
            Log.d("FitnessRepository", "Point added")
        }
        lastValidLocation = newLocation
    }

    private fun initializeTracking() {
        startTime = System.currentTimeMillis()
        trackingStartTime = System.currentTimeMillis()
        _isTracking.postValue(true)
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            fastestInterval = 500
            smallestDisplacement = 1f
        }
        if (checkLocationPermission()) {
            try {
                locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
            } catch (e: SecurityException) {
                Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )== PackageManager.PERMISSION_GRANTED
    }

    private fun calculateDistance(last: LatLng, latling: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            last.latitude, last.longitude,
            latling.latitude, latling.longitude,
            results
        )
        return results[0] / 1000f // Convert meters to kilometers
    }
    fun calculateCalories(weight:Float, distance:Float, duration:Long): Float{
        if(duration<1000) return cumulativeCalories

        val hours = duration / (1000.0 * 60.0 * 60.0)
        if(hours<=0)    return cumulativeCalories

        val speed = if(hours>0) distance/hours else 0.0
        val met = when {
            speed <= 4.0 ->2.0
            speed <= 8.0 ->7.0
            speed <= 11.0 -> 8.5
            else -> 10.0
        }.toFloat()

        if (isActuallyMoving) {
            cumulativeCalories = (met * weight * hours).toFloat()
        }
        return cumulativeCalories
    }

    fun calculatePace(distance: Float, duration: Long): Double{
        if(duration<1000 || distance<=0){
            return 0.0
        }
        val hours = duration / (1000.0 * 60.0 * 60.0)
        return distance/hours
    }
    fun startTracking(){
        if(checkLocationPermission()){
            initializeTracking()
            requestLocationUpdates()
        }
    }

    fun stopTracking(){
        _isTracking.postValue(false)
        locationClient.removeLocationUpdates(locationCallback)
        totalDuration = 0
        resetTimers()
    }

    fun pauseTracking(){
        _isTracking.postValue(false)
        locationClient.removeLocationUpdates(locationCallback)
    }

    fun resumeTracking(){
        if(checkLocationPermission()){
            _isTracking.postValue(true)
            requestLocationUpdates()
        }
    }

    private fun resetTimers(){
        lastMovementTime = 0
        activeMovementTime =0
        trackingStartTime = 0
        totalDuration = 0
    }

    fun clearTracking() {
        _routePoints.postValue(emptyList())
        _totalDistance.postValue(0f)
        _isTracking.postValue(false)
        startTime=0
        resetTimers()
        lastValidLocation = null
        isActuallyMoving = false
        cumulativeCalories = 0f
    }
}