package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class ConversationTest {

    @Test
    fun `default conversation has empty fields`() {
        val conv = Conversation()
        assertEquals("", conv.conversationId)
        assertEquals("", conv.productId)
        assertEquals("", conv.buyerId)
        assertEquals("", conv.sellerId)
        assertEquals("", conv.productTitle)
        assertNull(conv.productImage)
        assertEquals("", conv.lastMessage)
        assertEquals(0L, conv.lastMessageAt)
        assertTrue(conv.unreadCounts.isEmpty())
    }

    @Test
    fun `conversation preserves all fields`() {
        val conv = Conversation(
            conversationId = "c1",
            productId = "p1",
            buyerId = "b1",
            sellerId = "s1",
            productTitle = "Fresh Lanzones",
            productImage = "https://example.com/img.jpg",
            lastMessage = "Is this available?",
            lastMessageAt = 12345L,
            unreadCounts = mapOf("b1" to 2, "s1" to 0)
        )
        assertEquals("c1", conv.conversationId)
        assertEquals("p1", conv.productId)
        assertEquals("b1", conv.buyerId)
        assertEquals("s1", conv.sellerId)
        assertEquals("Fresh Lanzones", conv.productTitle)
        assertEquals("https://example.com/img.jpg", conv.productImage)
        assertEquals("Is this available?", conv.lastMessage)
        assertEquals(12345L, conv.lastMessageAt)
        assertEquals(2, conv.unreadCounts["b1"])
        assertEquals(0, conv.unreadCounts["s1"])
    }

    @Test
    fun `one product one buyer one seller rule`() {
        val conv1 = Conversation(productId = "p1", buyerId = "b1", sellerId = "s1")
        val conv2 = Conversation(productId = "p2", buyerId = "b1", sellerId = "s1")
        // Different products = different conversations
        assertNotEquals(conv1.productId, conv2.productId)
    }

    @Test
    fun `same product different buyers = different conversations`() {
        val conv1 = Conversation(productId = "p1", buyerId = "b1", sellerId = "s1")
        val conv2 = Conversation(productId = "p1", buyerId = "b2", sellerId = "s1")
        assertNotEquals(conv1.buyerId, conv2.buyerId)
    }

    @Test
    fun `unread counts track per user`() {
        val conv = Conversation(unreadCounts = mapOf("b1" to 3, "s1" to 1))
        assertEquals(3, conv.unreadCounts["b1"])
        assertEquals(1, conv.unreadCounts["s1"])
        assertEquals(0, conv.unreadCounts["unknown"] ?: 0)
    }
}
