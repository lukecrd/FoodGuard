package com.hightech.foodguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightech.foodguard.data.FoodItem
import com.hightech.foodguard.data.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodViewModel(private val repository: FoodRepository) : ViewModel() {

    val foods: StateFlow<List<FoodItem>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFood(name: String, status: com.hightech.foodguard.data.FoodStatus, isAllergen: Boolean, note: String = "") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.add(FoodItem(name = name.trim(), status = status, isAllergen = isAllergen, note = note))
        }
    }

    fun importFoods(items: List<FoodItem>) {
        if (items.isEmpty()) return
        viewModelScope.launch {
            repository.addAll(items)
        }
    }

    fun delete(item: FoodItem) {
        viewModelScope.launch { repository.delete(item) }
    }

    fun update(item: FoodItem) {
        viewModelScope.launch { repository.update(item) }
    }
}

