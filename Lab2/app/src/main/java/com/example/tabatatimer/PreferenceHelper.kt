package com.example.tabatatimer

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.tabatatimer.Constants.FONT_SIZE_PREFERENCE
import com.example.tabatatimer.Constants.NIGHT_MODE_PREFERENCE
import java.util.*

class PreferenceHelper {
    companion object {
        fun setNightMode(context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val nightModeEnabled = preferences.getBoolean(NIGHT_MODE_PREFERENCE, false)

            if (nightModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun setTextSizeFragment(context: Context, baseInflater: LayoutInflater): LayoutInflater {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val fontSize = preferences.getString(FONT_SIZE_PREFERENCE, "1")

            val contextThemeWrapper = when (fontSize) {
                "0" -> {
                    ContextThemeWrapper(context, R.style.Theme_TabataTimer_SmallFont)
                }
                "1" -> {
                    ContextThemeWrapper(context, R.style.Theme_TabataTimer_MediumFont)
                }
                else -> {
                    ContextThemeWrapper(context, R.style.Theme_TabataTimer_LargeFont)
                }
            }
            return baseInflater.cloneInContext(contextThemeWrapper)
        }

        fun setTextSizeSettings(context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val fontSize = preferences.getString(FONT_SIZE_PREFERENCE, "1")

            when (fontSize) {
                "0" -> {
                    context.setTheme(R.style.Theme_TabataTimer_Settings_SmallFont)
                }
                "1" -> {
                    context.setTheme(R.style.Theme_TabataTimer_Settings_MediumFont)
                }
                else -> {
                    context.setTheme(R.style.Theme_TabataTimer_Settings_LargeFont)
                }
            }
        }


        fun updateLocale(context: Context, activity: Activity) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val lang = preferences.getString(Constants.LOCALE_PREFERENCE, "en")
            val config = Configuration()
            val locale = Locale(lang!!)
            Locale.setDefault(locale)
            config.setLocale(locale)
            activity.baseContext.resources.updateConfiguration(config, activity.baseContext.resources.displayMetrics)
        }
    }
}