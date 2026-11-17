package com.islabuy.camiguin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.islabuy.camiguin.data.model.Listing
import kotlinx.coroutines.tasks.await

class ListingRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val collection = db.collection("listings")

    suspend fun createListing(listing: Listing): Result<String> = runCatching {
        val doc = collection.add(listing).await()
        doc.id
    }

    suspend fun getListing(id: String): Listing? = runCatching {
        collection.document(id).get().await().toObject(Listing::class.java)
    }.getOrNull()

    suspend fun getActiveListings(pageSize: Int = 20, lastCreatedAt: Long? = null): List<Listing> {
        var query: Query = collection
            .whereEqualTo("status", "active")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())
        if (lastCreatedAt != null) {
            query = query.startAfter(lastCreatedAt)
        }
        return query.get().await().documents.mapNotNull { it.toObject(Listing::class.java)?.copy(id = it.id) }
            .filter { !it.isExpired() }
    }

    suspend fun searchListings(query: String): List<Listing> {
        // Firestore doesn't support full-text search natively; use prefix matching on title
        return collection
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + "\uf8ff")
            .limit(50)
            .get().await().documents.mapNotNull { it.toObject(Listing::class.java)?.copy(id = it.id) }
            .filter { it.isActive() }
    }

    suspend fun getListingsBySeller(sellerId: String): List<Listing> {
        return collection
            .whereEqualTo("sellerId", sellerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull { it.toObject(Listing::class.java)?.copy(id = it.id) }
    }

    suspend fun getListingsByCategory(category: String): List<Listing> {
        return collection
            .whereEqualTo("category", category)
            .whereEqualTo("status", "active")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get().await().documents.mapNotNull { it.toObject(Listing::class.java)?.copy(id = it.id) }
            .filter { !it.isExpired() }
    }

    suspend fun updateListing(id: String, updates: Map<String, Any>): Result<Unit> = runCatching {
        collection.document(id).update(updates).await()
    }

    suspend fun deleteListing(id: String): Result<Unit> = runCatching {
        collection.document(id).delete().await()
    }

    suspend fun markAsSold(id: String): Result<Unit> = runCatching {
        collection.document(id).update("status", "sold").await()
    }

    suspend fun getExpiredListings(): List<Listing> {
        return collection
            .whereEqualTo("status", "active")
            .get().await().documents.mapNotNull { it.toObject(Listing::class.java)?.copy(id = it.id) }
            .filter { it.isExpired() }
    }
}
