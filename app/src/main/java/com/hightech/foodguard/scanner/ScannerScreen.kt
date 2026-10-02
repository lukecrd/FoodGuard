package com.hightech.foodguard.scanner

import android.Manifest
import android.app.Activity
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size as GeoSize
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.hightech.foodguard.analysis.AnalysisResult
import com.hightech.foodguard.analysis.Verdict
import com.hightech.foodguard.ui.components.*
import com.hightech.foodguard.ui.theme.*
import com.hightech.foodguard.viewmodel.ScanUiState
import com.hightech.foodguard.viewmodel.ScanViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    viewModel: ScanViewModel,
    onBack: () -> Unit
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val uiState by viewModel.uiState.collectAsState()
    val granted = cameraPermission.status.isGranted

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
    }

    // Icone della status bar chiare sopra la fotocamera, scure sullo sfondo panna
    val view = LocalView.current
    DisposableEffect(granted) {
        val window = (view.context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val previous = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = !granted
        onDispose {
            if (controller != null && previous != null) controller.isAppearanceLightStatusBars = previous
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (granted) {
            CameraPreviewWithScanner(
                onBarcodeDetected = { viewModel.onBarcodeScanned(it) },
                isPaused = uiState !is ScanUiState.Idle
            )
            ScanFrameOverlay()
        } else {
            PermissionRequestContent(onRequest = { cameraPermission.launchPermissionRequest() })
        }

        // Barra superiore
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FunIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Indietro",
                onClick = onBack,
                containerColor = Color.White,
                tint = Ink,
                modifier = Modifier.shadow(6.dp, CircleShape)
            )

            StatusPill(
                text = "SCANNER ATTIVO",
                containerColor = Color.White,
                contentColor = SkyInk,
                modifier = Modifier.shadow(6.dp, RoundedCornerShape(percent = 50)),
                leading = { PulsingStatusDot(color = Sky, size = 6.dp) }
            )
        }

        when (val state = uiState) {
            is ScanUiState.Loading -> FunLoadingOverlay()
            is ScanUiState.Success -> FunResultOverlay(result = state.result, onDismiss = { viewModel.reset() })
            is ScanUiState.Error -> FunErrorOverlay(message = state.message, onDismiss = { viewModel.reset() })
            ScanUiState.Idle -> {}
        }
    }
}

@Composable
private fun PermissionRequestContent(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(FunBackground, FunBackgroundAlt))),
        contentAlignment = Alignment.Center
    ) {
        FloatingBlobs(colors = listOf(Sky, Bubblegum, Sunny), blobAlpha = 0.12f)
        FunCard(
            accentColor = Sky,
            cornerRadius = 32.dp,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BouncyEmoji("📷")
                Text(
                    "Permesso Fotocamera Necessario",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Attiva l'accesso alla fotocamera per leggere i codici a barre sulle confezioni alimentari.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSoft,
                    textAlign = TextAlign.Center
                )
                FunButton(
                    text = "AUTORIZZA FOTOCAMERA",
                    onClick = onRequest,
                    colors = ScanGradient,
                    icon = Icons.Rounded.PhotoCamera,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CameraPreviewWithScanner(onBarcodeDetected: (String) -> Unit, isPaused: Boolean) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val analyzer = remember { BarcodeAnalyzer(onBarcodeDetected) }

    LaunchedEffect(isPaused) {
        if (!isPaused) analyzer.reset()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer) }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

/**
 * Mirino allegro: velo scuro con finestra arrotondata, angoli multicolore che "respirano"
 * e linea di scansione arcobaleno con scia luminosa.
 */
@Composable
private fun ScanFrameOverlay() {
    val transition = rememberInfiniteTransition(label = "scanner")
    val scanProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scanProgress"
    )
    val cornerPulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "cornerPulse"
    )
    val hueShift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "hueShift"
    )
    val cornerColors = listOf(Sky, Bubblegum, Sunny, Leaf)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            val frameW = 280.dp.toPx()
            val frameH = 180.dp.toPx()
            val radius = 28.dp.toPx()
            val left = (size.width - frameW) / 2f
            val top = (size.height - frameH) / 2f
            val frame = Rect(left, top, left + frameW, top + frameH)

            // Velo scuro con "buco" arrotondato
            drawRect(Color(0xFF1B1530).copy(alpha = 0.55f))
            drawRoundRect(
                color = Color.Transparent,
                topLeft = frame.topLeft,
                size = frame.size,
                cornerRadius = CornerRadius(radius, radius),
                blendMode = BlendMode.Clear
            )

            // Angoli colorati (rotazione dei colori nel tempo)
            val inset = -6.dp.toPx() * cornerPulse
            val expanded = frame.inflate(-inset)
            val colorOffset = (hueShift * cornerColors.size).toInt()
            val mirrors = listOf(1f to 1f, -1f to 1f, -1f to -1f, 1f to -1f)
            mirrors.forEachIndexed { i, (sx, sy) ->
                scale(scaleX = sx, scaleY = sy, pivot = expanded.center) {
                    drawCorner(
                        frame = expanded,
                        radius = radius,
                        length = 34.dp.toPx(),
                        color = cornerColors[(i + colorOffset) % cornerColors.size]
                    )
                }
            }

            // Linea di scansione arcobaleno con scia
            val lineY = frame.top + 12.dp.toPx() + (frame.height - 24.dp.toPx()) * scanProgress
            val lineLeft = frame.left + 14.dp.toPx()
            val lineRight = frame.right - 14.dp.toPx()
            val trail = 36.dp.toPx()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Sky.copy(alpha = 0.28f)),
                    startY = lineY - trail,
                    endY = lineY
                ),
                topLeft = Offset(lineLeft, lineY - trail),
                size = GeoSize(lineRight - lineLeft, trail)
            )
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Sky, Berry, Bubblegum, Sunny, Leaf),
                    startX = lineLeft,
                    endX = lineRight
                ),
                start = Offset(lineLeft, lineY),
                end = Offset(lineRight, lineY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Istruzioni in basso
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                .shadow(8.dp, RoundedCornerShape(percent = 50))
                .clip(RoundedCornerShape(percent = 50))
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconBubble(icon = Icons.Rounded.QrCodeScanner, tint = Color.White, containerColor = SkyDeep, size = 30.dp, iconSize = 18.dp)
            Text(
                text = "Inquadra il codice a barre nel riquadro",
                style = MaterialTheme.typography.titleSmall,
                color = Ink
            )
        }
    }
}

