package com.hightech.foodguard.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader

object MealImportExportHelper {

    /**
     * Esempi di template predefiniti per pasti completi bilanciati.
     */
    fun getDefaultSampleMeals(): List<MealGuideline> = listOf(
        MealGuideline(
            mealType = MealType.COLAZIONE,
            title = "Colazione Energetica con Avena e Frutta",
            components = "• Porridge con 50g di fiocchi d'avena\n• 200ml latte vegetale (avena o mandorla)\n• 1 cucchiaio di semi di chia e mirtilli freschi\n• 1 tazza di tè verde o caffè non zuccherato",
            guidelines = "Evitare zuccheri raffinati aggiunti. Bere un bicchiere d'acqua naturale a digiuno al risveglio.",
            dayOrTag = "Tutti i giorni"
        ),
        MealGuideline(
            mealType = MealType.SPUNTINO_MATTINA,
            title = "Spuntino Spezza-Fame & Frutta Secca",
            components = "• 1 frutto fresco di stagione (mela, pera o arancia)\n• 15g mandorle o noci sgusciate",
            guidelines = "Favorisce il senso di sazietà e l'apporto di omega-3 prima del pranzo.",
            dayOrTag = "Metà Mattina"
        ),
        MealGuideline(
            mealType = MealType.PRANZO,
            title = "Piatto Unico Mediterraneo Bilanciato",
            components = "• 70g riso integrale o farro bollito\n• 140g salmone o orata al vapore (o tofu alla piastra)\n• Zucchine e pomodorini grigliati a volontà\n• 1 cucchiaio (10g) olio extravergine d'oliva a crudo",
            guidelines = "Consumare prima la porzione di verdure per regolare il picco glicemico. Bere a piccoli sorsi.",
            dayOrTag = "Giorni feriali"
        ),
        MealGuideline(
            mealType = MealType.MERENDA,
            title = "Merenda Pomeridiana Proteica",
            components = "• 1 vasetto (150g) yogurt greco magro naturale o soia\n• 1 cucchiaino di miele o cannella in polvere\n• 1 tazza di tisana ai frutti rossi",
            guidelines = "Ottimo supporto per il recupero energetico post-lavoro o studio.",
            dayOrTag = "Pomeriggio"
        ),
        MealGuideline(
            mealType = MealType.CENA,
            title = "Cena Leggera e Digeribile",
            components = "• Vellutata di zucca o zucchine con erbe aromatiche\n• 150g petto di tacchino ai ferri o 2 uova in camicia\n• 1 fetta di pane di segale o integrale tostato (40g)",
            guidelines = "Cena leggera per favorire il riposo notturno; cenare almeno 2 ore prima di andare a dormire.",
            dayOrTag = "Sera"
        )
    )

    /**
     * Analizza e importa testo proveniente da appunti, Word o Excel.
     */
    fun parseMealsFromText(rawText: String): List<MealGuideline> {
        val results = mutableListOf<MealGuideline>()
        if (rawText.isBlank()) return results

        // 1. Tenta prima la suddivisione per blocchi vuoti (multi-riga tipica di Word o diete strutturate)
        val blocks = rawText.split(Regex("(?m)^\\s*$")).map { it.trim() }.filter { it.isNotBlank() }

        // Se ci sono più blocchi distinti e ciascun blocco ha più righe
        if (blocks.size >= 2 && blocks.any { it.contains("\n") }) {
            for (block in blocks) {
                val parsed = parseSingleBlock(block)
                if (parsed != null) {
                    results.add(parsed)
                }
            }
            if (results.isNotEmpty()) return results
        }

        // 2. Se non sono blocchi vuoti, analizziamo riga per riga (stile tabella Excel o elenco puntato)
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }

