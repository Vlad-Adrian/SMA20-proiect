package com.vlad.passKeeper.adapter

import android.content.Context
import android.content.Intent
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
import com.vlad.passKeeper.models.GeneralItem
import com.vlad.passKeeper.models.ListItem
import com.vlad.passKeeper.models.NameItem
import com.vlad.passKeeper.ui.LoginActivity
import com.vlad.passKeeper.ui.PasswordAdd
import com.vlad.passKeeper.utils.CommonUsed

class Adapter(private var adapterList: MutableList<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            1 -> {
                val v1 = inflater.inflate(R.layout.password_item, parent, false)
                viewHolder = GeneralViewHolder(v1)
            }

            0 -> {
                val v2 = inflater.inflate(R.layout.password_item_header, parent, false)
                viewHolder = NameViewHolder(v2)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val generalItem = adapterList[position] as GeneralItem
                val itemHolder = holder as GeneralViewHolder
                itemHolder.title.text = generalItem.password?.name ?: "null"
                itemHolder.email.text = generalItem.password?.username ?: "null"
                if (generalItem.password?.name?.length ?: 0 >= 4) {
                    itemHolder.title_text.text = generalItem.password?.name?.substring(0, 3)
                } else {
                    itemHolder.title_text.text = generalItem.password?.name
                }

                itemHolder.cardview.setOnClickListener {
                    //TODO(must implement open the password)
                }
                val ref =
                    FirebaseAuth.getInstance().currentUser?.let {
                        generalItem.password?.name?.let { name ->
                            FirebaseDatabase.getInstance().reference.child(it.uid).child(name)
                        }
                    }
                val drawable: Drawable = itemHolder.context.resources.getDrawable(R.drawable.delete)
                itemHolder.cardview.setOnLongClickListener {
                    if (CommonUsed.checkConnectivity(itemHolder.context))
                        MaterialDialog(itemHolder.context).show {
                            title(text = "Delete?")
                            icon(drawable = drawable)
                            message(text = "Are you sure you want to delete it?")
                            positiveButton(text = "Yes") {
                                adapterList.removeAt(position)
                                ref?.removeValue()

                            }
                            negativeButton(text = "Cancel")
                        }
                    true
                }

                itemHolder.cardview.setOnClickListener {
                    itemHolder.context.startActivity(
                        PasswordAdd.newInstance(
                            itemHolder.context,
                            generalItem.password
                        )
                    )
                }
            }

            0 -> {
                val nameItem = adapterList[position] as NameItem
                val nameViewHolder = holder as NameViewHolder
                nameViewHolder.titleName.text = nameItem.name
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return adapterList[position].getType()
    }

    override fun getItemCount(): Int {
        return adapterList.size
    }

    internal class NameViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var titleName: TextView = itemView.findViewById(R.id.initialTitle)
    }

    internal class GeneralViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var title: TextView = itemView.findViewById(R.id.title2)
        internal var email: TextView = itemView.findViewById(R.id.usernameElement)
        internal var title_text: TextView = itemView.findViewById(R.id.titleElementText)
        internal var cardview: CardView = itemView.findViewById(R.id.rootView)
        internal val context = v.context

    }
}