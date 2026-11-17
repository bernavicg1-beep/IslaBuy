package com.islabuy.camiguin.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val role: String = "buyer", // buyer, seller, admin
    val createdAt: Long = 0L
) {
    fun isAdmin(): Boolean = role == "admin"
    fun isSeller(): Boolean = role == "seller" || role == "admin"
}
