package com.example.quiphassignment.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quiphassignment.R
import com.example.quiphassignment.databinding.ActivityContactDetailsBinding
import com.example.quiphassignment.viewmodel.ContactDetailsViewModel
import com.example.quiphassignment.viewmodel.ContactViewModelFactory
import kotlinx.android.synthetic.main.activity_contact_details.*


class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var contactDetailsBinding: ActivityContactDetailsBinding

    private val model: ContactDetailsViewModel by lazy {
        ViewModelProvider(
            this,
            ContactViewModelFactory(application)
        ).get(ContactDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactDetailsBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_contact_details
        )

        contactDetailsBinding.lifecycleOwner = this
        contactDetailsBinding.vm = model

        intent.getStringExtra("id")?.let {
            model.setContactDetailsLiveData(it)
        }

        model.contactDetailLiveData.observe(this, Observer {

            Glide.with(this).load(if (it.photo == null) R.drawable.ic_person_vector else it.photo)
                .apply(RequestOptions.circleCropTransform()).into(contact_image)

        })

        edit_contact_fab.setOnClickListener {
            val intent = Intent(this, EditDetailsActivity::class.java)
            intent.putExtra("id", model.contactDetailLiveData.value?.id)
            startActivity(intent)
        }

    }
}