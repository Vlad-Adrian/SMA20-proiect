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
import com.google.firebase.database.*
import com.vlad.passKeeper.R
import com.vlad.passKeeper.adapter.NotesAdapter
import com.vlad.passKeeper.models.Note

class NotesFragment : Fragment() {
    private var notesList = mutableListOf<Note>()
    private lateinit var recyclerViewNote: RecyclerView
    private lateinit var emptyNotes: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: String
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.notes_fragment, container, false)


        firebaseUser = FirebaseAuth.getInstance().uid ?: ""
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(firebaseUser).child("notes")
        recyclerViewNote = view.findViewById(R.id.notesListRecyclerView)
        emptyNotes = view.findViewById(R.id.notesEmpty)

        recyclerViewNote.hasFixedSize()
        recyclerViewNote.layoutManager = LinearLayoutManager(activity)
        notesAdapter = NotesAdapter(notesList)
        recyclerViewNote.adapter = notesAdapter

        return view
    }


    override fun onResume() {
        super.onResume()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                for (entry in snapshot.children) {
                    val note = entry.getValue(Note::class.java)
                    if (note != null) {
                        notesList.add(note)
                    }
                }
                if (notesList.size != 0) {
                    recyclerViewNote.visibility = View.VISIBLE
                    emptyNotes.visibility = View.GONE
                } else {
                    recyclerViewNote.visibility = View.GONE
                    emptyNotes.visibility = View.VISIBLE
                }

                notesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }


    companion object {
        //companion object helps call functions just by ClassName.FunctionName
        fun newInstance(): NotesFragment {
            return NotesFragment()
        }
    }
}