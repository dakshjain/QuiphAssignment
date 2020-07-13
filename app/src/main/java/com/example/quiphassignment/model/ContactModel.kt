package com.example.quiphassignment.model

import android.graphics.Bitmap
import android.net.Uri


data class ContactModel(
    var id: String? = null,
    var name: String? = null,
    var mobileNumber: String? = null,
    var starred: String? = null,
    var photo: Bitmap? = null,
    var photoURI: Uri? = null,
    var email: String? = null
)
