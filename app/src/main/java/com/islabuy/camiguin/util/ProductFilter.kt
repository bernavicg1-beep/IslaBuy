package com.islabuy.camiguin.util

import com.islabuy.camiguin.data.model.Listing

object ProductFilter {
    fun filterActive(listings: List<Listing>): List<Listing> {
        return listings.filter { it.isActive() }
    }

    fun filterExpired(listings: List<Listing>): List<Listing> {
        return listings.filter { it.isExpired() }
    }

    fun filterByCategory(listings: List<Listing>, category: String): List<Listing> {
        return listings.filter { it.category == category && it.isActive() }
    }

    fun filterBySeller(listings: List<Listing>, sellerId: String): List<Listing> {
        return listings.filter { it.sellerId == sellerId }
    }

    fun sortByNewest(listings: List<Listing>): List<Listing> {
        return listings.sortedByDescending { it.createdAt }
    }

    fun filterPerishable(listings: List<Listing>): List<Listing> {
        return listings.filter { it.isPerishable }
    }

    fun filterNonPerishable(listings: List<Listing>): List<Listing> {
        return listings.filter { !it.isPerishable }
    }
}
