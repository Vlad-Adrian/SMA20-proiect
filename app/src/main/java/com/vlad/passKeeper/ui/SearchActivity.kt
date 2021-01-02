package com.vlad.passKeeper.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vlad.passKeeper.R
import com.vlad.passKeeper.adapter.Adapter
import com.vlad.passKeeper.models.GeneralItem
import com.vlad.passKeeper.models.ListItem
import com.vlad.passKeeper.models.NameItem
import com.vlad.passKeeper.models.Password
import java.util.*

class SearchActivity : AppCompatActivity() {
    private var dbList = mutableListOf<Password>()
    lateinit var databasereference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private var searchList = mutableListOf<Password>()
    val adapterList = mutableListOf<ListItem>()
    private lateinit var adapterPass: Adapter
    private lateinit var alerText: TextView
    private var searchedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        val toolbarSearch = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarSearch)
        setSupportActionBar(toolbarSearch)
        databasereference = FirebaseDatabase.getInstance().reference
        databasereference.child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dbList.clear()
                    for (entry in snapshot.children) {
                        entry.getValue(Password::class.java)?.let { dbList.add(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        recyclerView = findViewById(R.id.recyclerViewSearch)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(baseContext)

        alerText = findViewById(R.id.alertText)
        updateView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        val searchView = MenuItemCompat.getActionView(menu.findItem(R.id.search_pass)) as SearchView

        searchView.isIconifiedByDefault = false
        searchView.queryHint = "Search Passwords"
        menu.findItem(R.id.search_pass).expandActionView()

        val menuItem = menu.findItem(R.id.search_pass)
        MenuItemCompat.setOnActionExpandListener(
            menuItem,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    onBackPressed()
                    return false
                }
            })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList.clear()
                search(newText)
                searchedText = newText
                return true
            }
        })

        return true
    }

    override fun onResume() {
        super.onResume()
        if (searchedText.isNotBlank()) {
            search(searchedText)
        }
    }

    private fun search(newText: String) {
        if (newText == "") {
            searchList.clear()
        } else {
            searchList.clear()
            for (i in 0 until dbList.size) {
                if (dbList[i].name.toLowerCase(Locale.getDefault()).contains(newText)) {
                    searchList.add(dbList[i])
                }
            }
            val hashMap = createHashMap(searchList)
            adapterList.clear()
            for (nameHashed in hashMap.keys) {
                val nameItem = NameItem()
                nameItem.name = nameHashed
                adapterList.add(nameItem)

                for (passwordHashed in hashMap[nameHashed]!!) {
                    val generalItem = GeneralItem()
                    generalItem.password = passwordHashed
                    adapterList.add(generalItem)
                }
            }

            adapterPass = Adapter(adapterList)
            recyclerView.adapter = adapterPass
            adapterPass.notifyDataSetChanged()
        }
        updateView()
    }

    private fun updateView() {
        if (searchList.isEmpty()) {
            recyclerView.visibility = View.GONE
            alerText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            alerText.visibility = View.GONE
        }
    }

    private fun createHashMap(passwordList: MutableList<Password>): TreeMap<String, MutableList<Password>> {
        val hashMap = TreeMap<String, MutableList<Password>>()

        for (password in passwordList) {
            val key: String = password.name.substring(0, 1).toUpperCase(Locale.getDefault())
            if (hashMap.containsKey(key)) {
                hashMap[key]!!.add(password)
            } else {
                val list = ArrayList<Password>()
                list.add(password)
                hashMap[key] = list
            }
        }
        return hashMap
    }
}