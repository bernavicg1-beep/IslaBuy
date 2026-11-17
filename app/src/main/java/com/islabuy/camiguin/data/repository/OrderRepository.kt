package com.islabuy.camiguin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.islabuy.camiguin.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val orders = db.collection("orders")

    suspend fun createOrder(order: Order): String {
        val doc = orders.add(order).await()
        return doc.id
    }

    suspend fun getOrder(orderId: String): Order? =
        orders.document(orderId).get().await()
            .toObject(Order::class.java)?.copy(orderId = orderId)

    fun ordersForBuyer(buyerId: String) = orders
        .whereEqualTo("buyerId", buyerId)
        .orderBy("createdAt", Query.Direction.DESCENDING)

    fun ordersForSeller(sellerId: String) = orders
        .whereEqualTo("sellerId", sellerId)
        .orderBy("createdAt", Query.Direction.DESCENDING)

    suspend fun updateOrderStatus(orderId: String, status: String) {
        orders.document(orderId).update("orderStatus", status).await()
    }

    suspend fun updatePaymentStatus(orderId: String, status: String) {
        orders.document(orderId).update("paymentStatus", status).await()
    }

    suspend fun verifyGcashPayment(orderId: String, verified: Boolean) {
        val status = if (verified) "VERIFIED" else "REJECTED"
        val orderStatus = if (verified) "CONFIRMED" else "PENDING"
        orders.document(orderId).update(
            mapOf(
                "paymentStatus" to status,
                "orderStatus" to orderStatus
            )
        ).await()
    }
}