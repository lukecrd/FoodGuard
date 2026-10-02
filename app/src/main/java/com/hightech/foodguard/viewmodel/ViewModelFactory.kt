package com.hightech.foodguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hightech.foodguard.data.FoodRepository
import com.hightech.foodguard.data.MealRepository

class ViewModelFactory(
    private val foodRepository: FoodRepository,
    private val mealRepository: MealRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FoodViewModel::class.java) ->
                FoodViewModel(foodRepository) as T
            modelClass.isAssignableFrom(ScanViewModel::class.java) ->
                ScanViewModel(foodRepository) as T
            modelClass.isAssignableFrom(MealViewModel::class.java) ->
                MealViewModel(mealRepository) as T
            else -> throw IllegalArgumentException("ViewModel sconosciuto: ${modelClass.name}")
        }
    }
}
