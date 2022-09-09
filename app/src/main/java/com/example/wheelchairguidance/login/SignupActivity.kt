package com.example.wheelchairguidance.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.wheelchairguidance.MainActivity
import com.example.wheelchairguidance.R
import com.example.wheelchairguidance.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    lateinit var toLogin: TextView
    private lateinit var nameText : TextInputEditText
    private lateinit var mobileText : TextInputEditText
    private lateinit var emailText : TextInputEditText
    private lateinit var passwordText : TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var signUpButton: Button
    private lateinit var auth: FirebaseAuth;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        getSupportActionBar()?.hide();

        auth = Firebase.auth

        toLogin = findViewById(R.id.toLoginText)
        nameText = findViewById(R.id.inputName)
        mobileText = findViewById(R.id.inputMobile)
        emailText = findViewById(R.id.inputEmail)
        passwordText = findViewById(R.id.inputPassword)
        signUpButton = findViewById(R.id.signUpButton)
        progressBar = findViewById(R.id.progressBar)

        toLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        signUpButton.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val name = nameText.text.toString().trim()
        val email = emailText.text.toString().trim()
        val mobile = mobileText.text.toString().trim()
        val password = passwordText.text.toString().trim()

        if(name.isEmpty()){
            nameText.setError("Name cannot be blank")
            nameText.requestFocus()
            return;
        }
        if(mobile.isEmpty()){
            mobileText.setError("Mobile cannot be blank")
            mobileText.requestFocus()
            return;
        }
        if(email.isEmpty()){
            emailText.setError("email cannot be blank")
            emailText.requestFocus()
            return;
        }
        if(password.isEmpty()){
            passwordText.setError("password cannot be blank")
            passwordText.requestFocus()
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("email not valid")
            return;
            //email not valid
        }

        progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener { result ->

                if(result.isSuccessful){
                val user: User = User(name, mobile, email, password)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.VISIBLE

                            val intent = Intent(this, MainActivity::class.java)
                           startActivity(intent)
                           finish()

                        }else{
                            Toast.makeText(this, "Failed User Registration1", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.INVISIBLE
                        }
                    }
                 }else{
                    Toast.makeText(this,result.exception!!.message , Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.INVISIBLE
                }
            })

    }
}