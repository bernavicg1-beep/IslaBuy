package com.islabuy.camiguin.util

import org.junit.Assert.*
import org.junit.Test

class MessageValidatorTest {

    @Test
    fun `valid message text`() {
        assertTrue(MessageValidator.isValidMessage("Hello"))
        assertTrue(MessageValidator.isValidMessage("A".repeat(5000)))
    }

    @Test
    fun `blank message is invalid`() {
        assertFalse(MessageValidator.isValidMessage(""))
        assertFalse(MessageValidator.isValidMessage("   "))
    }

    @Test
    fun `message over 5000 chars is invalid`() {
        assertFalse(MessageValidator.isValidMessage("A".repeat(5001)))
    }

    @Test
    fun `sender can delete for everyone`() {
        assertTrue(MessageValidator.canDeleteForEveryone("user1", "user1"))
    }

    @Test
    fun `non-sender cannot delete for everyone`() {
        assertFalse(MessageValidator.canDeleteForEveryone("user1", "user2"))
    }

    @Test
    fun `participant can delete for me`() {
        assertTrue(MessageValidator.canDeleteForMe("user1", listOf("user1", "user2")))
    }

    @Test
    fun `non-participant cannot delete for me`() {
        assertFalse(MessageValidator.canDeleteForMe("user3", listOf("user1", "user2")))
    }

    @Test
    fun `conversation key format`() {
        assertEquals("p1:b1:s1", MessageValidator.conversationKey("p1", "b1", "s1"))
    }

    @Test
    fun `unique conversation check`() {
        val existing = setOf("p1:b1:s1", "p2:b1:s1")
        assertTrue(MessageValidator.isUniqueConversation(existing, "p3", "b1", "s1"))
        assertFalse(MessageValidator.isUniqueConversation(existing, "p1", "b1", "s1"))
    }

    @Test
    fun `same product different buyer is unique`() {
        val existing = setOf("p1:b1:s1")
        assertTrue(MessageValidator.isUniqueConversation(existing, "p1", "b2", "s1"))
    }
}
