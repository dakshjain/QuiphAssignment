package com.example.quiphassignment.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiphassignment.R
import com.example.quiphassignment.adapter.ContactsAdapter
import com.example.quiphassignment.viewmodel.MainViewModel
import com.example.quiphassignment.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_all_contacts.*
import kotlinx.android.synthetic.main.fragment_all_contacts.view.*

class AllContactsFragment : Fragment() {

    private val contactsAdapter =
        ContactsAdapter({ position ->
            activity?.let {
                val intent = Intent(it, ContactDetailsActivity::class.java)
                intent.putExtra("id", model.contacts.value?.get(position)?.id)
                it.startActivity(intent)
            }
        },
            {
                val intent = Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:" + model.contacts.value?.get(it)?.mobileNumber)
                )
                startActivity(intent)
            }
        )

    private val model: MainViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(activity?.application!!)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_all_contacts, container, false)

        v.add_contact_fab.setOnClickListener {
            startActivity(Intent(this.activity, EditDetailsActivity::class.java))
        }


        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getAllContacts()
        model.contacts.observe(this.viewLifecycleOwner, Observer {
            contactsAdapter.replaceItems(it.toList())
        })
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = contactsAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AllContactsFragment()
    }
}