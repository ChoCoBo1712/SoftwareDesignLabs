package com.example.tabatatimer.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.example.tabatatimer.Constants.LOCALE_PREFERENCE
import com.example.tabatatimer.Constants.RECREATE
import com.example.tabatatimer.PreferenceHelper
import com.example.tabatatimer.R
import com.example.tabatatimer.fragments.SequenceDetailFragment
import com.example.tabatatimer.fragments.SequenceListFragment
import com.example.tabatatimer.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceHelper.updateLocale(this, this)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if (intent.getBooleanExtra(RECREATE, false)) {
            bottomNavigation.selectedItemId = R.id.nav_settings
        }
        else {
            bottomNavigation.selectedItemId = R.id.nav_sequences
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SequenceDetailFragment.newInstance(0))
                .addToBackStack(null)
                .commit()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.nav_sequences -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SequenceListFragment.newInstance())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment.newInstance())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            else -> {
                false
            }
        }
    }
}