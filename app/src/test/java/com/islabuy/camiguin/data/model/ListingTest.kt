package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class ListingTest {

    @Test
    fun `non-perishable listing is never expired`() {
        val listing = Listing(isPerishable = false, status = "active")
        assertFalse(listing.isExpired())
        assertTrue(listing.isActive())
    }

    @Test
    fun `perishable listing with future expiry is not expired`() {
        val futureDate = java.time.LocalDate.now().plusDays(2).toString()
        val listing = Listing(
            isPerishable = true,
            expirationDate = futureDate,
            expirationTime = "23:59",
            status = "active"
        )
        assertFalse(listing.isExpired())
        assertTrue(listing.isActive())
    }

    @Test
    fun `perishable listing with past expiry is expired`() {
        val pastDate = java.time.LocalDate.now().minusDays(1).toString()
        val listing = Listing(
            isPerishable = true,
            expirationDate = pastDate,
            expirationTime = "00:00",
            status = "active"
        )
        assertTrue(listing.isExpired())
        assertFalse(listing.isActive())
    }

    @Test
    fun `perishable listing without expiration date is not expired`() {
        val listing = Listing(isPerishable = true, expirationDate = null, expirationTime = null, status = "active")
        assertFalse(listing.isExpired())
    }

    @Test
    fun `sold listing is not active`() {
        val listing = Listing(status = "sold")
        assertFalse(listing.isActive())
    }

    @Test
    fun `default listing has empty id and zero price`() {
        val listing = Listing()
        assertEquals("", listing.id)
        assertEquals(0.0, listing.price, 0.001)
        assertEquals(0L, listing.createdAt)
        assertNull(listing.latitude)
        assertNull(listing.longitude)
    }

    @Test
    fun `listing preserves price as double`() {
        val listing = Listing(price = 150.50)
        assertEquals(150.50, listing.price, 0.001)
    }
}
