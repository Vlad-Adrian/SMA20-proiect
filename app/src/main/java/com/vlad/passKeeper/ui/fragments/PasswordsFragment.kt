package com.vlad.passKeeper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.vlad.passKeeper.R
import com.vlad.passKeeper.adapter.Adapter
import com.vlad.passKeeper.models.GeneralItem
import com.vlad.passKeeper.models.ListItem
import com.vlad.passKeeper.models.NameItem
import com.vlad.passKeeper.models.Password
import java.util.*

class PasswordsFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private var user: FirebaseUser? = null
    private var passwords = mutableListOf<Password>()
    private lateinit var textView: TextView
    val adapterList = mutableListOf<ListItem>()
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list, container, false)
        user = FirebaseAuth.getInstance().currentUser
        recyclerView = view.findViewById(R.id.listRecyclerView)
        textView = view.findViewById(R.id.textView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = Adapter(adapterList)
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().reference.child(user?.uid ?: "")

        return view
    }

    override fun onResume() {
        super.onResume()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                passwords.clear()//wihtout this it would duplicate entries
                for (el in snapshot.children) {
                    println("${el.value} si ${el.key}")
                    el.getValue<Password>(Password::class.java)?.let { passwords.add(it) }
                }
                if (passwords.size == 0) {
                    recyclerView.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    textView.visibility = View.GONE

                    adapterList.clear()
                    val hashMap = createHashMap(passwords)
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
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun createHashMap(passwordList: MutableList<Password>): TreeMap<String, MutableList<Password>> {
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

    companion object {
        //companion object helps call functions just by ClassName.FunctionName
        fun newInstance(): PasswordsFragment {
            return PasswordsFragment()
        }
    }
}