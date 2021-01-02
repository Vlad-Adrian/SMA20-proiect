package com.vlad.passKeeper.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vlad.passKeeper.R
import com.vlad.passKeeper.models.Note
import com.vlad.passKeeper.utils.Encryption

class NoteAdd : AppCompatActivity() {
    private lateinit var noteToolbar: Toolbar
    private lateinit var noteTitle: EditText
    private lateinit var noteText: EditText
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: String
    private var edit = false
    private lateinit var beforeTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_edit_content)

        noteText = findViewById(R.id.noteAddText)
        noteTitle = findViewById(R.id.noteAddTitle)
        noteToolbar = findViewById(R.id.noteAddToolbar)

        setSupportActionBar(noteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        firebaseUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(firebaseUser).child("notes")

        if (intent.getSerializableExtra("note") != null) {
            val note = intent.getSerializableExtra("note") as Note
            noteTitle.setText(note.title)
            noteText.setText(
                Encryption.decryptWithAES(
                    "662ede816988e58fb6d057d9d85605e0",
                    note.note
                )
            )

            noteText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    edit = true
                }

            })

            noteTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    beforeTitle = note.title
                }

                override fun afterTextChanged(s: Editable?) {
                    edit = true
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        beforeTitle = ""
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

            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveDb() {
        if (noteTitle.text.isEmpty()) {
            Toast.makeText(applicationContext, "Title can't be empty", Toast.LENGTH_SHORT).show()
        } else if (edit) {
            if (beforeTitle != "") {
                databaseReference.child(beforeTitle).removeValue()
                databaseReference.child(noteTitle.text.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value == null) {
                                val encryptNote = Encryption.encrypt(
                                    noteText.text.toString(),
                                    "662ede816988e58fb6d057d9d85605e0"
                                )

                                val note = Note(noteTitle.text.toString(), encryptNote ?: "")
                                databaseReference.child(note.title).setValue(note)
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else {
                databaseReference.child(noteTitle.text.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val encryptNote = Encryption.encrypt(
                                noteText.text.toString(),
                                "662ede816988e58fb6d057d9d85605e0"
                            )

                            val note = Note(noteTitle.text.toString(), encryptNote ?: "")
                            databaseReference.child(note.title).setValue(note)
                            finish()

                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        } else {
            databaseReference.child(noteTitle.text.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            val encryptNote = Encryption.encrypt(
                                noteText.text.toString(),
                                "662ede816988e58fb6d057d9d85605e0"
                            )

                            val note = Note(noteTitle.text.toString(), encryptNote ?: "")
                            databaseReference.child(note.title).setValue(note)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "PLease choose a different title",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    companion object {
        private val TAG = "note"

        fun newInstance(context: Context, note: Note?): Intent {
            val intent = Intent(context, NoteAdd::class.java)
            if (note != null)
                intent.putExtra(TAG, note)
            return intent
        }
    }

}