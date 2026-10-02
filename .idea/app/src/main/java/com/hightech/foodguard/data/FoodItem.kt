package com.hightech.foodguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FoodStatus { VIETATO, CONSENTITO }

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,           // es. "arachidi", "glutine", "lattosio"
    val status: FoodStatus,     // VIETATO oppure CONSENTITO
    val isAllergen: Boolean = false, // se true, viene incrociato anche con i tag "allergens" del prodotto
    val note: String = ""
)
