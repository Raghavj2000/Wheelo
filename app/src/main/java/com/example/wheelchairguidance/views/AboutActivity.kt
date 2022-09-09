
package com.example.wheelchairguidance.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.wheelchairguidance.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#718FF7"))
        actionBar!!.setBackgroundDrawable(colorDrawable)


        val gma= findViewById<View>(R.id.gmailbutton) as ImageView
        gma.setOnClickListener(View.OnClickListener {
            val url3= Uri.parse("raghavjayateerth@gmail.com")
            val gmail = Intent(Intent.ACTION_VIEW,url3)
            gmail.setPackage("com.android.gmail")
            try {
                startActivity(gmail)

            }catch (e: ActivityNotFoundException){
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://mail.google.com/mail/u/raghavjayateerth@gmail.com")))
            }

        })


        val git = findViewById<View>(R.id.githubbutton) as ImageView
        git.setOnClickListener(View.OnClickListener {
            val url2= Uri.parse("https://github.com/Raghavj2000")
            val github = Intent(Intent.ACTION_VIEW,url2)
            github.setPackage("com.android.github")
            try {
                startActivity(github)

            }catch (e: ActivityNotFoundException){
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/Raghavj2000")))
            }

        })

        val linkedin = findViewById<View>(R.id.linkedinbutton) as ImageView
        linkedin.setOnClickListener(View.OnClickListener {
            val url1 = Uri.parse("https://www.linkedin.com/in/raghavendra-jayateerth-457893211")
            val linkedin1 = Intent(Intent.ACTION_VIEW,url1)
            linkedin1.setPackage("com.android.linkedin")
            try {
                startActivity(linkedin1)

            }catch (e: ActivityNotFoundException){
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.linkedin.com/in/raghavendra-jayateerth-457893211")))
            }
        })




        val insta = findViewById<View>(R.id.instabutton) as ImageView
        insta.setOnClickListener(View.OnClickListener {
            val url=Uri.parse("https://www.instagram.com/raghavj_/ ")
            val instagram = Intent(Intent.ACTION_VIEW,url)
            instagram.setPackage("com.android.instagram")
            try {
                startActivity(instagram)

            }catch (e: ActivityNotFoundException){
              startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/raghavj_/ ")))
            }
        })
        val buttonBack = findViewById<Button>(R.id.button1)

        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }}