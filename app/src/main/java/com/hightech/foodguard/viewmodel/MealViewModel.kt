package com.hightech.foodguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightech.foodguard.data.MealGuideline
import com.hightech.foodguard.data.MealImportExportHelper
import com.hightech.foodguard.data.MealRepository
import com.hightech.foodguard.data.MealType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    val meals: StateFlow<List<MealGuideline>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMeal(
        mealType: MealType,
        title: String,
        components: String,
        guidelines: String = "",
        dayOrTag: String = ""
    ) {
        if (title.isBlank() && components.isBlank()) return
        viewModelScope.launch {
            repository.add(
                MealGuideline(
                    mealType = mealType,
                    title = if (title.isBlank()) "${mealType.displayName} Bilanciato" else title.trim(),
                    components = components.trim(),
                    guidelines = guidelines.trim(),
                    dayOrTag = dayOrTag.trim()
                )
            )
        }
    }

    fun importMeals(items: List<MealGuideline>) {
        if (items.isEmpty()) return
        viewModelScope.launch {
            repository.addAll(items)
        }
    }

    fun updateMeal(item: MealGuideline) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteMeal(item: MealGuideline) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun populateSampleGuidelines() {
        viewModelScope.launch {
            repository.addAll(MealImportExportHelper.getDefaultSampleMeals())
        }
    }
}
