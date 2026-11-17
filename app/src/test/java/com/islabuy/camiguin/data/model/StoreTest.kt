package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class StoreTest {

    @Test
    fun `default store has empty fields`() {
        val store = Store()
        assertEquals("", store.storeId)
        assertEquals("", store.sellerId)
        assertEquals("", store.storeName)
        assertNull(store.storeLogo)
        assertNull(store.storeBanner)
        assertEquals("active", store.status)
        assertEquals(0L, store.createdAt)
    }

    @Test
    fun `store preserves all fields`() {
        val store = Store(
            storeId = "s1",
            sellerId = "u1",
            storeName = "Juan's Farm",
            storeLogo = "https://example.com/logo.jpg",
            storeBanner = "https://example.com/banner.jpg",
            description = "Fresh farm products",
            location = "Mambajao, Camiguin",
            createdAt = 12345L,
            status = "active"
        )
        assertEquals("s1", store.storeId)
        assertEquals("u1", store.sellerId)
        assertEquals("Juan's Farm", store.storeName)
        assertEquals("https://example.com/logo.jpg", store.storeLogo)
        assertEquals("Mambajao, Camiguin", store.location)
        assertEquals(12345L, store.createdAt)
    }

    @Test
    fun `suspended store has suspended status`() {
        val store = Store(status = "suspended")
        assertEquals("suspended", store.status)
    }
}
