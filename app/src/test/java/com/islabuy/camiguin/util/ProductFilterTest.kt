package com.islabuy.camiguin.util

import com.islabuy.camiguin.data.model.Listing
import org.junit.Assert.*
import org.junit.Test

class ProductFilterTest {

    private val activeListing = Listing(id = "1", title = "Active", status = "active", createdAt = 1000L, category = "Fruits", sellerId = "s1", isPerishable = false)
    private val expiredListing = Listing(id = "2", title = "Expired", status = "active", isPerishable = true, expirationDate = "2020-01-01", expirationTime = "00:00", createdAt = 2000L, category = "Vegetables", sellerId = "s1")
    private val soldListing = Listing(id = "3", title = "Sold", status = "sold", createdAt = 3000L, category = "Fruits", sellerId = "s2")
    private val perishableListing = Listing(id = "4", title = "Perishable", isPerishable = true, status = "active", createdAt = 4000L, category = "Fish and Seafood", sellerId = "s2")
    private val allListings = listOf(activeListing, expiredListing, soldListing, perishableListing)

    @Test
    fun `filterActive returns only active non-expired`() {
        val result = ProductFilter.filterActive(allListings)
        assertEquals(2, result.size)
        assertTrue(result.contains(activeListing))
        assertTrue(result.contains(perishableListing))
    }

    @Test
    fun `filterExpired returns only expired`() {
        val result = ProductFilter.filterExpired(allListings)
        assertEquals(1, result.size)
        assertTrue(result.contains(expiredListing))
    }

    @Test
    fun `filterByCategory returns matching active products`() {
        val result = ProductFilter.filterByCategory(allListings, "Fruits")
        assertEquals(1, result.size)
        assertEquals("Active", result[0].title)
    }

    @Test
    fun `filterBySeller returns all products for seller`() {
        val result = ProductFilter.filterBySeller(allListings, "s1")
        assertEquals(2, result.size)
    }

    @Test
    fun `sortByNewest sorts descending by createdAt`() {
        val result = ProductFilter.sortByNewest(allListings)
        assertEquals("Perishable", result[0].title)
        assertEquals("Sold", result[1].title)
        assertEquals("Expired", result[2].title)
        assertEquals("Active", result[3].title)
    }

    @Test
    fun `filterPerishable returns only perishable`() {
        val result = ProductFilter.filterPerishable(allListings)
        assertEquals(2, result.size)
        assertTrue(result.contains(expiredListing))
        assertTrue(result.contains(perishableListing))
    }

    @Test
    fun `filterNonPerishable returns only non-perishable`() {
        val result = ProductFilter.filterNonPerishable(allListings)
        assertEquals(2, result.size)
        assertTrue(result.contains(activeListing))
        assertTrue(result.contains(soldListing))
    }

    @Test
    fun `empty list returns empty`() {
        val result = ProductFilter.filterActive(emptyList())
        assertTrue(result.isEmpty())
    }
}
