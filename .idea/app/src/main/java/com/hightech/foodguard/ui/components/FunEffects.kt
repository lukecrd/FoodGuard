package com.hightech.foodguard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightech.foodguard.ui.theme.Leaf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Scala animata "a molla" per l'effetto rimbalzo alla pressione.
 * Leggere il valore dentro un graphicsLayer per evitare ricomposizioni.
 */
@Composable
fun rememberPressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.94f,
    enabled: Boolean = true
): State<Float> {
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed && enabled) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
}

/**
 * Sfondo a sfumatura che scorre lentamente avanti e indietro.
 */
fun Modifier.animatedGradient(
    colors: List<Color>,
    durationMillis: Int = 6000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "gradient")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientShift"
    )
    val safeColors = if (colors.size < 2) listOf(colors.first(), colors.first()) else colors
    drawBehind {
        val w = size.width
        drawRect(
            brush = Brush.linearGradient(
                colors = safeColors,
                start = Offset(-w * shift, 0f),
                end = Offset(w * (2f - shift), size.height),
                tileMode = TileMode.Mirror
            )
        )
    }
}

/**
 * Bolle decorative colorate che fluttuano lentamente sullo sfondo.
 */
@Composable
fun FloatingBlobs(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    blobAlpha: Float = 0.16f,
    durationMillis: Int = 9000
) {
    val transition = rememberInfiniteTransition(label = "blobs")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blobPhase"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        colors.forEachIndexed { i, color ->
            val p = phase + i * 2.1f
            val baseX = 0.12f + 0.76f * ((i * 0.37f + 0.1f) % 1f)
            val baseY = 0.15f + 0.7f * ((i * 0.61f + 0.2f) % 1f)
            val cx = size.width * baseX + cos(p) * size.width * 0.06f
            val cy = size.height * baseY + sin(p) * size.height * 0.08f
            val radius = size.minDimension * (0.22f + 0.08f * (i % 3))
            drawCircle(color = color.copy(alpha = blobAlpha), radius = radius, center = Offset(cx, cy))
        }
    }
}

/**
 * Entrata a cascata: dissolvenza + scivolamento dal basso + leggero zoom.
 * Il contenuto e' composto subito (nessun salto di layout), si anima solo la grafica.
 */
@Composable
fun StaggeredEntrance(
    delayMillis: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    content: @Composable () -> Unit
) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = state,
        modifier = modifier,
        enter = fadeIn(tween(420, delayMillis = delayMillis)) +
            slideInVertically(tween(520, delayMillis = delayMillis, easing = FastOutSlowInEasing)) { it / 4 } +
            scaleIn(tween(520, delayMillis = delayMillis, easing = FastOutSlowInEasing), initialScale = 0.92f)
    ) {
        content()
    }
}

/**
 * Emoji che saltella e dondola, per stati vuoti e intestazioni.
 */
@Composable
fun BouncyEmoji(
    emoji: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 44.sp
) {
    val transition = rememberInfiniteTransition(label = "emoji")
    val lift by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "emojiLift"
    )
    val tilt by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "emojiTilt"
    )
    Text(
        text = emoji,
        fontSize = fontSize,
        modifier = modifier.graphicsLayer {
            translationY = lift.dp.toPx()
            rotationZ = tilt
        }
    )
}

/**
 * Pallino di stato con alone che pulsa.
 */
@Composable
fun PulsingStatusDot(
    modifier: Modifier = Modifier,
    color: Color = Leaf,
    size: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val haloScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "haloScale"
    )
    Box(modifier = modifier.size(size * 2), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    scaleX = haloScale
                    scaleY = haloScale
                    alpha = (2f - haloScale).coerceIn(0f, 1f) * 0.6f
                }
                .clip(CircleShape)
                .background(color)
        )
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
        )
    }
}

private class ConfettiParticle(
    val angle: Float,
    val speed: Float,
    val width: Float,
    val height: Float,
    val spin: Float,
    val color: Color
)

/**
 * Esplosione di coriandoli colorati (una sola volta), per festeggiare un prodotto sicuro.
 */
@Composable
fun ConfettiBurst(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    particleCount: Int = 32
) {
    val particles = remember(colors, particleCount) {
        List(particleCount) { i ->
            val rnd = Random(i * 31 + 7)
            ConfettiParticle(
                angle = (rnd.nextFloat() * 2 * PI).toFloat(),
                speed = 0.45f + rnd.nextFloat() * 0.6f,
                width = 5f + rnd.nextFloat() * 4f,
                height = 8f + rnd.nextFloat() * 6f,
                spin = if (rnd.nextBoolean()) 1f else -1f,
                color = colors[i % colors.size]
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(1500, easing = LinearOutSlowInEasing))
    }
    Canvas(modifier = modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val maxDistance = size.minDimension * 0.65f
        particles.forEach { pt ->
            val distance = maxDistance * pt.speed * p
            val x = center.x + cos(pt.angle) * distance
            val y = center.y + sin(pt.angle) * distance + size.height * 0.25f * p * p
            val w = pt.width.dp.toPx()
            val h = pt.height.dp.toPx()
            rotate(degrees = pt.spin * p * 540f, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = pt.color.copy(alpha = (1f - p).coerceIn(0f, 1f)),
                    topLeft = Offset(x - w / 2f, y - h / 2f),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(w / 3f, w / 3f)
                )
            }
        }
    }
}
