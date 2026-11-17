package com.islabuy.camiguin.util

import org.junit.Assert.*
import org.junit.Test

class ListingValidatorTest {

    @Test
    fun `valid title`() {
        assertTrue(ListingValidator.isValidTitle("Fresh Lanzones"))
        assertTrue(ListingValidator.isValidTitle("A"))
    }

    @Test
    fun `blank title is invalid`() {
        assertFalse(ListingValidator.isValidTitle(""))
        assertFalse(ListingValidator.isValidTitle("   "))
    }

    @Test
    fun `title over 100 chars is invalid`() {
        assertFalse(ListingValidator.isValidTitle("A".repeat(101)))
    }

    @Test
    fun `valid price`() {
        assertTrue(ListingValidator.isValidPrice(0.0))
        assertTrue(ListingValidator.isValidPrice(150.50))
    }

    @Test
    fun `negative price is invalid`() {
        assertFalse(ListingValidator.isValidPrice(-1.0))
    }

    @Test
    fun `valid quantity`() {
        assertTrue(ListingValidator.isValidQuantity(1))
        assertTrue(ListingValidator.isValidQuantity(100))
    }

    @Test
    fun `zero or negative quantity is invalid`() {
        assertFalse(ListingValidator.isValidQuantity(0))
        assertFalse(ListingValidator.isValidQuantity(-1))
    }

    @Test
    fun `perishable requires expiration`() {
        assertTrue(ListingValidator.requiresExpiration(true))
        assertFalse(ListingValidator.requiresExpiration(false))
    }

    @Test
    fun `valid expiration for perishable`() {
        assertTrue(ListingValidator.isValidExpiration(true, "2026-12-25", "18:00"))
    }

    @Test
    fun `missing expiration for perishable is invalid`() {
        assertFalse(ListingValidator.isValidExpiration(true, null, "18:00"))
        assertFalse(ListingValidator.isValidExpiration(true, "2026-12-25", null))
        assertFalse(ListingValidator.isValidExpiration(true, "", "18:00"))
    }

    @Test
    fun `non-perishable does not need expiration`() {
        assertTrue(ListingValidator.isValidExpiration(false, null, null))
    }

    @Test
    fun `valid image count 0 to 5`() {
        assertTrue(ListingValidator.isValidImageCount(0))
        assertTrue(ListingValidator.isValidImageCount(3))
        assertTrue(ListingValidator.isValidImageCount(5))
    }

    @Test
    fun `image count over 5 is invalid`() {
        assertFalse(ListingValidator.isValidImageCount(6))
        assertFalse(ListingValidator.isValidImageCount(-1))
    }

    @Test
    fun `valid category`() {
        assertTrue(ListingValidator.isValidCategory("Fruits"))
        assertTrue(ListingValidator.isValidCategory("Other Products"))
    }

    @Test
    fun `invalid category`() {
        assertFalse(ListingValidator.isValidCategory("Invalid Category"))
        assertFalse(ListingValidator.isValidCategory(""))
    }

    @Test
    fun `canPublish with valid data`() {
        assertTrue(ListingValidator.canPublish(
            title = "Fresh Lanzones",
            price = 150.0,
            quantity = 10,
            category = "Fruits",
            isPerishable = true,
            expirationDate = "2026-12-25",
            expirationTime = "18:00",
            imageCount = 3
        ))
    }

    @Test
    fun `cannot publish perishable without expiration`() {
        assertFalse(ListingValidator.canPublish(
            title = "Fresh Lanzones",
            price = 150.0,
            quantity = 10,
            category = "Fruits",
            isPerishable = true,
            expirationDate = null,
            expirationTime = null,
            imageCount = 3
        ))
    }

    @Test
    fun `cannot publish with no images over limit`() {
        assertFalse(ListingValidator.canPublish(
            title = "Fresh Lanzones",
            price = 150.0,
            quantity = 10,
            category = "Fruits",
            isPerishable = false,
            expirationDate = null,
            expirationTime = null,
            imageCount = 6
        ))
    }

    @Test
    fun `can publish non-perishable without expiration`() {
        assertTrue(ListingValidator.canPublish(
            title = "Peanuts",
            price = 80.0,
            quantity = 5,
            category = "Local Products",
            isPerishable = false,
            expirationDate = null,
            expirationTime = null,
            imageCount = 1
        ))
    }
}
