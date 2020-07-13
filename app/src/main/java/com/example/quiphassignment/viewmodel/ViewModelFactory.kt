package com.example.quiphassignment.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory


internal class ViewModelFactory(application: Application) :
    NewInstanceFactory() {
    private val application: Application = application

    override fun <T : ViewModel> create(modelClass: Class<T>): T  {

          return  MainViewModel(application) as T

    }
}