        for (line in lines) {
            // Ignora eventuali righe di intestazione Excel
            val lower = line.lowercase()
            if ((lower.contains("tipo") || lower.contains("pasto")) &&
                (lower.contains("titolo") || lower.contains("alimenti") || lower.contains("componenti") || lower.contains("note"))
            ) {
                continue
            }

            // A. Verifica se la riga contiene delimitatori di colonna (Tab tipico da copia/incolla Excel, oppure ; o |)
            val columns = when {
                line.contains("\t") -> line.split("\t").map { it.trim() }
                line.contains(";") -> line.split(";").map { it.trim() }
                line.contains("|") -> line.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                else -> null
            }

            if (columns != null && columns.size >= 2) {
                val firstCol = columns[0]
                val detectedType = detectMealType(firstCol)
                
                if (columns.size == 2) {
                    // Colonna 1: Tipo o Titolo, Colonna 2: Componenti
                    val isFirstType = detectedType != MealType.ALTRO || isExplicitMealKeyword(firstCol)
                    val type = if (isFirstType) detectedType else MealType.PRANZO
                    val title = if (isFirstType) "${type.displayName} Bilanciato" else firstCol
                    val components = columns[1]
                    results.add(
                        MealGuideline(
                            mealType = type,
                            title = title,
                            components = cleanBullets(components)
                        )
                    )
                } else if (columns.size >= 3) {
                    // Colonna 1: Tipo/Titolo, Colonna 2: Titolo o Componenti, Colonna 3: Note/Linee Guida
                    val isFirstType = detectedType != MealType.ALTRO || isExplicitMealKeyword(firstCol)
                    val type = if (isFirstType) detectedType else detectMealType(columns[1])
                    val title = if (isFirstType) columns[1] else firstCol
                    val components = if (columns.size >= 4) columns[2] else columns[1]
                    val guidelines = if (columns.size >= 4) columns[3] else columns[2]

                    results.add(
                        MealGuideline(
                            mealType = type,
                            title = if (title.isBlank()) "${type.displayName} Completo" else title,
                            components = cleanBullets(components),
                            guidelines = guidelines
                        )
                    )
                }
                continue
            }

            // B. Riga con separatore a due punti (es. "Colazione: Porridge d'avena con frutti di bosco e cannella")
            if (line.contains(":") || line.contains(" - ")) {
                val parts = if (line.contains(":")) line.split(":", limit = 2) else line.split(" - ", limit = 2)
                val prefix = parts[0].trim()
                val body = parts[1].trim()
                val detectedType = detectMealType(prefix)

                // Cerca eventuali note finali separate da "(" o "Note:"
                var comp = body
                var guide = ""
                if (body.contains("Note:", ignoreCase = true)) {
                    val noteParts = body.split(Regex("(?i)Note:"), limit = 2)
                    comp = noteParts[0].trim()
                    guide = noteParts.getOrElse(1) { "" }.trim()
                }

                results.add(
                    MealGuideline(
                        mealType = detectedType,
                        title = if (prefix.length > 25) "${detectedType.displayName} Completo" else prefix,
                        components = cleanBullets(comp),
                        guidelines = guide
                    )
                )
                continue
            }

            // C. Riga semplice senza delimitatori
            val detectedType = detectMealType(line)
            results.add(
                MealGuideline(
                    mealType = detectedType,
                    title = "${detectedType.displayName} Consigliato",
                    components = cleanBullets(line)
                )
            )
        }

