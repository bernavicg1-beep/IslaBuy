package com.islabuy.camiguin.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_prefs")

class NotificationPreferences(private val context: Context) {
    companion object {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val MESSAGE_SOUNDS_ENABLED = booleanPreferencesKey("message_sounds_enabled")
        val PRODUCT_UPDATES_ENABLED = booleanPreferencesKey("product_updates_enabled")
        val ANNOUNCEMENTS_ENABLED = booleanPreferencesKey("announcements_enabled")
    }

    val notificationsEnabled: Flow<Boolean> = context.notificationDataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val messageSoundsEnabled: Flow<Boolean> = context.notificationDataStore.data.map { it[MESSAGE_SOUNDS_ENABLED] ?: true }
    val productUpdatesEnabled: Flow<Boolean> = context.notificationDataStore.data.map { it[PRODUCT_UPDATES_ENABLED] ?: true }
    val announcementsEnabled: Flow<Boolean> = context.notificationDataStore.data.map { it[ANNOUNCEMENTS_ENABLED] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setMessageSoundsEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[MESSAGE_SOUNDS_ENABLED] = enabled }
    }

    suspend fun setProductUpdatesEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[PRODUCT_UPDATES_ENABLED] = enabled }
    }

    suspend fun setAnnouncementsEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { it[ANNOUNCEMENTS_ENABLED] = enabled }
    }
}
