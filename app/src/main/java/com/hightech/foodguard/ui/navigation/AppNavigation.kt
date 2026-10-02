package com.hightech.foodguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hightech.foodguard.data.FoodStatus
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

    const val LIST_FILTER_ARG = "filter"
    const val LIST_PATTERN = "$LIST?$LIST_FILTER_ARG={$LIST_FILTER_ARG}"

    fun list(filter: FoodStatus? = null): String =
        if (filter == null) LIST else "$LIST?$LIST_FILTER_ARG=${filter.name}"
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
                onListClick = { navController.navigate(Routes.list()) },
                onMealsClick = { navController.navigate(Routes.MEALS) },
                onForbiddenClick = { navController.navigate(Routes.list(FoodStatus.VIETATO)) },
                onAllowedClick = { navController.navigate(Routes.list(FoodStatus.CONSENTITO)) }
            )
        }
        composable(
            route = Routes.LIST_PATTERN,
            arguments = listOf(
                navArgument(Routes.LIST_FILTER_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val filterName = entry.arguments?.getString(Routes.LIST_FILTER_ARG)
            FoodListScreen(
                viewModel = foodViewModel,
                initialFilter = FoodStatus.values().firstOrNull { it.name == filterName },
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

