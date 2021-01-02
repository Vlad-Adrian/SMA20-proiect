package com.vlad.passKeeper.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vlad.passKeeper.R
import com.vlad.passKeeper.models.Note
import com.vlad.passKeeper.ui.NoteAdd
import com.vlad.passKeeper.utils.CommonUsed

class NotesAdapter(private var note: MutableList<Note>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteLocal = note[position]
        holder.title.text = noteLocal.title
        holder.tileTitle.text =
            if (noteLocal.title.length > 3) noteLocal.title.substring(0, 2) else noteLocal.title

        holder.rootView.setOnClickListener {
            holder.context.startActivity(
                NoteAdd.newInstance(
                    holder.context,
                    noteLocal
                )
            )
        }
        val drawable: Drawable = holder.context.resources.getDrawable(R.drawable.delete)
        val databaseReference = FirebaseDatabase.getInstance().reference.child(
            FirebaseAuth.getInstance().currentUser?.uid ?: ""
        ).child("notes").child(noteLocal.title)
        holder.rootView.setOnLongClickListener {
            if (CommonUsed.checkConnectivity(holder.context))
                MaterialDialog(holder.context).show {
                    title(text = "Delete?")
                    icon(drawable = drawable)
                    message(text = "Are you sure you want to delete it?")
                    positiveButton(text = "Yes") {
                        note.removeAt(position)
                        databaseReference.removeValue()

                    }
                    negativeButton(text = "Cancel")
                }
            true
        }
    }

    override fun getItemCount(): Int {
        return note.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title = itemView.findViewById<TextView>(R.id.noteTitle)
        internal var tileTitle = itemView.findViewById<TextView>(R.id.noteTitleText)
        internal var rootView = itemView.findViewById<CardView>(R.id.noteRoot)
        internal var context = itemView.context

    }
}