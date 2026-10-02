package com.hightech.foodguard.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hightech.foodguard.data.FoodImportExportHelper
import com.hightech.foodguard.data.FoodStatus
import com.hightech.foodguard.data.MealImportExportHelper
import com.hightech.foodguard.ui.components.*
import com.hightech.foodguard.ui.theme.*
import com.hightech.foodguard.viewmodel.FoodViewModel
import com.hightech.foodguard.viewmodel.MealViewModel

@Composable
fun HomeScreen(
    foodViewModel: FoodViewModel,
    mealViewModel: MealViewModel,
    onScanClick: () -> Unit,
    onListClick: () -> Unit,
    onMealsClick: () -> Unit
) {
    val foods by foodViewModel.foods.collectAsState()
    val meals by mealViewModel.meals.collectAsState()
    val forbiddenCount = foods.count { it.status == FoodStatus.VIETATO }
    val allowedCount = foods.count { it.status == FoodStatus.CONSENTITO }
    val allergenCount = foods.count { it.isAllergen }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(FunBackground, FunBackgroundAlt)))
    ) {
        FloatingBlobs(colors = listOf(Sunny, Bubblegum, Sky, Leaf), blobAlpha = 0.10f, durationMillis = 14000)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            StaggeredEntrance(delayMillis = 0) {
                HeroHeader(forbiddenCount = forbiddenCount, allergenCount = allergenCount)
            }

            StaggeredEntrance(delayMillis = 90) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        icon = Icons.Rounded.Block,
                        count = forbiddenCount,
                        label = "Vietati",
                        accent = Tomato,
                        deep = TomatoDeep,
                        soft = TomatoSoft,
                        ink = TomatoInk,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Rounded.CheckCircle,
                        count = allowedCount,
                        label = "Consentiti",
                        accent = Leaf,
                        deep = LeafDeep,
                        soft = LeafSoft,
                        ink = LeafInk,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Rounded.Warning,
                        count = allergenCount,
                        label = "Allergeni",
                        accent = Sunny,
                        deep = SunnyDeep,
                        soft = SunnySoft,
                        ink = SunnyInk,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Scanner: azione principale
            StaggeredEntrance(delayMillis = 180) {
                FunCard(accentColor = Sky, cornerRadius = 32.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PulsingScanButton(onClick = onScanClick)
                        Text(
                            text = "Scansiona Codice a Barre",
                            style = MaterialTheme.typography.titleLarge,
                            color = Ink,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Inquadra la confezione di un alimento per verificare ingredienti e allergeni in tempo reale.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkSoft,
                            textAlign = TextAlign.Center
                        )
                        FunButton(
                            text = "AVVIA SCANSIONE",
                            onClick = onScanClick,
                            colors = ScanGradient,
                            icon = Icons.Rounded.QrCodeScanner,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Lista cibi + condivisione rapida
            StaggeredEntrance(delayMillis = 270) {
                FunCard(accentColor = Berry, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            IconBubble(
                                icon = Icons.AutoMirrored.Rounded.ListAlt,
                                tint = Color.White,
                                containerColor = BerryDeep,
                                size = 54.dp,
                                shape = RoundedCornerShape(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "La Mia Lista Cibi 🍓",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Ink
                                )
                                Text(
                                    text = "${foods.size} ingredienti salvati • Importa o esporta",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkSoft
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FunButton(
                                text = "GESTISCI",
                                onClick = onListClick,
                                colors = ListGradient,
                                icon = Icons.AutoMirrored.Rounded.FormatListBulleted,
                                height = 48.dp,
                                modifier = Modifier.weight(1f)
                            )
                            FunSoftButton(
                                text = "CONDIVIDI",
                                onClick = { FoodImportExportHelper.shareViaWhatsApp(context, foods) },
                                containerColor = LeafSoft,
                                contentColor = LeafInk,
                                icon = Icons.Rounded.Share,
                                height = 48.dp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Linee guida pasti completi (separate dai cibi singoli)
            StaggeredEntrance(delayMillis = 360) {
                FunCard(accentColor = Orange, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            IconBubble(
                                icon = Icons.Rounded.Restaurant,
                                tint = Color.White,
                                containerColor = OrangeDeep,
                                size = 54.dp,
                                shape = RoundedCornerShape(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Linee Guida Pasti",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Ink
                                    )
                                    StatusPill(text = "NUOVO", containerColor = LeafSoft, contentColor = LeafInk)
                                }
                                Text(
                                    text = if (meals.isNotEmpty())
                                        "${meals.size} pasti completi e menu bilanciati"
                                    else
                                        "Specifica colazione, pranzo e cena completi",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkSoft
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FunButton(
                                text = "APRI PASTI",
                                onClick = onMealsClick,
                                colors = MealGradient,
                                icon = Icons.Rounded.RestaurantMenu,
                                height = 48.dp,
                                modifier = Modifier.weight(1f)
                            )
                            FunSoftButton(
                                text = "INVIA MENU",
                                onClick = { MealImportExportHelper.shareViaWhatsApp(context, meals) },
                                containerColor = SunnySoft,
                                contentColor = SunnyInk,
                                icon = Icons.Rounded.Share,
                                height = 48.dp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

/**
 * Intestazione con sfumatura animata, bolle fluttuanti e logo che dondola.
 */
@Composable
private fun HeroHeader(forbiddenCount: Int, allergenCount: Int) {
    val shape = RoundedCornerShape(32.dp)
    val transition = rememberInfiniteTransition(label = "hero")
    val wiggle by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoWiggle"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, shape, ambientColor = Berry.copy(alpha = 0.5f), spotColor = BerryDeep.copy(alpha = 0.6f))
            .clip(shape)
            .animatedGradient(HeroGradient)
    ) {
        FloatingBlobs(
            modifier = Modifier.matchParentSize(),
            colors = listOf(Color.White, Sunny, Color.White, Bubblegum),
            blobAlpha = 0.16f
        )
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .graphicsLayer { rotationZ = wiggle }
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = BerryDeep,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FoodGuard",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Il tuo alleato sicuro contro gli allergeni ✨",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "STATO PROTEZIONE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = if (forbiddenCount > 0) "$forbiddenCount cibi monitorati con allerta" else "Nessuna restrizione salvata",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = if (allergenCount > 0) "⚠️ $allergenCount allergeni prioritari" else "Aggiungi ingredienti per iniziare 🌱",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
                StatusPill(
                    text = "ATTIVO",
                    containerColor = Color.White,
                    contentColor = LeafInk,
                    leading = { PulsingStatusDot(color = Leaf, size = 7.dp) }
                )
            }
        }
    }
}

/**
 * Piccola card statistica con icona e contatore animato.
 */
@Composable
private fun StatCard(
    icon: ImageVector,
    count: Int,
    label: String,
    accent: Color,
    deep: Color,
    soft: Color,
    ink: Color,
    modifier: Modifier = Modifier
) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "statCount"
    )
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .shadow(8.dp, shape, ambientColor = accent.copy(alpha = 0.4f), spotColor = accent.copy(alpha = 0.55f))
            .clip(shape)
            .background(soft)
            .border(2.dp, accent.copy(alpha = 0.45f), shape)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconBubble(icon = icon, tint = Color.White, containerColor = deep, size = 38.dp)
        Text(
            text = "$animatedCount",
            style = MaterialTheme.typography.headlineMedium,
            color = ink
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = ink,
            maxLines = 1
        )
    }
}

/**
 * Grande pulsante scanner rotondo con onde pulsanti e "respiro".
 */
@Composable
private fun PulsingScanButton(onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "scanPulse")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = rememberPressScale(interactionSource, pressedScale = 0.88f)

    Box(modifier = Modifier.size(132.dp), contentAlignment = Alignment.Center) {
        // Due onde sfasate che si espandono e svaniscono
        listOf(0f, 0.5f).forEachIndexed { i, offset ->
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer {
                        val progress = (wave + offset) % 1f
                        val s = 1f + progress * 0.5f
                        scaleX = s
                        scaleY = s
                        alpha = (1f - progress) * 0.55f
                    }
                    .clip(CircleShape)
                    .background(if (i == 0) Sky else Bubblegum)
            )
        }
        Box(
            modifier = Modifier
                .size(88.dp)
                .graphicsLayer {
                    val s = breathe * pressScale.value
                    scaleX = s
                    scaleY = s
                }
                .shadow(12.dp, CircleShape, ambientColor = Sky, spotColor = BerryDeep)
                .clip(CircleShape)
                .background(Brush.linearGradient(ScanGradient))
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    role = Role.Button,
                    onClickLabel = "Avvia scansione",
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.QrCodeScanner,
                contentDescription = "Avvia scansione",
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }
    }
}
