package com.islabuy.camiguin.data.model

data class Category(
    val categoryId: String = "",
    val name: String = "",
    val icon: String = "",
    val order: Int = 0
)

data class UserLocation(
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val sharing: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

data class AdminLog(
    val logId: String = "",
    val adminId: String = "",
    val action: String = "",
    val targetId: String = "",
    val details: String = "",
    val createdAt: Long = System.currentTimeMillis()
)