package com.hightech.foodguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hightech.foodguard.scanner.ScannerScreen
import com.hightech.foodguard.ui.screens.FoodListScreen
import com.hightech.foodguard.ui.screens.HomeScreen
import com.hightech.foodguard.ui.screens.MealGuidelinesScreen
import com.hightech.foodguard.viewmodel.FoodViewModel
import com.hightech.foodguard.viewmodel.MealViewModel
import com.hightech.foodguard.viewmodel.ScanViewModel

object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val SCAN = "scan"
    const val MEALS = "meals"
}

@Composable
fun AppNavigation(
    foodViewModel: FoodViewModel,
    scanViewModel: ScanViewModel,
    mealViewModel: MealViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                foodViewModel = foodViewModel,
                mealViewModel = mealViewModel,
                onScanClick = { navController.navigate(Routes.SCAN) },
                onListClick = { navController.navigate(Routes.LIST) },
                onMealsClick = { navController.navigate(Routes.MEALS) }
            )
        }
        composable(Routes.LIST) {
            FoodListScreen(
                viewModel = foodViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MEALS) {
            MealGuidelinesScreen(
                viewModel = mealViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SCAN) {
            ScannerScreen(
                viewModel = scanViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

