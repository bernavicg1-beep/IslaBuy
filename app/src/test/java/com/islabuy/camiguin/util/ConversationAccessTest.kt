package com.islabuy.camiguin.util

import com.islabuy.camiguin.data.model.Conversation
import org.junit.Assert.*
import org.junit.Test

class ConversationAccessTest {

    private val conversation = Conversation(
        conversationId = "c1",
        productId = "p1",
        buyerId = "buyer",
        sellerId = "seller"
    )

    @Test
    fun `buyer is a member`() {
        assertTrue(ConversationAccess.isMember(conversation, "buyer"))
    }

    @Test
    fun `seller is a member`() {
        assertTrue(ConversationAccess.isMember(conversation, "seller"))
    }

    @Test
    fun `stranger is not a member`() {
        assertFalse(ConversationAccess.isMember(conversation, "stranger"))
    }

    @Test
    fun `blank user is not a member`() {
        assertFalse(ConversationAccess.isMember(conversation, ""))
        assertFalse(ConversationAccess.isMember(Conversation(), ""))
    }

    @Test
    fun `counterpart of buyer is seller`() {
        assertEquals("seller", ConversationAccess.counterpartOf(conversation, "buyer"))
    }

    @Test
    fun `counterpart of seller is buyer`() {
        assertEquals("buyer", ConversationAccess.counterpartOf(conversation, "seller"))
    }

    @Test
    fun `counterpart of stranger is null`() {
        assertNull(ConversationAccess.counterpartOf(conversation, "stranger"))
    }

    @Test
    fun `conversation key format`() {
        assertEquals("p1:b1:s1", ConversationAccess.conversationKey("p1", "b1", "s1"))
    }

    @Test
    fun `unique conversation check`() {
        val existing = setOf("p1:b1:s1", "p2:b1:s1")
        assertTrue(ConversationAccess.isUniqueConversation(existing, "p3", "b1", "s1"))
        assertFalse(ConversationAccess.isUniqueConversation(existing, "p1", "b1", "s1"))
    }

    @Test
    fun `same product different buyer is unique`() {
        val existing = setOf("p1:b1:s1")
        assertTrue(ConversationAccess.isUniqueConversation(existing, "p1", "b2", "s1"))
    }
}
