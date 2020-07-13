package com.example.quiphassignment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(private var context: Context) {

    companion object {
        val PERMISSION_READ_CONTACTS = 1
        val PERMISSION_WRITE_CONTACTS = 2
        val PERMISSION_CALL_PHONE = 3


        val GENERIC_PERM_HANDLER = 10
    }

    private val activity = context as Activity

    fun checkContactPermissionsAndSetFragments( callback: (Boolean) -> Unit) {
        handlePermission(PERMISSION_READ_CONTACTS) { it ->
            if (it) {
                handlePermission(PERMISSION_WRITE_CONTACTS) {
                    if (it) {
                        handlePermission(PERMISSION_CALL_PHONE) {
                                callback(true)
                        }
                    }
                }
            }
        }
    }

    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        if (context.hasPermission(permissionId)) {
            callback(true)
        } else {
            val permission = getPermissionString(permissionId)
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                GENERIC_PERM_HANDLER
            )
        }
    }

    private fun getPermissionString(id: Int) = when (id) {

        PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
        PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
        PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
        else -> ""
    }

    private fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
        this, getPermissionString(permId)
    ) == PackageManager.PERMISSION_GRANTED
}