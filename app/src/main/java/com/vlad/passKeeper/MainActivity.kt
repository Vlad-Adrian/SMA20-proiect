package com.vlad.passKeeper

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.vlad.passKeeper.ui.LoginActivity
import com.vlad.passKeeper.ui.PasswordAdd
import com.vlad.passKeeper.ui.SearchActivity
import com.vlad.passKeeper.ui.fragments.NotesFragment
import com.vlad.passKeeper.ui.fragments.PasswordsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var navigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navigation = findViewById(R.id.navigation)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.title = "PassKeeper"

        fragmentChooser(PasswordsFragment.newInstance(), "PasswordsFragment")
        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_pass -> {
                    fragmentChooser(PasswordsFragment.newInstance(), "PasswordsFragment")
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_notes -> {
                    fragmentChooser(NotesFragment.newInstance(), "NotesFragment")
                    return@setOnNavigationItemSelectedListener true
                }

                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun fragmentChooser(frag: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentFrame, frag, tag)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragmentManager = supportFragmentManager.findFragmentByTag("PasswordsFragment")
        val drawable: Drawable = resources.getDrawable(R.drawable.exit2)
        return when (item.itemId) {
            R.id.add -> {
                if (fragmentManager?.isVisible == true)
                    startActivity(Intent(this, PasswordAdd::class.java))
                else Toast.makeText(this, "Add notes", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.search -> {
                if (fragmentManager?.isVisible == true)
                    startActivity(Intent(this, SearchActivity::class.java))
                else Toast.makeText(this, "Search notes", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.logout -> {
                MaterialDialog(this).show {
                    title(text = "Log out?")
                    icon(drawable = drawable)
                    message(text = "Are you sure you want to log out?")
                    positiveButton(text = "Yes") {
                        auth.signOut()
                        startActivity(Intent(baseContext, LoginActivity::class.java))
                        this@MainActivity.finish()

                    }
                    negativeButton(text = "Cancel")
                }
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}