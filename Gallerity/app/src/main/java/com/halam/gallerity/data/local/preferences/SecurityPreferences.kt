package com.halam.gallerity.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("security_prefs")

@Singleton
class SecurityPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    val pinCodeFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PIN_KEY]
    }

    suspend fun savePinCode(pin: String) {
        dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }

    companion object {
        private val PIN_KEY = stringPreferencesKey("security_pin")
    }
}
