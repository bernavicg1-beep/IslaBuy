package com.islabuy.camiguin.util

object ListingValidator {
    fun isValidTitle(title: String): Boolean {
        return title.isNotBlank() && title.length <= 100
    }

    fun isValidPrice(price: Double): Boolean {
        return price >= 0
    }

    fun isValidQuantity(quantity: Int): Boolean {
        return quantity > 0
    }

    fun requiresExpiration(isPerishable: Boolean): Boolean {
        return isPerishable
    }

    fun isValidExpiration(isPerishable: Boolean, expirationDate: String?, expirationTime: String?): Boolean {
        if (!isPerishable) return true
        return !expirationDate.isNullOrBlank() && !expirationTime.isNullOrBlank()
    }

    fun isValidImageCount(count: Int): Boolean {
        return count in 0..5
    }

    fun isValidCategory(category: String): Boolean {
        return category in Categories.ALL
    }

    fun canPublish(
        title: String,
        price: Double,
        quantity: Int,
        category: String,
        isPerishable: Boolean,
        expirationDate: String?,
        expirationTime: String?,
        imageCount: Int
    ): Boolean {
        return isValidTitle(title) &&
            isValidPrice(price) &&
            isValidQuantity(quantity) &&
            isValidCategory(category) &&
            isValidExpiration(isPerishable, expirationDate, expirationTime) &&
            isValidImageCount(imageCount)
    }
}
