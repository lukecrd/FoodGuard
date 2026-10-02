package com.hightech.foodguard.data

import android.content.Context
import android.content.Intent

object FoodImportExportHelper {

    /**
     * Analizza testo grezzo proveniente da Word, Excel (CSV, TSV), blocco note o WhatsApp
     * e lo trasforma in una lista di FoodItem pronti per essere salvati.
     */
    fun parseFoodList(rawText: String, defaultStatus: FoodStatus = FoodStatus.VIETATO): List<FoodItem> {
        val lines = rawText.lines()
        val result = mutableListOf<FoodItem>()

        for (rawLine in lines) {
            var line = rawLine.trim()
            if (line.isBlank()) continue

            // Rimuove elenchi puntati o numerati (es. "• Latte", "- Uova", "1. Arachidi", "1) Soia")
            line = line.replace(Regex("^[•\\-*\\d]+[.)\\s-]*"), "").trim()
            if (line.isBlank()) continue

            // Cerca separatori di colonna (es. copia-incolla da Excel con Tab, oppure CSV con virgola/punto e virgola/pipe)
            val parts = if (line.contains("\t")) {
                line.split("\t")
            } else if (line.contains(";")) {
                line.split(";")
            } else if (line.contains("|")) {
                line.split("|")
            } else if (line.contains(",")) {
                line.split(",")
            } else {
                listOf(line)
            }

            var itemName = parts[0].trim()
            if (itemName.isBlank()) continue

            var status = defaultStatus
            var isAllergen = false

            val fullLineLower = line.lowercase()

            // Riconoscimento dello stato (Consentito vs Vietato)
            if (fullLineLower.contains("consentit") || fullLineLower.contains("allowed") || fullLineLower.contains("permess") || fullLineLower.contains("verde") || fullLineLower.contains(" safe")) {
                status = FoodStatus.CONSENTITO
            } else if (fullLineLower.contains("vietat") || fullLineLower.contains("forbid") || fullLineLower.contains("ross") || fullLineLower.contains("danger") || fullLineLower.contains("allarme")) {
                status = FoodStatus.VIETATO
            }

            // Riconoscimento allergeni
            if (fullLineLower.contains("allergen") || fullLineLower.contains("allerg") || fullLineLower.contains("intolleran") || fullLineLower.contains("⚠️")) {
                isAllergen = true
            }

            // Pulizia del nome se include annotazioni tra parentesi o residui
            itemName = itemName
                .replace(Regex("(?i)\\b(vietato|vietata|consentito|consentita|allergene|allergia)\\b"), "")
                .replace("()", "")
                .replace("[]", "")
                .trim()

            if (itemName.isNotBlank()) {
                result.add(
                    FoodItem(
                        name = itemName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        status = status,
                        isAllergen = isAllergen
                    )
                )
            }
        }

        return result
    }

    /**
     * Genera un testo formattato e gradevole, pronto per essere inviato su WhatsApp o app di messaggistica.
     */
    fun generateShareText(foods: List<FoodItem>): String {
        val forbidden = foods.filter { it.status == FoodStatus.VIETATO }
        val allowed = foods.filter { it.status == FoodStatus.CONSENTITO }

        val sb = StringBuilder()
        sb.append("🛡️ *FoodGuard - La mia lista cibi & allergeni*\n\n")

        if (forbidden.isNotEmpty()) {
            sb.append("🚫 *CIBI VIETATI (${forbidden.size}):*\n")
            forbidden.forEach { item ->
                val allergenTag = if (item.isAllergen) " ⚠️ (Allergene)" else ""
                sb.append("• ${item.name}$allergenTag\n")
            }
            sb.append("\n")
        }

        if (allowed.isNotEmpty()) {
            sb.append("✅ *CIBI CONSENTITI (${allowed.size}):*\n")
            allowed.forEach { item ->
                sb.append("• ${item.name}\n")
            }
            sb.append("\n")
        }

        if (forbidden.isEmpty() && allowed.isEmpty()) {
            sb.append("La lista è attualmente vuota.\n\n")
        }

        sb.append("✨ _Inviato con FoodGuard_")
        return sb.toString()
    }

    /**
     * Apre il menu di condivisione di sistema (WhatsApp, Telegram, Note, Email, ecc.) con il testo formattato.
     */
    fun shareViaWhatsApp(context: Context, foods: List<FoodItem>) {
        val text = generateShareText(foods)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Condividi lista cibi tramite...")
        context.startActivity(shareIntent)
    }
}
