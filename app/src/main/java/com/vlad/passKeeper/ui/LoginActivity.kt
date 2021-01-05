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
import com.vlad.passKeeper.utils.Encryption

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
        sharedPreferences = getSharedPreferences("myshared", 0)

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
            ) else {
                if (getCredsSharedPrefs()) {
                    Toast.makeText(this, "Nice! You are in!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    this.finish()
                }
            }
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

    private fun saveCredsSharedPrefs(username: String, password: String, uid: String) {
        if (sharedPreferences.getString("credentials", "")?.isEmpty() == true) {
            with(sharedPreferences.edit()) {
                putString(
                    "credentials",
                    Encryption.encrypt("$username $password", "662ede816988e58fb6d057d9d85605e0")
                )
                apply()
            }
        }
        with(sharedPreferences.edit()){
            putString(
                "uid",
                uid
            )
            commit()
        }
    }

    private fun getCredsSharedPrefs(): Boolean {
        val username: String
        val pass: String
        if (sharedPreferences.getString("credentials", "")?.isNotEmpty() == true) {
            var concatenated = sharedPreferences.getString("credentials", "")
            concatenated =
                Encryption.decryptWithAES("662ede816988e58fb6d057d9d85605e0", concatenated)
            username = concatenated?.split(" ")?.get(0) ?: " "
            pass = concatenated?.split(" ")?.get(1) ?: " "
            return username == this.email?.text.toString() && pass == this.pass?.text.toString()
        }else{
            Toast.makeText(this, "Please first login when you have internet conn", Toast.LENGTH_SHORT).show()
            return false
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
                        saveCredsSharedPrefs(
                            this.email?.text.toString(),
                            this.pass?.text.toString(),
                            FirebaseAuth.getInstance().currentUser?.uid?:""
                        )
                        this.finish()
                    } else {
                        Log.w("login_attempt", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Login attemtp failed", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}