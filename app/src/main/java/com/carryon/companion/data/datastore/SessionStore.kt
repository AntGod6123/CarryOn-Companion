package com.carryon.companion.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionStore(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val baseUrl = stringPreferencesKey("server_base_url")
        val bearerToken = stringPreferencesKey("server_bearer_token")
        val serverName = stringPreferencesKey("server_name")
        val lastConnectedAt = longPreferencesKey("last_connected_at")
        val lastProfileId = stringPreferencesKey("last_profile_id")
    }

    val baseUrl: Flow<String?> = dataStore.data.map { it[Keys.baseUrl] }
    val bearerToken: Flow<String?> = dataStore.data.map { it[Keys.bearerToken] }
    val serverName: Flow<String?> = dataStore.data.map { it[Keys.serverName] }
    val lastConnectedAt: Flow<Long?> = dataStore.data.map { it[Keys.lastConnectedAt] }
    val lastProfileId: Flow<String?> = dataStore.data.map { it[Keys.lastProfileId] }

    suspend fun saveSession(
        baseUrl: String,
        bearerToken: String,
        serverName: String? = null,
        lastProfileId: String? = null,
    ) {
        dataStore.edit { prefs ->
            prefs[Keys.baseUrl] = baseUrl
            prefs[Keys.bearerToken] = bearerToken
            serverName?.let { prefs[Keys.serverName] = it }
            prefs[Keys.lastConnectedAt] = System.currentTimeMillis()
            lastProfileId?.let { prefs[Keys.lastProfileId] = it }
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.baseUrl)
            prefs.remove(Keys.bearerToken)
            prefs.remove(Keys.serverName)
            prefs.remove(Keys.lastConnectedAt)
            prefs.remove(Keys.lastProfileId)
        }
    }
}
