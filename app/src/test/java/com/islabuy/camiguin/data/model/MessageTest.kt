package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class MessageTest {

    @Test
    fun `default message has empty fields`() {
        val msg = Message()
        assertEquals("", msg.messageId)
        assertEquals("", msg.conversationId)
        assertEquals("", msg.senderId)
        assertEquals("", msg.receiverId)
        assertEquals("", msg.text)
        assertEquals(0L, msg.createdAt)
        assertEquals("sent", msg.status)
        assertTrue(msg.deletedFor.isEmpty())
        assertFalse(msg.deletedForEveryone)
    }

    @Test
    fun `message with deletedForEveryone is marked deleted`() {
        val msg = Message(deletedForEveryone = true)
        assertTrue(msg.deletedForEveryone)
    }

    @Test
    fun `message deleted for specific user`() {
        val msg = Message(deletedFor = listOf("user1"))
        assertTrue(msg.deletedFor.contains("user1"))
        assertFalse(msg.deletedFor.contains("user2"))
    }

    @Test
    fun `message status can be delivered or seen`() {
        val delivered = Message(status = "delivered")
        val seen = Message(status = "seen")
        assertEquals("delivered", delivered.status)
        assertEquals("seen", seen.status)
    }

    @Test
    fun `conversation one-per-product rule fields`() {
        val conv = Conversation(
            productId = "p1",
            buyerId = "b1",
            sellerId = "s1"
        )
        assertEquals("p1", conv.productId)
        assertEquals("b1", conv.buyerId)
        assertEquals("s1", conv.sellerId)
    }

    @Test
    fun `conversation unread counts default empty`() {
        val conv = Conversation()
        assertTrue(conv.unreadCounts.isEmpty())
    }
}
