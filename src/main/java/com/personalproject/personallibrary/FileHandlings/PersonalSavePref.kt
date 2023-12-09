package com.personalproject.personallibrary.FileHandlings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.personalproject.personallibrary.PersonalCommonVar

class PersonalSavePref(private val context: Context) {

    private val lockSharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PersonalCommonVar().STR_LOCK_FILE, Context.MODE_PRIVATE)
    }
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    // Save preference for various data types
    fun savePreference(key: String, value: Any) {
        val editor = lockSharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported data type")
        }

        editor.apply()
    }
    // Read preference with exception handling
    fun <T : Any> getLockPreference(key: String, defaultValue: T): T {
        return try {
            val sharedPreferences =lockSharedPreferences

            when (defaultValue) {
                is String -> sharedPreferences.getString(key, defaultValue) as? T ?: defaultValue
                is Int -> sharedPreferences.getInt(key, defaultValue) as T
                is Long -> sharedPreferences.getLong(key, defaultValue) as T
                is Float -> sharedPreferences.getFloat(key, defaultValue) as T
                is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
                else -> throw IllegalArgumentException("Unsupported data type")
            }
        } catch (e: Exception) {
            defaultValue
        }
    }
}