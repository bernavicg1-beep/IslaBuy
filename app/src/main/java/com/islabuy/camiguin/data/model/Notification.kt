package com.islabuy.camiguin.data.model

data class AppNotification(
    val notificationId: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val message: String = "",
    val relatedProductId: String? = null,
    val relatedConversationId: String? = null,
    val relatedId: String? = null,
    val isRead: Boolean = false,
    val read: Boolean = false,
    val createdAt: Long = 0L
)

data class Announcement(
    val announcementId: String = "",
    val title: String = "",
    val message: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)