        return results
    }

    /**
     * Analizza un blocco multi-riga.
     */
    private fun parseSingleBlock(block: String): MealGuideline? {
        val lines = block.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return null

        val firstLine = lines[0]
        val detectedType = detectMealType(firstLine)

        val title: String
        val compLines = mutableListOf<String>()
        val guideLines = mutableListOf<String>()

        var isGuideSection = false

        if (lines.size == 1) {
            return MealGuideline(
                mealType = detectedType,
                title = "${detectedType.displayName} Completo",
                components = cleanBullets(firstLine)
            )
        }

        // Se la prima riga è solo la categoria (es. "PRANZO" o "COLAZIONE")
        val isFirstLineTypeOnly = isExplicitMealKeyword(firstLine)
        val startIndex = if (isFirstLineTypeOnly) 1 else 0
        title = if (isFirstLineTypeOnly && lines.size > 1) {
            lines[1]
        } else {
            "${detectedType.displayName} Completo"
        }

        val contentStart = if (isFirstLineTypeOnly && lines.size > 2) 2 else if (isFirstLineTypeOnly) 1 else 1

        for (i in contentStart until lines.size) {
            val l = lines[i]
            val lower = l.lowercase()
            if (lower.startsWith("note:") || lower.startsWith("linee guida:") || lower.startsWith("consigli:") || lower.startsWith("regole:")) {
                isGuideSection = true
                val clean = l.replace(Regex("(?i)^(note|linee guida|consigli|regole):"), "").trim()
                if (clean.isNotBlank()) guideLines.add(clean)
            } else if (isGuideSection) {
                guideLines.add(l)
            } else {
                compLines.add(cleanBullets(l))
            }
        }

        val componentsText = if (compLines.isNotEmpty()) compLines.joinToString("\n") else lines.drop(startIndex).joinToString("\n")

        return MealGuideline(
            mealType = detectedType,
            title = if (title.isBlank()) "${detectedType.displayName} Completo" else title,
            components = componentsText,
            guidelines = guideLines.joinToString("\n")
        )
    }

    private fun isExplicitMealKeyword(str: String): Boolean {
        val clean = str.trim().lowercase().replace(":", "").replace("-", "")
        return clean in listOf(
            "colazione", "spuntino", "spuntino mattina", "spuntino pomeridiano",
            "pranzo", "merenda", "cena", "piatto unico", "dopocena"
        )
    }

    private fun detectMealType(text: String): MealType {
        val lower = text.lowercase()
        return when {
            lower.contains("colazion") || lower.contains("breakfast") -> MealType.COLAZIONE
            lower.contains("spuntino matt") || lower.contains("metà mattina") -> MealType.SPUNTINO_MATTINA
            lower.contains("pranz") || lower.contains("lunch") -> MealType.PRANZO
            lower.contains("merend") || lower.contains("spuntino pom") || lower.contains("pomerigg") -> MealType.MERENDA
            lower.contains("cen") || lower.contains("dinner") || lower.contains("sera") -> MealType.CENA
            lower.contains("piatto unico") || lower.contains("bowl") || lower.contains("unico") -> MealType.PIATTO_UNICO
            lower.contains("spuntin") || lower.contains("snack") -> MealType.SPUNTINO_MATTINA
            else -> MealType.ALTRO
        }
    }

    private fun cleanBullets(str: String): String {
        return str.trim()
            .removePrefix("-")
            .removePrefix("•")
            .removePrefix("*")
            .removePrefix(">")
            .trim()
    }

    /**
     * Legge testo da file URI (.txt, .csv, fogli di testo esportati).
     */
    fun readTextFromUri(context: Context, uri: Uri): String {
        val sb = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    sb.append(line).append("\n")
                    line = reader.readLine()
                }
            }
        }
        return sb.toString()
    }

    /**
     * Formatta ed esporta tutte le linee guida pasti per WhatsApp.
     */
    fun shareViaWhatsApp(context: Context, meals: List<MealGuideline>) {
        if (meals.isEmpty()) {
            Toast.makeText(context, "Nessun pasto configurato da condividere", Toast.LENGTH_SHORT).show()
            return
        }

        val sb = StringBuilder()
        sb.append("📋 *LINEE GUIDA PASTI COMPLETI* 🥗✨\n")
        sb.append("_FoodGuard - Guida nutrizionale e combinazioni sane_\n\n")

        // Raggruppa i pasti per tipo
        val grouped = meals.groupBy { it.mealType }

        for (type in MealType.entries) {
            val list = grouped[type] ?: continue
            sb.append("${type.emoji} *${type.displayName.uppercase()}*\n")
            sb.append("────────────────────────\n")
            for (m in list) {
                sb.append("🍽️ *${m.title}*")
                if (m.dayOrTag.isNotBlank()) {
                    sb.append(" _[${m.dayOrTag}]_")
                }
                sb.append("\n")

                // Componenti con elenco puntato
                m.components.lines().filter { it.isNotBlank() }.forEach { compLine ->
                    val clean = if (compLine.trim().startsWith("•")) compLine else "• ${compLine.trim()}"
                    sb.append("$clean\n")
                }

                if (m.guidelines.isNotBlank()) {
                    sb.append("💡 _Consigli: ${m.guidelines.trim()}_\n")
                }
                sb.append("\n")
            }
        }

        sb.append("────────────────────────\n")
        sb.append("🛡️ _Inviato con FoodGuard - Salute e Sicurezza Alimentare_")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            val fallbackIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sb.toString())
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Condividi linee guida pasti via"))
        }
    }

    /**
     * Condivide un singolo pasto completo via WhatsApp.
     */
    fun shareSingleMealViaWhatsApp(context: Context, meal: MealGuideline) {
        val sb = StringBuilder()
        sb.append("${meal.mealType.emoji} *${meal.mealType.displayName.uppercase()}: ${meal.title}*\n")
        if (meal.dayOrTag.isNotBlank()) {
            sb.append("📌 _${meal.dayOrTag}_\n")
        }
        sb.append("────────────────────────\n")
        sb.append("*Composizione del pasto:*\n")
        meal.components.lines().filter { it.isNotBlank() }.forEach { l ->
            val clean = if (l.trim().startsWith("•")) l else "• ${l.trim()}"
            sb.append("$clean\n")
        }
        if (meal.guidelines.isNotBlank()) {
            sb.append("\n💡 *Linee guida & Condimenti:*\n_${meal.guidelines.trim()}_\n")
        }
        sb.append("────────────────────────\n")
        sb.append("🛡️ _FoodGuard_")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            val fallbackIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sb.toString())
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Condividi pasto via"))
        }
    }
}
