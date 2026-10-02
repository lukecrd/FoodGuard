package com.hightech.foodguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hightech.foodguard.data.FoodDatabase
import com.hightech.foodguard.data.FoodRepository
import com.hightech.foodguard.ui.navigation.AppNavigation
import com.hightech.foodguard.ui.theme.FoodGuardTheme
import com.hightech.foodguard.viewmodel.FoodViewModel
import com.hightech.foodguard.viewmodel.ScanViewModel
import com.hightech.foodguard.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = FoodDatabase.getInstance(applicationContext)
        val foodRepository = FoodRepository(database.foodDao())
        val mealRepository = com.hightech.foodguard.data.MealRepository(database.mealGuidelineDao())
        val factory = ViewModelFactory(foodRepository, mealRepository)

        setContent {
            FoodGuardTheme {
                val foodViewModel: FoodViewModel = viewModel(factory = factory)
                val scanViewModel: ScanViewModel = viewModel(factory = factory)
                val mealViewModel: com.hightech.foodguard.viewmodel.MealViewModel = viewModel(factory = factory)
                AppNavigation(
                    foodViewModel = foodViewModel,
                    scanViewModel = scanViewModel,
                    mealViewModel = mealViewModel
                )
            }
        }
    }
}
