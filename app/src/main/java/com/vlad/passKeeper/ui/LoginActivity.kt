package com.vlad.passKeeper.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vlad.passKeeper.MainActivity
import com.vlad.passKeeper.R
import com.vlad.passKeeper.utils.CommonUsed

class LoginActivity : AppCompatActivity() {
    private var email: EditText? = null
    private var pass: EditText? = null
    private var loginButton: TextView? = null
    private var registerButton: TextView? = null
    private var forgotPassword: TextView? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mailText: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        setFirstUse()

        email = findViewById(R.id.email)
        pass = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        forgotPassword = findViewById(R.id.forgotPassword)

        auth = FirebaseAuth.getInstance()

        loginButton?.setOnClickListener {
            if (CommonUsed.checkConnectivity(this)) login(
                email?.text.toString(),
                pass?.text.toString()
            )
        }
        registerButton?.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
        forgotPassword?.setOnClickListener {
            if (CommonUsed.checkConnectivity(this))
                MaterialDialog(this).show {
                    title(R.string.reset)
                    input(waitForPositiveButton = false, hint = "Email") { dialog, text ->
                        val inputField = dialog.getInputField()
                        val isValid = validateEmail(text)
                        inputField.error = if (isValid) {
                            mailText = text.toString()
                            null
                        } else "Incorrect mail format"
                    }
                    positiveButton(R.string.send) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mailText)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "Mail sent successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        "Mail has not been sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
        }
    }

    private fun validateEmail(email: CharSequence): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()

    }

    private fun setFirstUse() {
        if (!sharedPreferences.getBoolean("firstStart", false)) {
            with(sharedPreferences.edit()) {
                putBoolean("firstStart", true)
                apply()
            }
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }

    private fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty())
            Toast.makeText(this, "Email and password can't be empty", Toast.LENGTH_SHORT).show()
        else
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Nice! You are in!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        this.finish()
                    } else {
                        Log.w("login_attempt", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Login attemtp failed", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}