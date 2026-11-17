package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class AnnouncementTest {

    @Test
    fun `default announcement is active`() {
        val ann = Announcement()
        assertTrue(ann.isActive)
        assertEquals("", ann.announcementId)
        assertEquals("", ann.title)
        assertEquals("", ann.message)
        assertEquals(0L, ann.createdAt)
        assertEquals(0L, ann.updatedAt)
    }

    @Test
    fun `inactive announcement`() {
        val ann = Announcement(isActive = false)
        assertFalse(ann.isActive)
    }

    @Test
    fun `announcement preserves fields`() {
        val ann = Announcement(
            announcementId = "a1",
            title = "Welcome",
            message = "Welcome to IslaBuy!",
            isActive = true,
            createdAt = 1000L,
            updatedAt = 2000L
        )
        assertEquals("a1", ann.announcementId)
        assertEquals("Welcome", ann.title)
        assertEquals("Welcome to IslaBuy!", ann.message)
        assertTrue(ann.isActive)
        assertEquals(1000L, ann.createdAt)
        assertEquals(2000L, ann.updatedAt)
    }
}