/** Disegna l'angolo in alto a sinistra di [frame]; gli altri si ottengono specchiando. */
private fun DrawScope.drawCorner(frame: Rect, radius: Float, length: Float, color: Color) {
    val path = Path().apply {
        moveTo(frame.left, frame.top + length)
        lineTo(frame.left, frame.top + radius)
        arcTo(
            rect = Rect(frame.left, frame.top, frame.left + radius * 2f, frame.top + radius * 2f),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(frame.left + length, frame.top)
    }
    drawPath(path = path, color = color, style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round))
}

/**
 * Overlay di caricamento con pallini colorati che saltellano.
 */
@Composable
private fun FunLoadingOverlay() {
    val transition = rememberInfiniteTransition(label = "loading")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "loadingPhase"
    )
    val dotColors = listOf(Sky, Bubblegum, Sunny, Leaf)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1530).copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        StaggeredEntrance(delayMillis = 0, modifier = Modifier.padding(32.dp)) {
            FunCard(accentColor = Sky, cornerRadius = 32.dp, contentPadding = 28.dp) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.height(40.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dotColors.forEachIndexed { i, color ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer {
                                        val local = ((phase - i * 0.18f) % 1f + 1f) % 1f
                                        val bounce = kotlin.math.sin(local * Math.PI).toFloat()
                                        translationY = -bounce * 20.dp.toPx()
                                    }
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                    Text(
                        text = "Ricerca ingredienti... 🔍",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink
                    )
                    Text(
                        text = "Interrogazione del database Open Food Facts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Riquadro di errore con entrata a molla.
 */
@Composable
private fun FunErrorOverlay(message: String, onDismiss: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val cardScale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "errorScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1530).copy(alpha = 0.6f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        FunCard(
            accentColor = Tomato,
            cornerRadius = 32.dp,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                    alpha = ((cardScale - 0.7f) / 0.3f).coerceIn(0f, 1f)
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconBubble(icon = Icons.Rounded.SearchOff, tint = TomatoInk, containerColor = TomatoSoft, size = 72.dp)
                Text(
                    text = "Prodotto Non Trovato 🤔",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSoft,
                    textAlign = TextAlign.Center
                )
                FunButton(
                    text = "RIPROVA",
                    onClick = onDismiss,
                    colors = ScanGradient,
                    icon = Icons.Rounded.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Risultato dell'analisi: la card entra con un rimbalzo, la fascia del verdetto
 * passa dall'azzurro al colore del verdetto, l'icona "esplode" e, se il prodotto
 * e' sicuro, partono i coriandoli.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FunResultOverlay(result: AnalysisResult, onDismiss: () -> Unit) {
    val accent = when (result.verdict) {
        Verdict.SICURO -> Leaf
        Verdict.ATTENZIONE -> Sunny
        Verdict.VIETATO -> Tomato
    }
    val gradient = when (result.verdict) {
        Verdict.SICURO -> SafeGradient
        Verdict.ATTENZIONE -> WarningGradient
        Verdict.VIETATO -> DangerGradient
    }
    val iconTint = when (result.verdict) {
        Verdict.SICURO -> LeafDeep
        Verdict.ATTENZIONE -> SunnyDeep
        Verdict.VIETATO -> TomatoDeep
    }
    val verdictLabel = when (result.verdict) {
        Verdict.SICURO -> "PRODOTTO SICURO ✅"
        Verdict.ATTENZIONE -> "DATI INSUFFICIENTI ⚠️"
        Verdict.VIETATO -> "ALIMENTO VIETATO 🚫"
    }
    val verdictIcon = when (result.verdict) {
        Verdict.SICURO -> Icons.Rounded.CheckCircle
        Verdict.ATTENZIONE -> Icons.Rounded.Warning
        Verdict.VIETATO -> Icons.Rounded.Dangerous
    }

    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }

    val cardScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "resultScale"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessVeryLow),
        label = "iconScale"
    )
    val bandStart by animateColorAsState(
        targetValue = if (revealed) gradient.first() else SkyDeep,
        animationSpec = tween(700, delayMillis = 150),
        label = "bandStart"
    )
    val bandEnd by animateColorAsState(
        targetValue = if (revealed) gradient.last() else BerryDeep,
        animationSpec = tween(700, delayMillis = 150),
        label = "bandEnd"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1530).copy(alpha = 0.65f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        val shape = RoundedCornerShape(34.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                    alpha = ((cardScale - 0.6f) / 0.4f).coerceIn(0f, 1f)
                }
                .shadow(18.dp, shape, ambientColor = accent, spotColor = accent)
                .clip(shape)
                .background(FunSurface)
                .verticalScroll(rememberScrollState())
        ) {
            // Fascia colorata del verdetto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(bandStart, bandEnd)))
                    .padding(vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                FloatingBlobs(
                    modifier = Modifier.matchParentSize(),
                    colors = listOf(Color.White, Color.White, accent),
                    blobAlpha = 0.14f,
                    durationMillis = 6000
                )
                if (result.verdict == Verdict.SICURO) {
                    ConfettiBurst(
                        colors = listOf(Sunny, Bubblegum, Sky, Color.White, Leaf),
                        modifier = Modifier.matchParentSize()
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                                rotationZ = (1f - iconScale) * -40f
                            }
                            .shadow(10.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = verdictIcon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Text(
                        text = verdictLabel,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dettagli prodotto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(FunSurfaceSoft)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconBubble(
                        icon = Icons.Rounded.Sell,
                        tint = Color.White,
                        containerColor = BerryDeep,
                        size = 40.dp,
                        shape = RoundedCornerShape(14.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = result.productName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Ink
                        )
                        result.brand?.let {
                            Text(
                                text = "Marca: $it",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSoft
                            )
                        }
                    }
                }

                // Cibi della lista trovati nel prodotto
                val threats = result.matchedAllergens + result.matchedForbidden
                if (threats.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(TomatoSoft)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = TomatoInk,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Contiene cibi della tua lista vietati:",
                                style = MaterialTheme.typography.titleSmall,
                                color = TomatoInk
                            )
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            threats.forEach { item ->
                                StatusPill(
                                    text = item.replaceFirstChar { it.uppercase() },
                                    containerColor = TomatoDeep,
                                    contentColor = Color.White,
                                    icon = Icons.Rounded.Block
                                )
                            }
                        }
                    }
                } else if (result.verdict == Verdict.SICURO) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(LeafSoft)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = LeafInk,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Nessun allergene o ingrediente vietato rilevato! 🎉",
                            style = MaterialTheme.typography.titleSmall,
                            color = LeafInk
                        )
                    }
                }

                FunButton(
                    text = "SCANSIONA ALTRO",
                    onClick = onDismiss,
                    colors = gradient,
                    icon = Icons.Rounded.QrCodeScanner,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
