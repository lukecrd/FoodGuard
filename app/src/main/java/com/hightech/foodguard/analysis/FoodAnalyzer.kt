package com.hightech.foodguard.analysis

import com.hightech.foodguard.data.FoodItem
import com.hightech.foodguard.network.Product
import java.text.Normalizer
import java.util.Locale

enum class Verdict { SICURO, ATTENZIONE, VIETATO }

data class AnalysisResult(
    val verdict: Verdict,
    val productName: String,
    val brand: String?,
    val imageUrl: String?,
    val matchedForbidden: List<String>,   // nomi della tua lista trovati nel prodotto
    val matchedAllergens: List<String>,   // allergeni ufficiali OFF trovati che sono anche nella tua lista
    val ingredientsText: String?
)

object FoodAnalyzer {

    private fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(Locale.ITALIAN), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "") // rimuove accenti
            .trim()

    /**
     * Confronta il prodotto scansionato con la lista di cibi salvati dall'utente.
     * Cerca ogni voce "VIETATO" (o allergene) dentro il testo degli ingredienti
     * e dentro i tag allergeni ufficiali di Open Food Facts.
     */
    fun analyze(product: Product, forbiddenList: List<FoodItem>): AnalysisResult {
        val ingredients = normalize(product.ingredientsText.orEmpty())
        val offAllergenTags = (product.allergensTags.orEmpty() + product.tracesTags.orEmpty())
            .map { normalize(it.substringAfter(":")) } // "en:gluten" -> "gluten"

        val matchedForbidden = mutableListOf<String>()
        val matchedAllergens = mutableListOf<String>()

        forbiddenList.filter { it.status == com.hightech.foodguard.data.FoodStatus.VIETATO }
            .forEach { food ->
                val term = normalize(food.name)
                if (term.isBlank()) return@forEach

                val foundInIngredients = ingredients.contains(term)
                val foundInAllergenTags = offAllergenTags.any { it.contains(term) || term.contains(it) }

                if (foundInIngredients || foundInAllergenTags) {
                    if (food.isAllergen) matchedAllergens.add(food.name) else matchedForbidden.add(food.name)
                }
            }

        val verdict = when {
            matchedAllergens.isNotEmpty() || matchedForbidden.isNotEmpty() -> Verdict.VIETATO
            product.ingredientsText.isNullOrBlank() -> Verdict.ATTENZIONE // dati insufficienti
            else -> Verdict.SICURO
        }

        return AnalysisResult(
            verdict = verdict,
            productName = product.productName ?: "Prodotto sconosciuto",
            brand = product.brands,
            imageUrl = product.imageUrl,
            matchedForbidden = matchedForbidden,
            matchedAllergens = matchedAllergens,
            ingredientsText = product.ingredientsText
        )
    }
}
