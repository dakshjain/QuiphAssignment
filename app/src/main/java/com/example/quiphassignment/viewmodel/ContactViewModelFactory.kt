package com.example.quiphassignment.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

public class ContactViewModelFactory(application: Application) :
    ViewModelProvider.NewInstanceFactory() {
    private val application: Application = application

    override fun <T : ViewModel> create(modelClass: Class<T>): T  {

        return  ContactDetailsViewModel(
            application
        ) as T

    }
}