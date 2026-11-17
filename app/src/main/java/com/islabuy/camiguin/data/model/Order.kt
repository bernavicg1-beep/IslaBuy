package com.islabuy.camiguin.data.model

data class Order(
    val orderId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImage: String? = null,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "CASH", // CASH or GCASH
    val paymentStatus: String = "PENDING", // PENDING, PENDING_VERIFICATION, VERIFIED, REJECTED
    val orderStatus: String = "PENDING", // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    val deliveryAddress: String = "",
    val gcashReferenceNumber: String? = null,
    val paymentProofUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class CartItem(
    val productId: String = "",
    val productName: String = "",
    val productImage: String? = null,
    val sellerId: String = "",
    val sellerName: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 1,
    val available: Boolean = true,
    val selected: Boolean = true
) {
    val subtotal: Double get() = unitPrice * quantity
}