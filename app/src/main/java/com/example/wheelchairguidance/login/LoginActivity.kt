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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var toSignUp: TextView
    private lateinit var emailText : TextInputEditText
    private lateinit var passwordText : TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var signInButton: Button
    private lateinit var auth: FirebaseAuth;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val user = FirebaseAuth.getInstance().getCurrentUser()
        if(user!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{

        }

        auth = Firebase.auth

        toSignUp = findViewById(R.id.toSignInText)
        emailText = findViewById(R.id.inputUsername)
        passwordText = findViewById(R.id.signinPassword)
        signInButton = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.progressBar)

        toSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        signInButton.setOnClickListener { loginUser() }

    }

    private fun loginUser() {
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()

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


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener { result ->

                if(result.isSuccessful){
                    Toast.makeText(this, "User Logged in", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.VISIBLE

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,result.exception!!.message , Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.INVISIBLE
                }
            })
    }
}