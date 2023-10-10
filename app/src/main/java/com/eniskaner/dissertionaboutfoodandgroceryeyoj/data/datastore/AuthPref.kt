package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class AuthPref(var context: Context) {
    val Context.ds: DataStore<Preferences> by preferencesDataStore("auth")

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("USERNAME")
        val USER_PHONE_KEY = stringPreferencesKey("USERPHONE")
        val USER_EMAIL_KEY = stringPreferencesKey("USEREMAIL")
        val USER_PASSWORD_KEY = stringPreferencesKey("USERPASSWORD")
    }

    suspend fun userData(userName: String, userPassword: String, userPhone: String, userEmail: String) {
        context.ds.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
            preferences[USER_PHONE_KEY] = userPhone
            preferences[USER_EMAIL_KEY] = userEmail
            preferences[USER_PASSWORD_KEY] = userPassword
        }
    }

    suspend fun readUserName(): String {
        val p = context.ds.data.first()
        return  p[USER_NAME_KEY] ?: "name do not exist"
    }
}