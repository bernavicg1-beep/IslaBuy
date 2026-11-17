package com.islabuy.camiguin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.islabuy.camiguin.data.model.AppNotification
import kotlinx.coroutines.tasks.await

class NotificationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val notifications = db.collection("notifications")

    suspend fun send(notification: AppNotification): String =
        notifications.add(notification).await().id

    fun forUser(userId: String) = notifications
        .whereEqualTo("userId", userId)
        .orderBy("createdAt", Query.Direction.DESCENDING)

    suspend fun markRead(notificationId: String) {
        notifications.document(notificationId).update("isRead", true).await()
    }

    suspend fun markAllRead(userId: String) {
        val snap = notifications.whereEqualTo("userId", userId).whereEqualTo("isRead", false).get().await()
        for (doc in snap.documents) {
            doc.reference.update("read", true).await()
        }
    }

    suspend fun unreadCount(userId: String): Int {
        val snap = notifications.whereEqualTo("userId", userId).whereEqualTo("isRead", false).get().await()
        return snap.size()
    }
}