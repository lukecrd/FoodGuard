package com.hightech.foodguard.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BreakfastDining
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Cookie
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.RamenDining
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightech.foodguard.data.MealGuideline
import com.hightech.foodguard.data.MealImportExportHelper
import com.hightech.foodguard.data.MealType
import com.hightech.foodguard.ui.components.*
import com.hightech.foodguard.ui.theme.*
import com.hightech.foodguard.viewmodel.MealViewModel

/** Colori e icona associati a ciascun tipo di pasto. */
private data class MealStyle(
    val accent: Color,
    val deep: Color,
    val soft: Color,
    val ink: Color,
    val icon: ImageVector
)

private fun mealStyle(type: MealType): MealStyle = when (type) {
    MealType.COLAZIONE -> MealStyle(Sunny, SunnyDeep, SunnySoft, SunnyInk, Icons.Rounded.BreakfastDining)
    MealType.SPUNTINO_MATTINA -> MealStyle(Orange, OrangeDeep, OrangeSoft, OrangeInk, Icons.Rounded.Eco)
    MealType.PRANZO -> MealStyle(Sky, SkyDeep, SkySoft, SkyInk, Icons.Rounded.LunchDining)
    MealType.MERENDA -> MealStyle(Berry, BerryDeep, BerrySoft, BerryInk, Icons.Rounded.Cookie)
    MealType.CENA -> MealStyle(Leaf, LeafDeep, LeafSoft, LeafInk, Icons.Rounded.DinnerDining)
    MealType.PIATTO_UNICO -> MealStyle(Tomato, TomatoDeep, TomatoSoft, TomatoInk, Icons.Rounded.RamenDining)
    MealType.ALTRO -> MealStyle(Bubblegum, BubblegumDeep, BubblegumSoft, BubblegumInk, Icons.AutoMirrored.Rounded.Notes)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealGuidelinesScreen(
    viewModel: MealViewModel,
    onBack: () -> Unit
) {
    val meals by viewModel.meals.collectAsState()
    var selectedTypeFilter by remember { mutableStateOf<MealType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<MealGuideline?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val screenStart = remember { System.currentTimeMillis() }

    val filteredMeals = meals.filter { meal ->
        val matchesType = selectedTypeFilter == null || meal.mealType == selectedTypeFilter
        val matchesSearch = searchQuery.isBlank() ||
                meal.title.contains(searchQuery, ignoreCase = true) ||
                meal.components.contains(searchQuery, ignoreCase = true) ||
                meal.guidelines.contains(searchQuery, ignoreCase = true) ||
                meal.dayOrTag.contains(searchQuery, ignoreCase = true)
        matchesType && matchesSearch
    }

    Scaffold(
        containerColor = FunBackground,
        topBar = {
            FunHeader(
                title = "Linee Guida Pasti 🍽️",
                subtitle = "${meals.size} pasti completi configurati",
                icon = Icons.Rounded.Restaurant,
                gradient = MealGradient,
                onBack = onBack
            ) {
                // Import (Word, Excel, Appunti)
                FunIconButton(
                    icon = Icons.Rounded.FileUpload,
                    contentDescription = "Importa da file o testo",
                    onClick = { showImportDialog = true },
                    containerColor = Color.White.copy(alpha = 0.22f),
                    tint = Color.White,
                    size = 40.dp,
                    iconSize = 20.dp
                )
                // Condivisione WhatsApp
                FunIconButton(
                    icon = Icons.Rounded.Share,
                    contentDescription = "Esporta su WhatsApp",
                    onClick = { MealImportExportHelper.shareViaWhatsApp(context, meals) },
                    containerColor = Color.White.copy(alpha = 0.22f),
                    tint = Color.White,
                    size = 40.dp,
                    iconSize = 20.dp
                )
            }
        },
        floatingActionButton = {
            FunFab(
                text = "NUOVO PASTO",
                icon = Icons.Rounded.Add,
                onClick = {
                    editingMeal = null
                    showAddEditDialog = true
                },
                colors = MealGradient
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FloatingBlobs(colors = listOf(Orange, Sky, Bubblegum), blobAlpha = 0.07f, durationMillis = 16000)

            Column(modifier = Modifier.fillMaxSize()) {
                // Barra di ricerca a pillola
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cerca piatto, ingrediente o pasto...", color = InkSoft) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Cancella", tint = InkSoft)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(percent = 50),
                    singleLine = true,
                    colors = funTextFieldColors(OrangeDeep)
                )

                // Chip categorie
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FunChoiceChip(
                        selected = selectedTypeFilter == null,
                        onClick = { selectedTypeFilter = null },
                        label = "Tutti (${meals.size})",
                        icon = Icons.Rounded.Category,
                        selectedColor = InkSoft,
                        softColor = FunSurface,
                        inkColor = Ink
                    )
                    MealType.entries.forEach { type ->
                        val isSelected = selectedTypeFilter == type
                        val count = meals.count { it.mealType == type }
                        val style = mealStyle(type)
                        FunChoiceChip(
                            selected = isSelected,
                            onClick = { selectedTypeFilter = if (isSelected) null else type },
                            label = "${type.displayName} ($count)",
                            icon = style.icon,
                            selectedColor = style.deep,
                            softColor = style.soft,
                            inkColor = style.ink
                        )
                    }
                }

                // Lista pasti o stato vuoto
                if (filteredMeals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        StaggeredEntrance(delayMillis = 0) {
                            FunCard(accentColor = Orange, cornerRadius = 32.dp, modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    BouncyEmoji("🥗🍳🍲")
                                    Text(
                                        if (meals.isEmpty()) "Nessuna Linea Guida Pasti" else "Nessun Risultato per la Ricerca",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Ink,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        if (meals.isEmpty())
                                            "Questa sezione è separata dai cibi singoli ed è dedicata a comporre pasti completi (Colazione, Pranzo, Cena) con le rispettive linee guida nutrizionali e porzioni."
                                        else
                                            "Nessun pasto corrisponde ai filtri selezionati. Prova a modificare la ricerca.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSoft,
                                        textAlign = TextAlign.Center
                                    )

                                    if (meals.isEmpty()) {
                                        FunButton(
                                            text = "Carica Esempi ✨",
                                            onClick = { viewModel.populateSampleGuidelines() },
                                            colors = SafeGradient,
                                            icon = Icons.Rounded.AutoAwesome,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        FunSoftButton(
                                            text = "Importa File 📄",
                                            onClick = { showImportDialog = true },
                                            containerColor = BerrySoft,
                                            contentColor = BerryInk,
                                            icon = Icons.Rounded.FileUpload,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(filteredMeals, key = { _, meal -> meal.id }) { index, meal ->
                            val delay = if (System.currentTimeMillis() - screenStart < 800) index.coerceAtMost(6) * 70 else 0
                            StaggeredEntrance(
                                delayMillis = delay,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                            ) {
                                MealCardItem(
                                    meal = meal,
                                    onEdit = {
                                        editingMeal = meal
                                        showAddEditDialog = true
                                    },
                                    onDelete = {
                                        viewModel.deleteMeal(meal)
                                        Toast.makeText(context, "Pasto rimosso", Toast.LENGTH_SHORT).show()
                                    },
                                    onShareSingle = {
                                        MealImportExportHelper.shareSingleMealViaWhatsApp(context, meal)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog per aggiungere o modificare un pasto completo
    if (showAddEditDialog) {
        MealAddEditDialog(
            initialMeal = editingMeal,
            onDismiss = {
                showAddEditDialog = false
                editingMeal = null
            },
            onSave = { type, title, components, guidelines, tag ->
                if (editingMeal != null) {
                    viewModel.updateMeal(
                        editingMeal!!.copy(
                            mealType = type,
                            title = title,
                            components = components,
                            guidelines = guidelines,
                            dayOrTag = tag
                        )
                    )
                } else {
                    viewModel.addMeal(
                        mealType = type,
                        title = title,
                        components = components,
                        guidelines = guidelines,
                        dayOrTag = tag
                    )
                }
                showAddEditDialog = false
                editingMeal = null
            }
        )
    }

    // Dialog per importare pasti da testo/Word/Excel
    if (showImportDialog) {
        MealImportDialog(
            onDismiss = { showImportDialog = false },
            onImport = { importedMeals ->
                viewModel.importMeals(importedMeals)
                Toast.makeText(context, "${importedMeals.size} pasti importati con successo! ✨", Toast.LENGTH_LONG).show()
                showImportDialog = false
            }
        )
    }
}

/**
 * Singola card per il pasto completo.
 */
@Composable
private fun MealCardItem(
    meal: MealGuideline,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShareSingle: () -> Unit
) {
    val style = mealStyle(meal.mealType)

    FunCard(
        accentColor = style.accent,
        cornerRadius = 28.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Intestazione: categoria + azioni rapide
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(style.deep)
                        .padding(start = 6.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconBubble(
                        icon = style.icon,
                        tint = style.deep,
                        containerColor = Color.White,
                        size = 24.dp,
                        iconSize = 15.dp
                    )
                    Text(
                        text = meal.mealType.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                Spacer(Modifier.weight(1f))

                // Azioni (WhatsApp, Modifica, Elimina)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FunIconButton(
                        icon = Icons.Rounded.Share,
                        contentDescription = "Invia pasto via WhatsApp",
                        onClick = onShareSingle,
                        containerColor = LeafSoft,
                        tint = LeafInk,
                        size = 36.dp,
                        iconSize = 18.dp
                    )
                    FunIconButton(
                        icon = Icons.Rounded.Edit,
                        contentDescription = "Modifica",
                        onClick = onEdit,
                        containerColor = SkySoft,
                        tint = SkyInk,
                        size = 36.dp,
                        iconSize = 18.dp
                    )
                    FunIconButton(
                        icon = Icons.Rounded.DeleteOutline,
                        contentDescription = "Elimina",
                        onClick = onDelete,
                        containerColor = TomatoSoft,
                        tint = TomatoInk,
                        size = 36.dp,
                        iconSize = 18.dp
                    )
                }
            }

            // Titolo del pasto
            Text(
                text = meal.title,
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )

            // Tag opzionale (es. "Tutti i giorni")
            if (meal.dayOrTag.isNotBlank()) {
                StatusPill(
                    text = meal.dayOrTag,
                    containerColor = FunSurfaceSoft,
                    contentColor = InkSoft,
                    icon = Icons.Rounded.LocalOffer
                )
            }

            // Composizione del pasto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(style.soft)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.RestaurantMenu, contentDescription = null, tint = style.ink, modifier = Modifier.size(16.dp))
                    Text(
                        text = "COMPOSIZIONE PASTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = style.ink
                    )
                }
                meal.components.lines().filter { it.isNotBlank() }.forEach { line ->
                    val clean = line.trim().removePrefix("•").trim()
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(style.deep)
                        )
                        Text(
                            text = clean,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink
                        )
                    }
                }
            }

            // Linee guida e note nutrizionali (se presenti)
            if (meal.guidelines.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SunnySoft)
                        .border(1.5.dp, Sunny.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    IconBubble(
                        icon = Icons.Rounded.Lightbulb,
                        tint = Color.White,
                        containerColor = SunnyDeep,
                        size = 30.dp,
                        iconSize = 17.dp
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "LINEE GUIDA & REGOLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = SunnyInk
                        )
                        Text(
                            text = meal.guidelines,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog per aggiungere o modificare un pasto completo.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealAddEditDialog(
    initialMeal: MealGuideline?,
    onDismiss: () -> Unit,
    onSave: (type: MealType, title: String, components: String, guidelines: String, tag: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialMeal?.mealType ?: MealType.PRANZO) }
    var title by remember { mutableStateOf(initialMeal?.title ?: "") }
    var components by remember { mutableStateOf(initialMeal?.components ?: "") }
    var guidelines by remember { mutableStateOf(initialMeal?.guidelines ?: "") }
    var tag by remember { mutableStateOf(initialMeal?.dayOrTag ?: "") }
    val selectedStyle = mealStyle(selectedType)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            IconBubble(
                icon = if (initialMeal == null) Icons.Rounded.Restaurant else Icons.Rounded.Edit,
                tint = Color.White,
                containerColor = selectedStyle.deep,
                size = 52.dp
            )
        },
        title = {
            Text(
                text = if (initialMeal == null) "Nuovo Pasto Completo 🥗" else "Modifica Pasto ✏️",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tipo di Pasto:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.entries.forEach { type ->
                        val style = mealStyle(type)
                        FunChoiceChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = type.displayName,
                            icon = style.icon,
                            selectedColor = style.deep,
                            softColor = style.soft,
                            inkColor = style.ink
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome / Titolo del Pasto") },
                    placeholder = { Text("es. Pranzo Mediterraneo con Riso e Salmone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(selectedStyle.deep)
                )

                OutlinedTextField(
                    value = components,
                    onValueChange = { components = it },
                    label = { Text("Composizione del Pasto (Alimenti & Dosi) *") },
                    placeholder = { Text("es. \n• 70g riso basmati\n• 150g salmone al vapore\n• Zucchine grigliate\n• 1 cucchiaio olio EVO") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(selectedStyle.deep)
                )

                OutlinedTextField(
                    value = guidelines,
                    onValueChange = { guidelines = it },
                    label = { Text("Linee Guida, Condimenti & Note") },
                    placeholder = { Text("es. Olio a crudo, bere prima del pasto, nessuna frittura...") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(selectedStyle.deep)
                )

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag / Giorni (opzionale)") },
                    placeholder = { Text("es. Giorni feriali, Allenamento, Weekend") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(selectedStyle.deep)
                )
            }
        },
        confirmButton = {
            FunButton(
                text = "SALVA PASTO",
                onClick = {
                    if (components.isNotBlank()) {
                        val finalTitle = if (title.isBlank()) "${selectedType.displayName} Bilanciato" else title.trim()
                        onSave(selectedType, finalTitle, components.trim(), guidelines.trim(), tag.trim())
                    }
                },
                enabled = components.isNotBlank(),
                colors = MealGradient,
                icon = Icons.Rounded.Save,
                height = 46.dp
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ANNULLA", color = InkSoft, style = MaterialTheme.typography.labelLarge)
            }
        },
        containerColor = FunSurface,
        shape = RoundedCornerShape(32.dp)
    )
}

/**
 * Dialog di importazione pasti da testo (Word, Excel, appunti o file).
 */
@Composable
private fun MealImportDialog(
    onDismiss: () -> Unit,
    onImport: (List<MealGuideline>) -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = MealImportExportHelper.readTextFromUri(context, uri)
                rawText = content
            } catch (e: Exception) {
                Toast.makeText(context, "Errore nella lettura del file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val parsedMeals = remember(rawText) {
        MealImportExportHelper.parseMealsFromText(rawText)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            IconBubble(icon = Icons.Rounded.FileUpload, tint = Color.White, containerColor = BerryDeep, size = 52.dp)
        },
        title = {
            Text(
                text = "Importa Pasti Completi 📄",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Incolla il testo copiato da un foglio Word/Excel, oppure carica direttamente un file di testo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSoft
                )

                // Azioni rapide: incolla dagli appunti e scegli file
                FunSoftButton(
                    text = "Incolla Appunti",
                    onClick = {
                        val clip = clipboardManager.getText()?.text
                        if (!clip.isNullOrBlank()) {
                            rawText = clip
                            Toast.makeText(context, "Testo incollato dagli appunti!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gli appunti sono vuoti", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = BerrySoft,
                    contentColor = BerryInk,
                    icon = Icons.Rounded.ContentPaste,
                    height = 46.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                FunSoftButton(
                    text = "Apri File (.txt)",
                    onClick = { filePickerLauncher.launch("text/*") },
                    containerColor = SkySoft,
                    contentColor = SkyInk,
                    icon = Icons.Rounded.FileOpen,
                    height = 46.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    placeholder = {
                        Text(
                            "Esempi supportati:\n\n" +
                                    "Colazione: Porridge d'avena con frutti rossi e mandorle\n" +
                                    "Pranzo: 80g Riso integrale, 150g Salmone, zucchine a vapore. Note: Olio EVO a crudo\n" +
                                    "Cena: Petto di pollo ai ferri con insalata mista\n\n" +
                                    "Oppure incolla da Excel (Tabella Pasto | Piatti | Note)",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = InkSoft
                        )
                    },
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(SkyDeep)
                )

                // Anteprima conteggio
                if (rawText.isNotBlank()) {
                    val ok = parsedMeals.isNotEmpty()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (ok) LeafSoft else SunnySoft)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (ok) Icons.Rounded.AutoAwesome else Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = if (ok) LeafInk else SunnyInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (ok)
                                "Rilevati ${parsedMeals.size} pasti completi pronti per l'importazione!"
                            else
                                "Nessun pasto valido riconosciuto nel testo inserito.",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (ok) LeafInk else SunnyInk
                        )
                    }
                }
            }
        },
        confirmButton = {
            FunButton(
                text = "IMPORTA (${parsedMeals.size})",
                onClick = { onImport(parsedMeals) },
                enabled = parsedMeals.isNotEmpty(),
                colors = SafeGradient,
                icon = Icons.Rounded.Download,
                height = 46.dp
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ANNULLA", color = InkSoft, style = MaterialTheme.typography.labelLarge)
            }
        },
        containerColor = FunSurface,
        shape = RoundedCornerShape(32.dp)
    )
}
