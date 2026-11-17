package com.islabuy.camiguin.util

import org.junit.Assert.*
import org.junit.Test

class CategoriesTest {

    @Test
    fun `has 12 initial categories`() {
        assertEquals(12, Categories.ALL.size)
    }

    @Test
    fun `fruits is perishable`() {
        assertTrue(Categories.isPerishableCategory("Fruits"))
    }

    @Test
    fun `vegetables is perishable`() {
        assertTrue(Categories.isPerishableCategory("Vegetables"))
    }

    @Test
    fun `fish and seafood is perishable`() {
        assertTrue(Categories.isPerishableCategory("Fish and Seafood"))
    }

    @Test
    fun `meat and poultry is perishable`() {
        assertTrue(Categories.isPerishableCategory("Meat and Poultry"))
    }

    @Test
    fun `fresh food is perishable`() {
        assertTrue(Categories.isPerishableCategory("Fresh Food"))
    }

    @Test
    fun `groceries is not perishable`() {
        assertFalse(Categories.isPerishableCategory("Groceries"))
    }

    @Test
    fun `electronics is not perishable`() {
        assertFalse(Categories.isPerishableCategory("Electronics"))
    }

    @Test
    fun `clothing is not perishable`() {
        assertFalse(Categories.isPerishableCategory("Clothing"))
    }

    @Test
    fun `contains expected categories`() {
        assertTrue(Categories.ALL.contains("Fruits"))
        assertTrue(Categories.ALL.contains("Vegetables"))
        assertTrue(Categories.ALL.contains("Fish and Seafood"))
        assertTrue(Categories.ALL.contains("Meat and Poultry"))
        assertTrue(Categories.ALL.contains("Fresh Food"))
        assertTrue(Categories.ALL.contains("Rice and Agricultural Products"))
        assertTrue(Categories.ALL.contains("Groceries"))
        assertTrue(Categories.ALL.contains("Household Items"))
        assertTrue(Categories.ALL.contains("Clothing"))
        assertTrue(Categories.ALL.contains("Electronics"))
        assertTrue(Categories.ALL.contains("Local Products"))
        assertTrue(Categories.ALL.contains("Other Products"))
    }
}
