package com.example.quiphassignment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.quiphassignment.repository.ContactsRepository
import com.example.quiphassignment.model.ContactModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel constructor(application: Application) : AndroidViewModel(application) {

    private var applicationNew: Application = application

    var contacts = MutableLiveData<List<ContactModel>>()
    var favContacts = MutableLiveData<List<ContactModel>>()
    private val contactsRepository =
        ContactsRepository.newInstance(application)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun getAllContacts() {
        contacts = contactsRepository.getAllContacts()
    }

    fun setAllContacts() {
        ioScope.launch {
            contactsRepository.refreshAllContacts(applicationNew)
        }
    }

    fun getAllFavouriteContacts() {
        favContacts = contactsRepository.getAllFavouriteContacts()
    }

    fun filterContacts(filter: String) {
        contactsRepository.filterContacts(filter)
    }
}