package com.halam.gallerity.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    val authMethodFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_METHOD_KEY]
    }

    val isFirstLaunchFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }

    suspend fun savePinCode(pin: String) {
        dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }

    suspend fun saveAuthMethod(method: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_METHOD_KEY] = method
        }
    }

    suspend fun setFirstLaunchCompleted() {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = false
        }
    }

    companion object {
        const val AUTH_PIN = "pin"
        const val AUTH_FINGERPRINT = "fingerprint"

        private val PIN_KEY = stringPreferencesKey("security_pin")
        private val AUTH_METHOD_KEY = stringPreferencesKey("auth_method")
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    }
}
