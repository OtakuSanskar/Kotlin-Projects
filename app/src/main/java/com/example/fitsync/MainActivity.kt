package com.example.fitsync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fitsync.R
import com.example.fitsync.data.repository.FitnessRepository
import com.example.fitsync.databinding.ActivityMainBinding
import com.example.fitsync.ui.ViewModel.FitnessViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MainActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var currentLocationMarker: Marker
    private lateinit var startStopButton: Button
    private lateinit var distanceValue: TextView
    private lateinit var caloriesValue: TextView
    private lateinit var pathPolyline: Polyline
    private var firstLocationUpdate = true
    private lateinit var durationValue: TextView
    private var isWorkoutPaused = false

    private val viewModel: FitnessViewModel by viewModels{
        FitnessViewModel.Factory(FitnessRepository(this), context = this)
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissions->
        if(permissions.all{it.value}){
            viewModel.startWorkout()
        }
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        map = binding.map
        currentLocationMarker = Marker(map)
        startStopButton = binding.actionButton
        distanceValue = binding.distanceValue
        caloriesValue = binding.caloriesValue
        pathPolyline = Polyline(map)
        durationValue = binding.durationValue
        setupMap()
        setupObservers()
        setupButtons()
        setupWeightInput()
    }
    private fun setupMap(){
        map.apply{
            setMultiTouchControls(true)
            controller.setZoom(18.0)
            setTileSource(TileSourceFactory.MAPNIK)
        }
        currentLocationMarker = Marker(map).apply{
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(this@MainActivity, org.osmdroid.library.R.drawable.osm_ic_follow_me_on)
            title = "Current Location"
        }
        map.overlays.add(currentLocationMarker)
        pathPolyline = Polyline(map).apply{
            outlinePaint.color = ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark)
            outlinePaint.strokeWidth = 10f
        }
        map.overlays.add(pathPolyline)
        startLocationUpdates()
    }

    private fun setupButtons(){
        binding.actionButton.setOnClickListener {
            if(!viewModel.isTracking.value!! && !isWorkoutPaused){
                val weight = binding.weightInput.text.toString().toFloatOrNull()
                if( weight == null || weight <= 40){
                    binding.weightInput.error = "Enter a valid weight"
                    return@setOnClickListener
                }
                checkPermissionsAndStartTracking()
                binding.pauseResumeButton.visibility = View.VISIBLE
            } else{
                stopTracking()
                binding.pauseResumeButton.visibility = View.GONE
            }
        }

        binding.pauseResumeButton.setOnClickListener {
            if(isWorkoutPaused){
                resumeTracking()
            } else{
                pauseTracking()
            }
        }
    }

    private fun setupWeightInput(){
        binding.weightInput.setOnEditorActionListener { _, _, _ ->
            val weight = binding.weightInput.text.toString().toFloatOrNull()
            if(weight != null && weight > 0){
                viewModel.updateWeight(weight)
            }
            false
        }
    }

    private fun pauseTracking(){
        isWorkoutPaused = true
        viewModel.pauseWorkout()
        binding.pauseResumeButton.text = "RESUME"
    }

    private fun resumeTracking(){
        isWorkoutPaused = false
        viewModel.resumeWorkout()
        binding.pauseResumeButton.text = "PAUSE"
    }

    private fun startTracking(){
        viewModel.startWorkout()
    }
    private fun stopTracking(){
        isWorkoutPaused = false
        binding.pauseResumeButton.visibility = View.GONE
        viewModel.stopWorkout()
    }

    private fun checkLocationPermission(): Boolean{
        return(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )== PackageManager.PERMISSION_GRANTED)
    }
    private fun startLocationUpdates(){
        if(!checkLocationPermission()) return

        val locationRequest = LocationRequest.create().apply{
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            fastestInterval = 500
        }
        val locationCallback = object: LocationCallback(){
            override fun onLocationResult(result: LocationResult){
                result.lastLocation?.let{location ->
                    val latling = LatLng(location.latitude, location.longitude)
                    updateLocationMarker(latling)
                }
            }
        }
        try{
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        } catch(e: SecurityException){
            Toast.makeText(this, "Unable to fetch location at this this", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateLocationMarker(latling: LatLng){
        val geoPoint = GeoPoint(latling.latitude, latling.longitude)
        currentLocationMarker.position=geoPoint
        if(firstLocationUpdate){
            map.controller.setZoom(20.0)
            map.controller.setCenter(geoPoint)
            map.controller.animateTo(geoPoint)
            firstLocationUpdate = false
        } else if(viewModel.isTracking.value ==true){
            map.controller.animateTo(geoPoint)
        }
        map.invalidate()
    }
    private fun setupObservers(){
        viewModel.isTracking.observe(this) { isTracking ->
            binding.actionButton.text = when {
                isWorkoutPaused -> "STOP"
                !isTracking -> "START"
                else -> "STOP"
            }
        }
        viewModel.duration.observe(this) { duration ->
            durationValue.text = viewModel.formatDuration(duration)
        }
        viewModel.routePoints.observe(this){points ->
            if(points.isNotEmpty()){
                updateRouteOnMap(points)
            }
        }
        viewModel.currentLocation.observe(this){location ->
            if(location != null){
                updateLocationMarker(location)
            }
            if (location != null) {
                Log.d("MainActivity", "Marker updated to: ${location.latitude}, ${location.longitude}")
            }
        }
        viewModel.calories.observe(this) { calories ->
            caloriesValue.text = viewModel.formatCalories(calories)
            Log.d("MainActivity", "Calories updated to: $calories")
        }
        viewModel.distance.observe(this) { distance ->
            distanceValue.text = viewModel.formatDistance(distance)
            Log.d("MainActivity", "Distance updated to: $distance")
        }
    }

    private fun checkPermissionsAndStartTracking(){
        if(checkLocationPermission()){
            startTracking()
        } else{
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun updateRouteOnMap(points: List<LatLng>){
        if(points.isEmpty()) return

        try{
            val geoPoints = points.map{GeoPoint(it.latitude, it.longitude)}
            val currentLocation = geoPoints.last()

            // Update Marker and Path
            updateLocationMarker(LatLng(currentLocation.latitude, currentLocation.longitude))
            pathPolyline.setPoints(geoPoints)

            // Keep map centered on current location
            if(viewModel.isTracking.value == true){
                map.controller.animateTo(currentLocation)
            }
            map.invalidate()
            Log.d("MainActivity", "Updated location: ${currentLocation.latitude}, ${currentLocation.longitude}")
        } catch (e: Exception){
            Log.e("MainActivity", "Error updating route on map: ${e.message}")
        }
    }

    override fun onResume(){
        super.onResume()
        map.onResume()
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopWorkout()
    }
}