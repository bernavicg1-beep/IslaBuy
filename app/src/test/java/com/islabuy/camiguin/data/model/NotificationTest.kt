package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class NotificationTest {

    @Test
    fun `default notification is unread`() {
        val notif = AppNotification()
        assertFalse(notif.isRead)
        assertEquals("", notif.notificationId)
        assertEquals("", notif.userId)
        assertEquals("", notif.type)
        assertEquals("", notif.title)
        assertEquals("", notif.body)
        assertNull(notif.relatedProductId)
        assertNull(notif.relatedConversationId)
        assertEquals(0L, notif.createdAt)
    }

    @Test
    fun `read notification`() {
        val notif = AppNotification(isRead = true)
        assertTrue(notif.isRead)
    }

    @Test
    fun `notification with related product and conversation`() {
        val notif = AppNotification(
            notificationId = "n1",
            userId = "u1",
            type = "message",
            title = "New Message",
            body = "You have a new message",
            relatedProductId = "p1",
            relatedConversationId = "c1",
            isRead = false,
            createdAt = 5000L
        )
        assertEquals("n1", notif.notificationId)
        assertEquals("u1", notif.userId)
        assertEquals("message", notif.type)
        assertEquals("p1", notif.relatedProductId)
        assertEquals("c1", notif.relatedConversationId)
        assertFalse(notif.isRead)
        assertEquals(5000L, notif.createdAt)
    }
}
