package com.islabuy.camiguin.data.model

data class Listing(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val quantity: Int = 1,
    val isPerishable: Boolean = false,
    val expirationDate: String? = null, // ISO date "yyyy-MM-dd"
    val expirationTime: String? = null, // "HH:mm"
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val status: String = "active", // active, sold, expired, deleted
    val latitude: Double? = null,
    val longitude: Double? = null,
    val pickupAddress: String? = null,
    val imageUrls: List<String> = emptyList(),
    val cloudinaryPublicIds: List<String> = emptyList(),
    val videoUrls: List<String> = emptyList(),
    val cloudinaryVideoPublicIds: List<String> = emptyList()
) {
    fun isExpired(): Boolean {
        if (!isPerishable) return false
        if (expirationDate == null || expirationTime == null) return false
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val expiry = sdf.parse("$expirationDate $expirationTime")
            expiry != null && expiry.before(java.util.Date())
        } catch (e: Exception) {
            false
        }
    }

    fun isActive(): Boolean = status == "active" && !isExpired()
}
