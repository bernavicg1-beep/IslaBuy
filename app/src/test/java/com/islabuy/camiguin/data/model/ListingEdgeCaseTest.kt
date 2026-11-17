package com.islabuy.camiguin.data.model

import org.junit.Assert.*
import org.junit.Test

class ListingEdgeCaseTest {

    @Test
    fun `listing with null coordinates`() {
        val listing = Listing(latitude = null, longitude = null)
        assertNull(listing.latitude)
        assertNull(listing.longitude)
    }

    @Test
    fun `listing with valid coordinates`() {
        val listing = Listing(latitude = 9.1731, longitude = 124.7284)
        assertEquals(9.1731, listing.latitude!!, 0.0001)
        assertEquals(124.7284, listing.longitude!!, 0.0001)
    }

    @Test
    fun `listing with empty image list`() {
        val listing = Listing(imageUrls = emptyList(), cloudinaryPublicIds = emptyList())
        assertTrue(listing.imageUrls.isEmpty())
        assertTrue(listing.cloudinaryPublicIds.isEmpty())
    }

    @Test
    fun `listing with max 5 images`() {
        val urls = (1..5).map { "https://example.com/img$it.jpg" }
        val ids = (1..5).map { "public_id_$it" }
        val listing = Listing(imageUrls = urls, cloudinaryPublicIds = ids)
        assertEquals(5, listing.imageUrls.size)
        assertEquals(5, listing.cloudinaryPublicIds.size)
    }

    @Test
    fun `listing with video urls`() {
        val listing = Listing(
            videoUrls = listOf("https://example.com/video.mp4"),
            cloudinaryVideoPublicIds = listOf("video_id_1")
        )
        assertEquals(1, listing.videoUrls.size)
        assertEquals(1, listing.cloudinaryVideoPublicIds.size)
    }

    @Test
    fun `expired listing is not active`() {
        val listing = Listing(
            isPerishable = true,
            expirationDate = "2020-01-01",
            expirationTime = "00:00",
            status = "active"
        )
        assertTrue(listing.isExpired())
        assertFalse(listing.isActive())
    }

    @Test
    fun `non-perishable active listing is active`() {
        val listing = Listing(isPerishable = false, status = "active")
        assertFalse(listing.isExpired())
        assertTrue(listing.isActive())
    }

    @Test
    fun `listing with zero quantity`() {
        val listing = Listing(quantity = 0)
        assertEquals(0, listing.quantity)
    }

    @Test
    fun `listing price can be zero`() {
        val listing = Listing(price = 0.0)
        assertEquals(0.0, listing.price, 0.001)
    }

    @Test
    fun `listing with pickup address`() {
        val listing = Listing(pickupAddress = "Mambajao, Camiguin")
        assertEquals("Mambajao, Camiguin", listing.pickupAddress)
    }
}
