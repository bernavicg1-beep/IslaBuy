package com.islabuy.camiguin.util

object Categories {
    val ALL = listOf(
        "Fruits",
        "Vegetables",
        "Fish and Seafood",
        "Meat and Poultry",
        "Fresh Food",
        "Rice and Agricultural Products",
        "Groceries",
        "Household Items",
        "Clothing",
        "Electronics",
        "Local Products",
        "Other Products"
    )

    val PERISHABLE = setOf(
        "Fruits",
        "Vegetables",
        "Fish and Seafood",
        "Meat and Poultry",
        "Fresh Food"
    )

    fun isPerishableCategory(category: String): Boolean = category in PERISHABLE
}
