package com.example.quiphassignment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.quiphassignment.repository.ContactsRepository
import com.example.quiphassignment.model.ContactModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactDetailsViewModel(application: Application) : AndroidViewModel(application) {

    var name = MutableLiveData<String>()
    var mobileNumber = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var starred = MutableLiveData<String>()

    private val contactsRepository =
        ContactsRepository.newInstance(application)

    var contactDetailLiveData = MutableLiveData<ContactModel>()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun setContactDetailsLiveData(id: String) {
        contactDetailLiveData = contactsRepository.getContactDetails(id)
    }

    fun updateContact() {
        ioScope.launch {
            contactsRepository.updateContact(
                ContactModel(
                    id = contactDetailLiveData.value?.id,
                    starred = starred.value,
                    mobileNumber = mobileNumber.value,
                    email = email.value,
                    name = name.value,
                    photo = contactDetailLiveData.value?.photo,
                    photoURI = contactDetailLiveData.value?.photoURI
                )
            )
        }
    }

    fun addContact() {
        ioScope.launch {
            contactsRepository.addContact(
                ContactModel(
                    starred = starred.value,
                    mobileNumber = mobileNumber.value,
                    email = email.value,
                    name = name.value
                )
            )
        }
    }

    fun updateStarred() {
        val newStarred = if (contactDetailLiveData.value?.starred.equals("1")) "0" else "1"
        ioScope.launch {
            contactDetailLiveData.value?.copy(starred = newStarred)?.let {
                contactsRepository.updateContact(
                    it
                )
            }
        }
    }

    fun toggleStarredLiveData() {
        starred.value = if (starred.value.equals("1")) "0" else "1"
    }

    fun setDataForEditDetails() {
        name.value = contactDetailLiveData.value?.name
        mobileNumber.value = contactDetailLiveData.value?.mobileNumber
        email.value = contactDetailLiveData.value?.email
        starred.value = contactDetailLiveData.value?.starred
    }

}

