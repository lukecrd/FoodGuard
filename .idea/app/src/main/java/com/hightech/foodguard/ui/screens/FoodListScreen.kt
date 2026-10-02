package com.hightech.foodguard.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hightech.foodguard.data.FoodImportExportHelper
import com.hightech.foodguard.data.FoodItem
import com.hightech.foodguard.data.FoodStatus
import com.hightech.foodguard.ui.components.*
import com.hightech.foodguard.ui.theme.*
import com.hightech.foodguard.viewmodel.FoodViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodListScreen(
    viewModel: FoodViewModel,
    onBack: () -> Unit
) {
    val foods by viewModel.foods.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf<FoodStatus?>(null) }
    val context = LocalContext.current
    val screenStart = remember { System.currentTimeMillis() }

    val forbiddenCount = foods.count { it.status == FoodStatus.VIETATO }
    val allowedCount = foods.count { it.status == FoodStatus.CONSENTITO }

    Scaffold(
        containerColor = FunBackground,
        topBar = {
            FunHeader(
                title = "La Mia Lista Cibi",
                subtitle = "${foods.size} ingredienti salvati",
                icon = Icons.AutoMirrored.Rounded.ListAlt,
                gradient = ListGradient,
                onBack = onBack
            ) {
                // Tasto Importa (Word / Excel / Testo)
                FunIconButton(
                    icon = Icons.Rounded.FileUpload,
                    contentDescription = "Importa da file o testo",
                    onClick = { showImportDialog = true },
                    containerColor = Color.White.copy(alpha = 0.22f),
                    tint = Color.White,
                    size = 40.dp,
                    iconSize = 20.dp
                )
                // Tasto Condividi WhatsApp
                FunIconButton(
                    icon = Icons.Rounded.Share,
                    contentDescription = "Invia su WhatsApp",
                    onClick = { FoodImportExportHelper.shareViaWhatsApp(context, foods) },
                    containerColor = Color.White.copy(alpha = 0.22f),
                    tint = Color.White,
                    size = 40.dp,
                    iconSize = 20.dp
                )
            }
        },
        floatingActionButton = {
            FunFab(
                text = "AGGIUNGI",
                icon = Icons.Rounded.Add,
                onClick = { showAddDialog = true },
                colors = ScanGradient
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            FloatingBlobs(colors = listOf(Berry, Sunny, Leaf), blobAlpha = 0.07f, durationMillis = 16000)

            Column(modifier = Modifier.fillMaxSize()) {
                // Filtri colorati
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CounterFilterChip(
                        selected = filter == null,
                        onClick = { filter = null },
                        label = "Tutti",
                        count = foods.size,
                        icon = Icons.Rounded.Category,
                        deep = SkyDeep,
                        soft = SkySoft,
                        ink = SkyInk,
                        modifier = Modifier.weight(1f)
                    )
                    CounterFilterChip(
                        selected = filter == FoodStatus.VIETATO,
                        onClick = { filter = FoodStatus.VIETATO },
                        label = "Vietati",
                        count = forbiddenCount,
                        icon = Icons.Rounded.Block,
                        deep = TomatoDeep,
                        soft = TomatoSoft,
                        ink = TomatoInk,
                        modifier = Modifier.weight(1f)
                    )
                    CounterFilterChip(
                        selected = filter == FoodStatus.CONSENTITO,
                        onClick = { filter = FoodStatus.CONSENTITO },
                        label = "Consentiti",
                        count = allowedCount,
                        icon = Icons.Rounded.CheckCircle,
                        deep = LeafDeep,
                        soft = LeafSoft,
                        ink = LeafInk,
                        modifier = Modifier.weight(1f)
                    )
                }

                val visibleFoods = foods.filter { filter == null || it.status == filter }

                if (visibleFoods.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        StaggeredEntrance(delayMillis = 0, modifier = Modifier.padding(24.dp)) {
                            FunCard(accentColor = Berry, cornerRadius = 32.dp, modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    BouncyEmoji("🥦🍓🥕")
                                    Text(
                                        "Nessun alimento presente",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Ink,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "Tocca il pulsante + per aggiungere un alimento, oppure usa il tasto 'Importa' in alto per caricare liste da Excel, Word o WhatsApp!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSoft,
                                        textAlign = TextAlign.Center
                                    )
                                    FunButton(
                                        text = "IMPORTA LISTA ESTERNA",
                                        onClick = { showImportDialog = true },
                                        colors = ListGradient,
                                        icon = Icons.Rounded.FileUpload,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(visibleFoods, key = { _, it -> it.id }) { index, item ->
                            // Cascata solo all'apertura della schermata, non durante lo scroll
                            val delay = if (System.currentTimeMillis() - screenStart < 800) index.coerceAtMost(8) * 60 else 0
                            StaggeredEntrance(
                                delayMillis = delay,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                            ) {
                                FoodRow(
                                    item = item,
                                    onDelete = { viewModel.delete(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        FunAddFoodDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, status, isAllergen ->
                viewModel.addFood(name, status, isAllergen)
                showAddDialog = false
            }
        )
    }

    if (showImportDialog) {
        FunImportDialog(
            onDismiss = { showImportDialog = false },
            onImport = { items ->
                viewModel.importFoods(items)
                showImportDialog = false
                Toast.makeText(context, "${items.size} alimenti importati con successo!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Chip filtro "a gettone" con icona, contatore e etichetta.
 */
@Composable
private fun CounterFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    count: Int,
    icon: ImageVector,
    deep: Color,
    soft: Color,
    ink: Color,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(if (selected) deep else soft, label = "filterBg")
    val foreground by animateColorAsState(if (selected) Color.White else ink, label = "filterFg")
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.9f)
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .graphicsLayer {
                val s = scale.value * (if (selected) 1.04f else 1f)
                scaleX = s
                scaleY = s
            }
            .clip(shape)
            .background(background)
            .border(2.dp, if (selected) deep else ink.copy(alpha = 0.15f), shape)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = foreground, modifier = Modifier.size(18.dp))
            Text(text = "$count", style = MaterialTheme.typography.titleLarge, color = foreground)
        }
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = foreground, maxLines = 1)
    }
}

@Composable
private fun FoodRow(item: FoodItem, onDelete: () -> Unit) {
    val isForbidden = item.status == FoodStatus.VIETATO
    val accent = if (isForbidden) Tomato else Leaf
    val deep = if (isForbidden) TomatoDeep else LeafDeep
    val soft = if (isForbidden) TomatoSoft else LeafSoft
    val ink = if (isForbidden) TomatoInk else LeafInk
    val statusLabel = if (isForbidden) "VIETATO" else "CONSENTITO"
    val statusIcon = if (isForbidden) Icons.Rounded.Block else Icons.Rounded.CheckCircle

    FunCard(
        accentColor = accent,
        cornerRadius = 26.dp,
        contentPadding = 14.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBubble(
                icon = statusIcon,
                tint = Color.White,
                containerColor = deep,
                size = 48.dp,
                shape = RoundedCornerShape(16.dp)
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusPill(text = statusLabel, containerColor = soft, contentColor = ink)
                    if (item.isAllergen) {
                        StatusPill(
                            text = "ALLERGENE",
                            containerColor = SunnySoft,
                            contentColor = SunnyInk,
                            icon = Icons.Rounded.Warning
                        )
                    }
                }
            }

            FunIconButton(
                icon = Icons.Rounded.DeleteOutline,
                contentDescription = "Elimina",
                onClick = onDelete,
                containerColor = TomatoSoft,
                tint = TomatoInk,
                size = 42.dp,
                iconSize = 21.dp
            )
        }
    }
}

/**
 * Modale per aggiungere un singolo alimento
 */
@Composable
private fun FunAddFoodDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, FoodStatus, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(FoodStatus.VIETATO) }
    var isAllergen by remember { mutableStateOf(false) }
    val allergenBackground by animateColorAsState(
        if (isAllergen) SunnySoft else FunSurfaceSoft,
        label = "allergenBg"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FunSurface,
        shape = RoundedCornerShape(32.dp),
        icon = {
            IconBubble(icon = Icons.Rounded.Add, tint = Color.White, containerColor = SkyDeep, size = 52.dp)
        },
        title = {
            Text(
                text = "Nuovo Alimento 🥗",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome (es. arachidi, latte, soia)") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(SkyDeep),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Stato dell'alimento",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkSoft
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FunChoiceChip(
                        selected = status == FoodStatus.VIETATO,
                        onClick = { status = FoodStatus.VIETATO },
                        label = "Vietato",
                        icon = Icons.Rounded.Block,
                        selectedColor = TomatoDeep,
                        softColor = TomatoSoft,
                        inkColor = TomatoInk,
                        modifier = Modifier.weight(1f)
                    )
                    FunChoiceChip(
                        selected = status == FoodStatus.CONSENTITO,
                        onClick = { status = FoodStatus.CONSENTITO },
                        label = "Consentito",
                        icon = Icons.Rounded.CheckCircle,
                        selectedColor = LeafDeep,
                        softColor = LeafSoft,
                        inkColor = LeafInk,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(allergenBackground)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Checkbox(
                        checked = isAllergen,
                        onCheckedChange = { isAllergen = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SunnyDeep,
                            uncheckedColor = InkSoft,
                            checkmarkColor = Color.White
                        )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("È un allergene ⚠️", color = Ink, style = MaterialTheme.typography.titleSmall)
                        Text("Verifica i tag allergeni delle etichette", color = InkSoft, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            FunButton(
                text = "AGGIUNGI",
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), status, isAllergen) },
                colors = ScanGradient,
                icon = Icons.Rounded.Add,
                height = 46.dp
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = InkSoft, style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

/**
 * Modale per importare elenchi da Word, Excel, CSV o copia-incolla
 */
@Composable
private fun FunImportDialog(
    onDismiss: () -> Unit,
    onImport: (List<FoodItem>) -> Unit
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    var defaultStatus by remember { mutableStateOf(FoodStatus.VIETATO) }

    // Launcher per selezionare file da dispositivo (.txt, .csv)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = reader.readText()
                    textInput = content
                    Toast.makeText(context, "File caricato! Controlla l'anteprima.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Errore nella lettura del file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val parsedPreview = remember(textInput, defaultStatus) {
        if (textInput.isBlank()) emptyList() else FoodImportExportHelper.parseFoodList(textInput, defaultStatus)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FunSurface,
        shape = RoundedCornerShape(32.dp),
        icon = {
            IconBubble(icon = Icons.Rounded.FileUpload, tint = Color.White, containerColor = BerryDeep, size = 52.dp)
        },
        title = {
            Text(
                text = "Importa da Excel / Word 📄",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Incolla il testo copiato da un foglio Word/Excel, oppure scegli un file .txt o .csv dal telefono.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft
                )

                // Pulsante Carica File
                FunSoftButton(
                    text = "SCEGLI FILE (.CSV / .TXT)",
                    onClick = { filePickerLauncher.launch("*/*") },
                    containerColor = BerrySoft,
                    contentColor = BerryInk,
                    icon = Icons.Rounded.FileOpen,
                    height = 48.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo di testo per incollare
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Incolla qui la lista cibi...") },
                    placeholder = { Text("Es:\nLatte, vietato, allergene\nArachidi\nMela, consentito") },
                    minLines = 4,
                    maxLines = 6,
                    shape = RoundedCornerShape(18.dp),
                    colors = funTextFieldColors(BerryDeep),
                    modifier = Modifier.fillMaxWidth()
                )

                // Selezione stato predefinito se non specificato
                Text(
                    text = "Stato se non specificato:",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkSoft
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FunChoiceChip(
                        selected = defaultStatus == FoodStatus.VIETATO,
                        onClick = { defaultStatus = FoodStatus.VIETATO },
                        label = "Vietato",
                        icon = Icons.Rounded.Block,
                        selectedColor = TomatoDeep,
                        softColor = TomatoSoft,
                        inkColor = TomatoInk,
                        modifier = Modifier.weight(1f)
                    )
                    FunChoiceChip(
                        selected = defaultStatus == FoodStatus.CONSENTITO,
                        onClick = { defaultStatus = FoodStatus.CONSENTITO },
                        label = "Consentito",
                        icon = Icons.Rounded.CheckCircle,
                        selectedColor = LeafDeep,
                        softColor = LeafSoft,
                        inkColor = LeafInk,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Badge conteggio anteprima
                if (parsedPreview.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(LeafSoft)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = LeafInk, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Rilevati ${parsedPreview.size} alimenti validi pronti all'importazione!",
                            style = MaterialTheme.typography.titleSmall,
                            color = LeafInk
                        )
                    }
                }
            }
        },
        confirmButton = {
            FunButton(
                text = "IMPORTA (${parsedPreview.size})",
                onClick = { if (parsedPreview.isNotEmpty()) onImport(parsedPreview) },
                enabled = parsedPreview.isNotEmpty(),
                colors = ListGradient,
                height = 46.dp
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = InkSoft, style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
