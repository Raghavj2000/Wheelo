package com.example.wheelchairguidance

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.wheelchairguidance.login.LoginActivity
import com.example.wheelchairguidance.views.AboutActivity
import com.example.wheelchairguidance.views.ListRecyclerView
import com.example.wheelchairguidance.views.LocationActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var wheelchairCard: CardView
    private lateinit var toiletCard: CardView
    private lateinit var helpCard: CardView
    private lateinit var locationButton: RelativeLayout
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var location: Location

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        private const val TAG = "MainLocation"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#718FF7"))
        actionBar!!.setBackgroundDrawable(colorDrawable)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()


        logoutButton = findViewById(R.id.logoutButton)
        wheelchairCard = findViewById(R.id.wheelchaircard);
        toiletCard = findViewById(R.id.toiletCard);
        helpCard = findViewById(R.id.helpCard);
        locationButton = findViewById(R.id.locationButton)

        logoutButton.setOnClickListener { logoutUser() }
        wheelchairCard.setOnClickListener {
            val intent = Intent(this, ListRecyclerView::class.java)
            intent.putExtra("poiList", "hospital")
            intent.putExtra("location", location)
            startActivity(intent)
            finish()
        }
        toiletCard.setOnClickListener {
            val intent = Intent(this, ListRecyclerView::class.java)
            intent.putExtra("poiList", "toilet")
            intent.putExtra("location", location)
            startActivity(intent)
            finish()
        }
        helpCard.setOnClickListener {
            val intent = Intent(this, ListRecyclerView::class.java)
            intent.putExtra("poiList", "police")
            intent.putExtra("location", location)
            startActivity(intent)
            finish()
        }

        locationButton.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.putExtra("location", location)
            startActivity(intent)
            finish()
        }


    }

    fun getCurrentLocation() {
        if (checkPermissions()) {
            Log.d(TAG, "getCurrentLocation: ")
            if (locationEnabled()) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this,  { task ->
                    location = task.result
                    if(location == null){
                        Toast.makeText(this, "NUll Location", Toast.LENGTH_SHORT)
                    }else{
                        Log.d(TAG, "getCurrentLocation: "+ location.latitude.toString() +  "," + location.longitude.toString())
                    }
                })

            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission();
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "getCurrentLocation: ")
            return true
        }
        return false
    }

    fun locationEnabled(): Boolean {
        val locatinoManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locatinoManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locatinoManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.logout -> {
                logoutUser()
            }
            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        return true
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}