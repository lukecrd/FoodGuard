package com.hightech.foodguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Categoria / Tipo di pasto per le linee guida.
 */
enum class MealType(val displayName: String, val emoji: String) {
    COLAZIONE("Colazione", "🌅"),
    SPUNTINO_MATTINA("Spuntino Mattina", "🍎"),
    PRANZO("Pranzo", "🥗"),
    MERENDA("Merenda", "🥜"),
    CENA("Cena", "🌙"),
    PIATTO_UNICO("Piatto Unico", "🍲"),
    ALTRO("Altro / Linea Guida", "📋")
}

/**
 * Entità per la gestione delle linee guida dei pasti completi.
 * Completamente separata dalla lista cibi consentiti/vietati.
 */
@Entity(tableName = "meal_guidelines")
data class MealGuideline(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealType: MealType = MealType.PRANZO,
    val title: String,                // es. "Pranzo Equilibrato Carboidrati + Proteine"
    val components: String,           // Composizione del pasto: cibi, piatti, ingredienti completi
    val guidelines: String = "",      // Regole alimentari, porzioni, condimenti (es. "Olio EVO a crudo max 1 cucchiaio")
    val dayOrTag: String = "",        // es. "Giorni feriali", "Allenamento", "Generale"
    val orderIndex: Int = 0
)
