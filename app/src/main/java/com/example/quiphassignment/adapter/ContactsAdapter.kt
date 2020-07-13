package com.example.quiphassignment.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quiphassignment.R
import com.example.quiphassignment.model.ContactModel
import com.example.quiphassignment.view.ContactDetailsActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactsAdapter(
    private val clickEvent: (Int) -> Unit,
    private val longClickEvent: (Int) -> Unit
) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    private var items = listOf<ContactModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_list_item, parent, false)

        return ViewHolder(view, clickEvent, longClickEvent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.containerView.contentTextView.text = item.mobileNumber
        holder.containerView.sourceTextView.text = item.name
    }

    fun replaceItems(items: List<ContactModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(
        override val containerView: View,
        private val clickEvent: (Int) -> Unit,
        private val longClickEvent: (Int) -> Unit
    ) : RecyclerView.ViewHolder(containerView),
        LayoutContainer,
        View.OnClickListener,
        View.OnLongClickListener {

        init {
            containerView.setOnClickListener(this)
            containerView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            clickEvent.invoke(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            longClickEvent.invoke(adapterPosition)
            return true
        }

    }
}