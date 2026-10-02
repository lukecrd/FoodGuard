package com.hightech.foodguard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hightech.foodguard.ui.theme.FunBorder
import com.hightech.foodguard.ui.theme.FunSurface
import com.hightech.foodguard.ui.theme.FunSurfaceSoft
import com.hightech.foodguard.ui.theme.Ink
import com.hightech.foodguard.ui.theme.InkSoft
import com.hightech.foodguard.ui.theme.ScanGradient
import com.hightech.foodguard.ui.theme.Sunny

private val PillShape = RoundedCornerShape(percent = 50)

/**
 * Card bianca arrotondata con ombra colorata morbida, bordo tinta pastello
 * e (se cliccabile) effetto rimbalzo alla pressione.
 */
@Composable
fun FunCard(
    modifier: Modifier = Modifier,
    accentColor: Color,
    cornerRadius: Dp = 28.dp,
    containerColor: Color = FunSurface,
    contentPadding: Dp = 18.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.97f, enabled = onClick != null)
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.35f),
                spotColor = accentColor.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(containerColor)
            .background(
                Brush.verticalGradient(
                    listOf(accentColor.copy(alpha = 0.10f), Color.Transparent, Color.Transparent)
                )
            )
            .border(2.dp, accentColor.copy(alpha = 0.25f), shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Pulsante a pillola con sfumatura colorata, ombra tinta e rimbalzo.
 * Usare colori "Deep" per garantire il contrasto del testo bianco.
 */
@Composable
fun FunButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = ScanGradient,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 52.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.93f, enabled = enabled)
    val brushColors = if (colors.size < 2) listOf(colors.first(), colors.first()) else colors
    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                alpha = if (enabled) 1f else 0.45f
            }
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = PillShape,
                ambientColor = brushColors.last().copy(alpha = 0.4f),
                spotColor = brushColors.first().copy(alpha = 0.6f)
            )
            .clip(PillShape)
            .background(Brush.horizontalGradient(brushColors))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Pulsante secondario a pillola, sfondo pastello e testo scuro della stessa tinta.
 */
@Composable
fun FunSoftButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 52.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.93f, enabled = enabled)
    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                alpha = if (enabled) 1f else 0.45f
            }
            .clip(PillShape)
            .background(containerColor)
            .border(2.dp, contentColor.copy(alpha = 0.25f), PillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(7.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * FAB a pillola che "respira" dolcemente per attirare l'attenzione.
 */
@Composable
fun FunFab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "fab")
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabBreathe"
    )
    FunButton(
        text = text,
        onClick = onClick,
        icon = icon,
        colors = colors,
        height = 58.dp,
        modifier = modifier.graphicsLayer {
            scaleX = breathe
            scaleY = breathe
        }
    )
}

/**
 * Pulsante icona rotondo con rimbalzo.
 */
@Composable
fun FunIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    containerColor: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.82f)
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/**
 * Icona dentro una "bolla" colorata.
 */
@Composable
fun IconBubble(
    icon: ImageVector,
    tint: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = size * 0.52f,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/**
 * Pillola di stato (es. VIETATO, CONSENTITO, ALLERGENE).
 */
@Composable
fun StatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    leading: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        leading?.invoke()
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1
        )
    }
}

/**
 * Chip di scelta a pillola con colori animati e rimbalzo.
 */
@Composable
fun FunChoiceChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    selectedColor: Color,
    softColor: Color,
    inkColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val background by animateColorAsState(if (selected) selectedColor else softColor, label = "chipBackground")
    val foreground by animateColorAsState(if (selected) Color.White else inkColor, label = "chipForeground")
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.9f)
    Row(
        modifier = modifier
            .graphicsLayer {
                val s = scale.value * (if (selected) 1.03f else 1f)
                scaleX = s
                scaleY = s
            }
            .clip(PillShape)
            .background(background)
            .border(1.5.dp, if (selected) selectedColor else inkColor.copy(alpha = 0.18f), PillShape)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = foreground, modifier = Modifier.size(17.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = foreground,
            maxLines = 1
        )
    }
}

/**
 * Intestazione "galleggiante" con sfumatura animata e bolle, per le schermate secondarie.
 */
@Composable
fun FunHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    ambientColor = gradient.last().copy(alpha = 0.5f),
                    spotColor = gradient.first().copy(alpha = 0.6f)
                )
                .clip(shape)
                .animatedGradient(gradient)
        ) {
            FloatingBlobs(
                modifier = Modifier.matchParentSize(),
                colors = listOf(Color.White, Sunny, Color.White),
                blobAlpha = 0.14f
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FunIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Indietro",
                    onClick = onBack,
                    containerColor = Color.White,
                    tint = Ink,
                    size = 42.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.92f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}

/**
 * Colori coerenti per i campi di testo.
 */
@Composable
fun funTextFieldColors(accent: Color): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accent,
    unfocusedBorderColor = FunBorder,
    focusedLabelColor = accent,
    unfocusedLabelColor = InkSoft,
    cursorColor = accent,
    focusedContainerColor = FunSurface,
    unfocusedContainerColor = FunSurfaceSoft,
    focusedTextColor = Ink,
    unfocusedTextColor = Ink,
    focusedLeadingIconColor = accent,
    unfocusedLeadingIconColor = accent
)
