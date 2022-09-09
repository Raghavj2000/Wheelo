package com.example.wheelchairguidance.views

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.wheelchairguidance.MainActivity
import com.example.wheelchairguidance.R

class LocationActivity : AppCompatActivity() {

    var location: Location? = null
    private lateinit var latitudeValue : TextView
    private lateinit var longitudeValue : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        location = intent.getParcelableExtra("location") as Location?

        latitudeValue =findViewById(R.id.latitudeValue)
        longitudeValue =findViewById(R.id.longitudeValue)

        val buttonBack = findViewById<Button>(R.id.back)

        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        if(location != null){
            latitudeValue.text = location!!.latitude.toString()
            longitudeValue.text = location!!.longitude.toString()
        }
    }
}