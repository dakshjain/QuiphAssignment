package com.example.quiphassignment.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.quiphassignment.datahelper.ContactsHelper
import com.example.quiphassignment.model.ContactModel


class ContactsRepository private constructor(private var application: Application) {

    private val list: MutableList<ContactModel> = ArrayList()
    private var listLiveData = MutableLiveData<List<ContactModel>>()
    private val favouritesLiveData = MutableLiveData<List<ContactModel>>()
    private val contactDetailLiveData = MutableLiveData<ContactModel>()

    private val contactsHelper = ContactsHelper(application)

    companion object {
        private var contactRepository: ContactsRepository? = null

        fun newInstance(application: Application): ContactsRepository {
            if (contactRepository == null) {
                contactRepository =
                    ContactsRepository(
                        application
                    )
            }
            return contactRepository as ContactsRepository
        }
    }

    fun getAllContacts() = listLiveData

    fun getAllFavouriteContacts(): MutableLiveData<List<ContactModel>> {
        favouritesLiveData.postValue(list.filter { contactModel -> contactModel.starred.equals("1") })
        return favouritesLiveData
    }

    fun getContactDetails(id: String): MutableLiveData<ContactModel> {
        val contact = list.find { it.id == id }
        contactDetailLiveData.postValue(contact)
        return contactDetailLiveData
    }

    fun filterContacts(filter: String) {
        if (filter.isNotEmpty()) {
            val newList = list.filter { contactModel ->
                contactModel.name?.toLowerCase()?.contains(filter.toLowerCase()) == true
            }
            listLiveData.value = list.filter { contactModel ->
                contactModel.name?.toLowerCase()?.contains(filter.toLowerCase()) == true
            }
            favouritesLiveData.value =
                newList.filter { contactModel -> contactModel.starred.equals("1") }
        } else {
            listLiveData.value = list
            getAllFavouriteContacts()
        }
    }

    private fun makeListSorted() = list.sortBy { contactModel -> contactModel.name }

    fun refreshAllContacts(application: Application) {
        contactsHelper
            .refreshAllContacts { newList ->
                run {
                    list.clear()
                    list.addAll(newList)
                    makeListSorted()
                    listLiveData.postValue(list)
                    getAllFavouriteContacts()
                }
            }
    }

    fun updateContact(contactModel: ContactModel) {
        contactsHelper.updateContact(contactModel) {
            if (it) {
                refreshAllContacts(application)
                contactModel.id?.let { it1 -> getContactDetails(it1) }
            }
        }
    }

    fun addContact(contactModel: ContactModel) {
        contactsHelper.addNewContact(contactModel) {
            if (it) {
                refreshAllContacts(application)
                contactModel.id?.let { it1 -> getContactDetails(it1) }
            }
        }
    }
}