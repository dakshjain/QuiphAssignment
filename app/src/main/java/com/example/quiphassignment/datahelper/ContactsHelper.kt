package com.example.quiphassignment.datahelper

import android.app.Application
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import com.example.quiphassignment.model.ContactModel
import java.io.InputStream


class ContactsHelper(private var application: Application) {

    fun updateContact(contact: ContactModel, callback: (Boolean) -> Unit) {

        try {
            val operations = ArrayList<ContentProviderOperation>()
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).apply {
                val selection = "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND (${ContactsContract.Data.MIMETYPE} = ? OR ${ContactsContract.Data.MIMETYPE} = ?)"
                val selectionArgs = arrayOf(contact.id.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                withSelection(selection, selectionArgs)
                withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name)
                operations.add(build())
            }

            // delete phone numbers
            ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).apply {
                val selection = "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? "
                val selectionArgs = arrayOf(contact.id.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                withSelection(selection, selectionArgs)
                operations.add(build())
            }

            // add phone numbers
            contact.mobileNumber.let {
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
                    withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.id)
                    withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, it)
                    operations.add(build())
                }
            }

            // delete emails
            ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).apply {
                val selection = "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? "
                val selectionArgs = arrayOf(contact.id.toString(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                withSelection(selection, selectionArgs)
                operations.add(build())
            }

            // add emails
            contact.email?.let {
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
                    withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.id)
                    withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    withValue(ContactsContract.CommonDataKinds.Email.DATA, it)
                    operations.add(build())
                }
            }

            // favorite
            try {
                val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact.id.toString())
                val contentValues = ContentValues(1)
                contentValues.put(ContactsContract.Contacts.STARRED, contact.starred)
                application.contentResolver.update(uri, contentValues, null, null)
            } catch (e: Exception) {
                callback(false)
            }

            application.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            callback(true)
        } catch (e: Exception) {

            callback(false)
        }
    }

    fun refreshAllContacts(action: (MutableList<ContactModel>) -> Unit) {

        val list : MutableList<ContactModel> = ArrayList()

        val contentResolver: ContentResolver = application.contentResolver
        val cursorObject: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        cursorObject?.let { cursor ->
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        val cursorInfo: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        val inputStream: InputStream? =
                            ContactsContract.Contacts.openContactPhotoInputStream(
                                application.contentResolver,
                                ContentUris.withAppendedId(
                                    ContactsContract.Contacts.CONTENT_URI,
                                    id.toLong()
                                )
                            )
                        val person: Uri =
                            ContentUris.withAppendedId(
                                ContactsContract.Contacts.CONTENT_URI,
                                id.toLong()
                            )
                        val pURI: Uri = Uri.withAppendedPath(
                            person,
                            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
                        )

                        var photo: Bitmap? = null
                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream)
                        }

                        while (cursorInfo?.moveToNext()!!) {
                            val info =
                                ContactModel()
                            info.id = id
                            info.name =
                                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME).let {
                                    cursor.getString(
                                        it
                                    )
                                }
                            info.mobileNumber =
                                cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            info.starred =
                                cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.Contacts.STARRED))

                            val emailCur: Cursor? = contentResolver.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )
                            while (emailCur?.moveToNext()!!) {
                                info.email =
                                    emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)) // Here you will get list of email
                            }
                            emailCur.close()

                            /*info.email =
                                cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))*/

                            info.photo = photo;
                            info.photoURI = pURI;

                            list.add(info)
                            action.invoke(list)
                        }
                        cursorInfo.close()
                    }
                }
                cursor.close()
            }
        }
    }

    fun addNewContact(person: ContactModel, callback: (Boolean) -> Unit) {
        val displayName: String = person.name ?: ""
        val mobileNumber: String = person.mobileNumber ?: ""
        val email: String = person.email  ?: ""
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )

        // Names
        if (displayName != null) {
            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        displayName
                    ).build()
            )
        }

        // Mobile Number
        if (mobileNumber != null) {
            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    ).build()
            )
        }

        // Email
        if (email != null) {
            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .withValue(
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    ).build()
            )
        }

        // Asking the Contact provider to create a new contact
        try {
            application.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            callback(true)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            callback(false)
        }
    }
}