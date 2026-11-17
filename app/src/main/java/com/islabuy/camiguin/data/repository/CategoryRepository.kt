package com.islabuy.camiguin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.islabuy.camiguin.data.model.Category
import com.islabuy.camiguin.data.model.UserLocation
import kotlinx.coroutines.tasks.await

class CategoryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val categories = db.collection("categories")

    suspend fun getAll(): List<Category> =
        categories.get().await().documents.mapNotNull { it.toObject(Category::class.java)?.copy(categoryId = it.id) }

    suspend fun add(category: Category): String = categories.add(category).await().id

    suspend fun delete(categoryId: String) { categories.document(categoryId).delete().await() }
}

class UserLocationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val locations = db.collection("userLocations")

    suspend fun update(location: UserLocation) {
        locations.document(location.userId).set(location).await()
    }

    suspend fun get(userId: String): UserLocation? =
        locations.document(userId).get().await().toObject(UserLocation::class.java)

    suspend fun setSharing(userId: String, sharing: Boolean) {
        locations.document(userId).update("sharing", sharing).await()
    }
}