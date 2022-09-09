package com.example.wheelchairguidance

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.wheelchairguidance.views.AboutActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapsActivity : AppCompatActivity() {

    private val MY_USER_AGENT: String? = "Raghav"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map: MapView;
    private lateinit var myLocationNewOverlay: MyLocationNewOverlay
    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>
    private var lastLoction: Location? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    var location: Location? = null


    companion object {
        val REQUEST_CHECK_SETTINGS = 20202
    }

    init {
        locationRequest = LocationRequest.create()
            .apply { //https://stackoverflow.com/questions/66489605/is-constructor-locationrequest-deprecated-in-google-maps-v2
                interval = 1000 //can be much higher
                fastestInterval = 500
                smallestDisplacement = 10f //10m
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                maxWaitTime = 1000
            }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    val latitude = currentLocation?.latitude
                    val longitude = currentLocation?.longitude//MY function
                    Log.d("MapsActivity", "onLocationResult: ${latitude} + ${longitude} ")
                }
            }
        }

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            Log.d("MapsActivity", ":Permissions granted ")
            if (allAreGranted) {
                initCheckLocationSettings()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val poi = intent.getParcelableExtra("poi") as POI?
        location = intent.getParcelableExtra("location") as Location?
        Log.d("MapsActivity", "onCreate: " + poi.toString())
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //inflate and create the map
        setContentView(R.layout.activity_maps);

        val buttonBack = findViewById<Button>(R.id.back)

        buttonBack.setOnClickListener {
            onBackPressed()
        }

        val appPerms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )
        activityResultLauncher.launch(appPerms)


        map = findViewById(R.id.map)
        myLocationNewOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationNewOverlay.enableMyLocation()
        Log.d("MapsActivity", "onCreate: " + myLocationNewOverlay)
        initializeMap(poi!!.mLocation.latitude, poi.mLocation.longitude, poi.mDescription);
        setUpRpute(poi!!.mLocation.latitude, poi.mLocation.longitude, poi.mDescription);
//        searchpoi();


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        Timber.d("Settings onActivityResult for $requestCode result $resultCode")
        Log.d("MapsActivity", "onActivityResult: for ${requestCode} result ${resultCode}")
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Log.d("MapsActivity", "onActivityResult: Permissions granted")
            }
        }
    }


    private fun setUpRpute(latitude: Double, longitude: Double, mDescription: String) {
        val roadManager: RoadManager = OSRMRoadManager(this, MY_USER_AGENT);
        val waypoints = ArrayList<GeoPoint>()
//        val startPoint = GeoPoint(13.0545, 77.5916)
        var startPoint = GeoPoint( 13.0260869, 77.5874712)
        if(location != null){
            startPoint = GeoPoint( location!!.latitude, location!!.longitude)
        }
        waypoints.add(startPoint)
        val endPoint = GeoPoint(latitude, longitude)
        waypoints.add(endPoint)

        val road = roadManager.getRoad(waypoints)
        val roadOverlay: Polyline = RoadManager.buildRoadOverlay(road)

        map.getOverlays().add(roadOverlay);
//        map.invalidate();
    }

    private fun initializeMap(latitude: Double, longitude: Double, mDescription: String) {
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setUseDataConnection(true)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        map.isHorizontalMapRepetitionEnabled = false
        map.isVerticalMapRepetitionEnabled = false
        map.setMaxZoomLevel(20.0);
        map.setMinZoomLevel(4.0);
        val mapController = map.controller
        mapController.setZoom(14)
        var startPoint = GeoPoint( 13.0260869, 77.5874712)
        if(location != null){
            startPoint = GeoPoint( location!!.latitude, location!!.longitude)
        }
        mapController.setCenter(startPoint)

        val startMarker = Marker(map)
        startMarker.setPosition(startPoint)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker)

        val endPoint = GeoPoint(latitude, longitude)
        val endMarker = Marker(map)
        endMarker.setPosition(endPoint)
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(endMarker)
        val list = mDescription.split(",", limit = 2)
        endMarker.title = list[0]

        //Set your own marker
//        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        startMarker.setTitle("Start point");
    }

    override fun onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun initCheckLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
//            Timber.d("Settings Location IS OK")
            Log.d("MapsActivity", "initCheckLocationSettings: Settings Location is OK")
//            MyEventLocationSettingsChange.globalState = true //default
//            initMap()
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
//                Timber.d("Settings Location addOnFailureListener call settings")
                Log.d(
                    "MapsActivity",
                    "initCheckLocationSettings: Settings Location addOnFailureListener call settings"
                )
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
//                    Timber.d("Settings Location sendEx??")
                    Log.d("MapsActivity", "initCheckLocationSettings: settings Location sendEx??")

                }
            }
        }

    }


}