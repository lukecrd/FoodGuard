package com.hightech.foodguard.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Palette "allegra e colorata" di FoodGuard.
 *
 * Ogni colore ha quattro varianti:
 *  - <Nome>       tono brillante, per decorazioni, bordi, ombre colorate
 *  - <Nome>Deep   tono saturo e scuro, per sfondi con testo/icone bianche (contrasto >= 4.5:1)
 *  - <Nome>Soft   tono pastello, per sfondi di chip, badge e riquadri
 *  - <Nome>Ink    tono scurissimo, per testo su sfondo <Nome>Soft (contrasto >= 4.5:1)
 */

// Sfondi caldi color panna
val FunBackground = Color(0xFFFFF8EE)
val FunBackgroundAlt = Color(0xFFFFEEDB)
val FunSurface = Color(0xFFFFFFFF)
val FunSurfaceSoft = Color(0xFFFFF3E3)
val FunBorder = Color(0xFFF2E2CC)

// Testo (prugna scuro, piu' morbido del nero)
val Ink = Color(0xFF2D2443)
val InkSoft = Color(0xFF615879)
val InkMuted = Color(0xFF8C84A3)

// Verde foglia: consentito / sicuro
val Leaf = Color(0xFF34C759)
val LeafDeep = Color(0xFF15803D)
val LeafSoft = Color(0xFFDDF7E3)
val LeafInk = Color(0xFF166534)

// Rosso pomodoro: vietato / allerta
val Tomato = Color(0xFFFF5A4E)
val TomatoDeep = Color(0xFFC62828)
val TomatoSoft = Color(0xFFFFE4E0)
val TomatoInk = Color(0xFFB91C1C)

// Giallo sole: attenzione / allergeni / dati insufficienti
val Sunny = Color(0xFFFFC53D)
val SunnyDeep = Color(0xFFA15C00)
val SunnySoft = Color(0xFFFFF4CC)
val SunnyInk = Color(0xFF8A4B00)

// Arancia: pasti
val Orange = Color(0xFFFF8A3D)
val OrangeDeep = Color(0xFFC2410C)
val OrangeSoft = Color(0xFFFFE7D3)
val OrangeInk = Color(0xFF9A3412)

// Azzurro cielo: scanner / azioni principali
val Sky = Color(0xFF38BDF8)
val SkyDeep = Color(0xFF0369A1)
val SkySoft = Color(0xFFDFF3FF)
val SkyInk = Color(0xFF075985)

// Mirtillo: lista cibi
val Berry = Color(0xFFA855F7)
val BerryDeep = Color(0xFF7E22CE)
val BerrySoft = Color(0xFFF1E6FF)
val BerryInk = Color(0xFF6B21A8)

// Fragola / gomma da masticare: accenti
val Bubblegum = Color(0xFFFF6FB5)
val BubblegumDeep = Color(0xFFBE185D)
val BubblegumSoft = Color(0xFFFFE3F1)
val BubblegumInk = Color(0xFF9D174D)

// Verde acqua per sfumature "sicuro"
val TealDeep = Color(0xFF0F766E)

// Sfumature pronte all'uso (tutte con contrasto AA per testo bianco)
val HeroGradient = listOf(SkyDeep, BerryDeep, BubblegumDeep)
val ScanGradient = listOf(SkyDeep, BerryDeep)
val ListGradient = listOf(BerryDeep, BubblegumDeep)
val MealGradient = listOf(OrangeDeep, BubblegumDeep)
val SafeGradient = listOf(LeafDeep, TealDeep)
val WarningGradient = listOf(SunnyDeep, OrangeDeep)
val DangerGradient = listOf(TomatoDeep, BubblegumDeep)
