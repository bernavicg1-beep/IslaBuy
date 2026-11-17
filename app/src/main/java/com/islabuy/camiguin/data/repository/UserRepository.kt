package com.islabuy.camiguin.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.islabuy.camiguin.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun currentUser() = auth.currentUser

    suspend fun createUserProfile(user: User): Result<Unit> = runCatching {
        db.collection("users").document(user.uid).set(user).await()
    }

    suspend fun getUserProfile(uid: String): User? = runCatching {
        db.collection("users").document(uid).get().await().toObject(User::class.java)
    }.getOrNull()

    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Result<Unit> = runCatching {
        db.collection("users").document(uid).update(updates).await()
    }

    suspend fun deleteAccount(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No user logged in")
        val uid = user.uid

        // Delete user's listings
        val listings = db.collection("listings").whereEqualTo("sellerId", uid).get().await()
        for (doc in listings.documents) {
            doc.reference.delete().await()
        }

        // Delete user's stores
        val stores = db.collection("stores").whereEqualTo("sellerId", uid).get().await()
        for (doc in stores.documents) {
            doc.reference.delete().await()
        }

        // Delete user document
        db.collection("users").document(uid).delete().await()

        // Delete auth account
        user.delete().await()
    }

    suspend fun signIn(email: String, password: String) = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String) = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    fun signOut() = auth.signOut()
}
