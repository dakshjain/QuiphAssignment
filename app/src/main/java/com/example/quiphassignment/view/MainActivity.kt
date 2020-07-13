package com.example.quiphassignment.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.quiphassignment.PermissionHandler
import com.example.quiphassignment.R
import com.example.quiphassignment.adapter.MyViewPageStateAdapter
import com.example.quiphassignment.databinding.ActivityMainBinding
import com.example.quiphassignment.viewmodel.MainViewModel
import com.example.quiphassignment.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionHandler: PermissionHandler

    private val model: MainViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(application)
        ).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        checkPermissionAndSetUpUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        permissionHandler = PermissionHandler(this)

        checkPermissionAndSetUpUI()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                val count = fm.backStackEntryCount
                if (count >= 1) {
                    supportFragmentManager.popBackStack()
                }
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.editFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                model.filterContacts(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun checkPermissionAndSetUpUI() {
        permissionHandler.checkContactPermissionsAndSetFragments {
            if (it) setUpUI()
        }
    }

    private fun setUpUI() {
        model.setAllContacts()
        setStatePageAdapter()
    }

    private fun actionOnPermission(isPermissionHandled: Boolean) {
        if (!isPermissionHandled) {
            val dialog = AlertDialog.Builder(this)
            dialog.apply {
                setMessage("Please provide all Permissions to start")
                setPositiveButton("Yes") { _, _ ->
                    permissionHandler.checkContactPermissionsAndSetFragments {
                    }
                }
                show()
            }
        } else {
            setUpUI()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (0 in grantResults) {
            if ((android.Manifest.permission.CALL_PHONE) in permissions) {
                actionOnPermission(true)
            } else checkPermissionAndSetUpUI()
        }
    }

    private fun setStatePageAdapter() {
        val myViewPageStateAdapter: MyViewPageStateAdapter =
            MyViewPageStateAdapter(
                supportFragmentManager
            )
        myViewPageStateAdapter.addFragment(AllContactsFragment.newInstance(), "All Contacts")
        myViewPageStateAdapter.addFragment(FavouriteFragment(), "Favourites")
        viewpager.adapter = myViewPageStateAdapter
        tabs.setupWithViewPager(viewpager, true)
    }
}