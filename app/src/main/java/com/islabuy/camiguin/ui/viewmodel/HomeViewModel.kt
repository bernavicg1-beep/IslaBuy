package com.islabuy.camiguin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.data.repository.ListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ListingRepository = ListingRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Listing>>(emptyList())
    val products: StateFlow<List<Listing>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var lastCreatedAt: Long? = null
    private val pageSize = 20

    init {
        loadProducts()
    }

    fun loadProducts(refresh: Boolean = false) {
        if (refresh) {
            lastCreatedAt = null
            _products.value = emptyList()
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newProducts = repository.getActiveListings(pageSize, lastCreatedAt)
                    .filter { !it.isExpired() } // Remove expired perishable products from feed
                if (newProducts.isNotEmpty()) {
                    lastCreatedAt = newProducts.last().createdAt
                }
                _products.value = if (refresh) newProducts else _products.value + newProducts
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadProducts(refresh = true)
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.searchListings(query)
                _products.value = results
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.getListingsByCategory(category)
                _products.value = results
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
