package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class UserTest {

    @Test
    fun `default user is buyer`() {
        val user = User()
        assertEquals("buyer", user.role)
        assertFalse(user.isAdmin())
        assertFalse(user.isSeller())
    }

    @Test
    fun `admin user is admin and seller`() {
        val user = User(role = "admin")
        assertTrue(user.isAdmin())
        assertTrue(user.isSeller())
    }

    @Test
    fun `seller user is seller but not admin`() {
        val user = User(role = "seller")
        assertFalse(user.isAdmin())
        assertTrue(user.isSeller())
    }

    @Test
    fun `user with empty uid`() {
        val user = User()
        assertEquals("", user.uid)
        assertNull(user.profileImageUrl)
    }

    @Test
    fun `user fields preserved`() {
        val user = User(
            uid = "u1",
            name = "Juan",
            email = "juan@test.com",
            phone = "09171234567",
            role = "seller",
            createdAt = 12345L
        )
        assertEquals("u1", user.uid)
        assertEquals("Juan", user.name)
        assertEquals("juan@test.com", user.email)
        assertEquals("09171234567", user.phone)
        assertEquals(12345L, user.createdAt)
    }
}
