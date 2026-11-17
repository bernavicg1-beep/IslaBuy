package com.islabuy.camiguin.data.model

data class Store(
    val storeId: String = "",
    val sellerId: String = "",
    val storeName: String = "",
    val storeLogo: String? = null,
    val storeBanner: String? = null,
    val description: String = "",
    val location: String = "",
    val createdAt: Long = 0L,
    val status: String = "active"
)
