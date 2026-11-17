package com.islabuy.camiguin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp

class IslaBuyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channels = listOf(
                NotificationChannel("messages", "Messages", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "New message notifications"
                    enableVibration(true)
                },
                NotificationChannel("products", "Product Updates", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Product-related notifications"
                },
                NotificationChannel("announcements", "Announcements", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Admin announcements and news"
                }
            )
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }
}
