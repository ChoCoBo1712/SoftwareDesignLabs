package com.example.tabatatimer.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.tabatatimer.Constants.LOCALE_PREFERENCE
import com.example.tabatatimer.R
import com.example.tabatatimer.PreferenceHelper
import java.util.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        PreferenceHelper.updateLocale(this, this)
        PreferenceHelper.setNightMode(this)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}