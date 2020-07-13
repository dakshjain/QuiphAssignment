package com.example.quiphassignment.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quiphassignment.R
import com.example.quiphassignment.databinding.ActivityEditDetailsBinding
import com.example.quiphassignment.viewmodel.ContactDetailsViewModel
import com.example.quiphassignment.viewmodel.ContactViewModelFactory
import kotlinx.android.synthetic.main.activity_edit_details.*

class EditDetailsActivity : AppCompatActivity() {

    private lateinit var editDetailsBinding: ActivityEditDetailsBinding

    private val model: ContactDetailsViewModel by lazy {
        ViewModelProvider(
            this,
            ContactViewModelFactory(application)
        ).get(ContactDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editDetailsBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_edit_details
        )

        editDetailsBinding.vm = model

        intent.getStringExtra("id")?.let {
            model.setContactDetailsLiveData(it)
            save_contact_fab.setOnClickListener {
                model.updateContact()
                finish()
            }
        } ?: kotlin.run {
            save_contact_fab.apply {
                text = "Add"
                setOnClickListener {
                    model.addContact()
                    finish()
                }
            }
        }

        model.setDataForEditDetails()

        model.starred.observe(this, Observer {
            if (it == "0") {
                img_starred.setImageResource(R.drawable.ic_star_off_vector)
            } else {
                img_starred.setImageResource(R.drawable.ic_star_on_vector)
            }
        })
    }
}