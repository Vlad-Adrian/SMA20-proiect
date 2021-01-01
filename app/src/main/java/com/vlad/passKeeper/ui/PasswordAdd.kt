package com.vlad.passKeeper.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vlad.passKeeper.R
import com.vlad.passKeeper.models.Password
import com.vlad.passKeeper.utils.Encryption

class PasswordAdd : AppCompatActivity() {
    private lateinit var appbar: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var notes: EditText
    private lateinit var link: EditText
    private lateinit var titleImage: FrameLayout
    private lateinit var titleText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_edit)

        toolbar = findViewById<Toolbar>(R.id.toolbarPassword)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appbar = findViewById(R.id.appBar)
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout)

        username = findViewById(R.id.username)
        password = findViewById(R.id.passwordEdit)
        name = findViewById(R.id.name)
        notes = findViewById(R.id.notes)
        link = findViewById(R.id.link)

        titleImage = findViewById(R.id.titleImage)
        titleText = findViewById(R.id.titleText)
        if (!name.text.isEmpty())
            titleText.text = name.text.toString()

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                titleText.text = name.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                titleText.text = name.text.toString()
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveDb()
                true
            }

            R.id.edit -> {
                //TODO(WIP)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveDb() {
        if (password.text.isEmpty()) {
            Toast.makeText(applicationContext, "Password field can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (name.text.isEmpty()) {
            Toast.makeText(applicationContext, "Name field can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else {
            val user: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (user != "") {
                val database = FirebaseDatabase.getInstance().reference
                database.child(user).child(name.text.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.getValue() == null) {
                                val encryptPass = Encryption.encrypt(
                                    password.text.toString(),
                                    "662ede816988e58fb6d057d9d85605e0"
                                )
                                val pass = Password(
                                    username.text.toString(),
                                    encryptPass,
                                    name.text.toString(),
                                    link.text.toString(),
                                    notes.text.toString()
                                )
                                database.child(user).child(name.text.toString()).setValue(pass)
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "PLease choose a different name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

            }
        }

    }

    private fun copyText(s: String?) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = s
        Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
    }

    fun copyClick(view: View) {
        when (view.id) {
            R.id.copyUsername -> {
                if (username.text.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Username Field is Empty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    copyText(username.text.toString())
                }
            }
            R.id.copyPassword -> {
                if (password.text.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Password Field is Empty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    copyText(password.text.toString())
                }
            }
            R.id.copyLink -> {
                if (link.text.isEmpty()) {
                    Toast.makeText(applicationContext, "Link Field is Empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    copyText(link.text.toString())
                }
            }
        }
    }
}