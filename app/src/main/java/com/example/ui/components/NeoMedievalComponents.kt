package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BattleUnit
import com.example.model.Hero
import com.example.model.HeroRole
import com.example.ui.theme.*

@Composable
fun MedievalSurface(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    borderColor: Color = if (isSelected) AntiqueBronze else SageOlive.copy(alpha = 0.6f),
    borderWidth: Dp = if (isSelected) 2.dp else 1.dp,
    backgroundColor: Color = if (isSelected) LightPineGreen else PineGreen,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = AntiqueBronze),
                onClick = onClick
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .then(clickModifier),
        content = content
    )
}

@Composable
fun MedievalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    backgroundColor: Color = AntiqueBronze,
    contentColor: Color = DeepSlate
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = SageOlive.copy(alpha = 0.3f),
            disabledContentColor = SageOlive.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Composable
fun MedievalSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = DarkPineGreen,
            contentColor = WarmCream,
            disabledContainerColor = DarkPineGreen.copy(alpha = 0.4f),
            disabledContentColor = SageOlive.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, SageOlive),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = WarmCream
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = WarmCream
                )
            )
        }
    }
}

@Composable
fun MedievalIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = WarmCream,
    backgroundColor: Color = DarkPineGreen,
    borderColor: Color = SageOlive
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = AntiqueBronze),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun HealthBar(
    currentHp: Int,
    maxHp: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false
) {
    val pct = if (maxHp > 0) (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f) else 0f
    val barColor = when {
        pct > 0.5f -> HealthGreen
        pct > 0.25f -> HealthYellow
        else -> HealthRed
    }

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Vida:",
                    style = MaterialTheme.typography.bodySmall.copy(color = SageOlive)
                )
                Text(
                    text = "$currentHp / $maxHp",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = WarmCream
                    )
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF141E22))
                .border(0.5.dp, SageOlive.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(pct)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(barColor, barColor.copy(alpha = 0.8f))
                        )
                    )
            )
        }
    }
}

@Composable
fun UnitTokenView(
    unit: BattleUnit,
    isActivePlayerTurn: Boolean,
    isSelectedAsTarget: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isProtected: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    val isPlayable = isActivePlayerTurn && unit.isAlive && !unit.hasActed
    val roleColor = when (unit.hero.role) {
        HeroRole.GUERRERO -> AntiqueBronze
        HeroRole.MISTICO -> MysticMana
        HeroRole.MAGO -> MagicPurple
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(min = 44.dp, max = 50.dp)
            .clickable(
                enabled = unit.isAlive,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp)
        ) {
            // Active player turn pulsing ring
            if (isPlayable) {
                Box(
                    modifier = Modifier
                        .size(44.dp * glowScale)
                        .clip(CircleShape)
                        .border(2.dp, AntiqueBronzeBright, CircleShape)
                        .background(AntiqueBronze.copy(alpha = 0.15f))
                )
            }

            // Target ring
            if (isSelectedAsTarget) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, HealthRed, CircleShape)
                        .background(HealthRed.copy(alpha = 0.2f))
                )
            }

            // Main token circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (!unit.isAlive) Color(0xFF141A1C)
                        else if (unit.hasActed) DarkPineGreen.copy(alpha = 0.6f)
                        else PineGreen
                    )
                    .border(
                        width = if (isPlayable) 2.dp else 1.dp,
                        color = if (!unit.isAlive) SageOlive.copy(alpha = 0.3f)
                        else if (isPlayable) AntiqueBronze
                        else if (isProtected) AntiqueBronzeBright.copy(alpha = 0.7f)
                        else SageOlive,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!unit.isAlive) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Defeated",
                        tint = SageOlive.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    // Hero role icon & badge
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val icon = when (unit.hero.role) {
                            HeroRole.GUERRERO -> Icons.Default.Shield
                            HeroRole.MISTICO -> Icons.Default.AutoAwesome
                            HeroRole.MAGO -> Icons.Default.LocalFireDepartment
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = unit.hero.name,
                            tint = if (unit.hasActed) SageOlive else roleColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = unit.hero.role.shortBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 8.sp,
                                color = if (unit.hasActed) SageOlive else WarmCream
                            )
                        )
                    }
                }
            }

            // Frontline Protection Shield Badge
            if (isProtected && unit.isAlive) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(DarkPineGreen)
                        .border(1.dp, AntiqueBronzeBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Protegido por Guerreros",
                        tint = AntiqueBronzeBright,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Mini HP Bar & value
        if (unit.isAlive) {
            HealthBar(
                currentHp = unit.currentHp,
                maxHp = unit.maxHp,
                modifier = Modifier.width(42.dp)
            )
            Text(
                text = if (isProtected) "🛡 ${unit.currentHp}" else "${unit.currentHp}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    color = if (isProtected) AntiqueBronzeBright else WarmCream,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "CAÍDO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 7.sp,
                    color = SageOlive.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun HeroAvatarCircle(
    hero: Hero,
    size: Dp = 48.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val roleColor = when (hero.role) {
        HeroRole.GUERRERO -> AntiqueBronze
        HeroRole.MISTICO -> MysticMana
        HeroRole.MAGO -> MagicPurple
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(DarkPineGreen)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AntiqueBronze else SageOlive,
                shape = CircleShape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = AntiqueBronze),
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = when (hero.role) {
                HeroRole.GUERRERO -> Icons.Default.Shield
                HeroRole.MISTICO -> Icons.Default.AutoAwesome
                HeroRole.MAGO -> Icons.Default.LocalFireDepartment
            }
            Icon(
                imageVector = icon,
                contentDescription = hero.name,
                tint = roleColor,
                modifier = Modifier.size((size.value * 0.45f).dp)
            )
            Text(
                text = hero.avatarId,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = (size.value * 0.18f).dp.value.sp,
                    color = WarmCream
                )
            )
        }
    }
}

@Composable
fun MedievalDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSlateScrim)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 340.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PineGreen)
                    .border(1.5.dp, SageOlive, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Row with Title and Close X
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCream
                            )
                        )

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = WarmCream
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = SageOlive.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    content()
                }
            }
        }
    }
}
