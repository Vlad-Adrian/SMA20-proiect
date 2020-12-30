package com.vlad.passKeeper.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.vlad.passKeeper.R
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var rPassword: TextView
    private lateinit var auth: FirebaseAuth
    val PASSWORD_PATTERN: String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        email = findViewById(R.id.registerEmail)
        password = findViewById(R.id.registerPassword)
        rPassword = findViewById(R.id.registerPasswordRepeat)

        auth = FirebaseAuth.getInstance()

        val createAccountButton = findViewById<TextView>(R.id.createAccountButton)

        createAccountButton.setOnClickListener { createAccount(email.text.toString(), password.text.toString(), rPassword.text.toString()) }
    }

    private fun createAccount(email: String, pass: String, rPass: String) {
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(pass)
        if(email.isEmpty() || pass.isEmpty() || rPass.isEmpty()){
            Toast.makeText(this, "PLease complete all fields!",Toast.LENGTH_SHORT).show()
        }else if(!pass.equals(rPass)){
            Toast.makeText(this, "Passwords must be the same!",Toast.LENGTH_SHORT).show()
        }else if(!matcher.matches()){
            Toast.makeText(this, "Password must contain at least one special character, one digit, one uppercase letter",Toast.LENGTH_LONG).show()
        }else
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this){task ->
                    if (task.isSuccessful) {
                        Log.d("signupAttemtpt", "createUserWithEmail:success")
                        Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        this.finish()
                    } else {
                        Log.w("signupAttemtpt", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()

                    }
                }
    }
}