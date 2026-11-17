package com.islabuy.camiguin.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.islabuy.camiguin.MainActivity
import com.islabuy.camiguin.R

/**
 * In-app notifications for the Firebase Spark (free) plan.
 *
 * The app listens to its own `notifications` documents with a Firestore
 * snapshot listener and shows a local notification, so no Cloud Functions
 * or server are required.
 */
object LocalNotifications {

    fun show(context: Context, id: Int, title: String, body: String, type: String) {
        val channelId = when (type.lowercase()) {
            "message" -> "messages"
            "announcement" -> "announcements"
            else -> "products"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }
}