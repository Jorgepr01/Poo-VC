package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttackType
import com.example.model.BattleUnit
import com.example.model.HeroRole
import com.example.model.StrategyRollResult
import com.example.ui.components.HeroAvatarCircle
import com.example.ui.components.MedievalButton
import com.example.ui.theme.*

@Composable
fun AttackModal(
    attacker: BattleUnit,
    targetUnits: List<BattleUnit>, // Available targets (Rivals when attacking, Allies when healing)
    allOpponents: List<BattleUnit>, // All opponent units for full picker
    allAllies: List<BattleUnit>, // All ally units for heal picker
    selectedTargetIds: List<String>,
    selectedAttackType: AttackType,
    targetCountLimit: Int,
    isHealMode: Boolean,
    isTargetSelectorOpen: Boolean,
    strategyBonus: StrategyRollResult?,
    onAttackTypeChanged: (AttackType) -> Unit,
    onTargetCountLimitChanged: (Int) -> Unit,
    onHealModeChanged: (Boolean) -> Unit,
    onOpenTargetSelector: (Boolean) -> Unit,
    onToggleTargetId: (String) -> Unit,
    onOpenStrategyModal: () -> Unit,
    onExecuteAttack: () -> Unit,
    onDismiss: () -> Unit
) {
    val isMage = attacker.hero.role == HeroRole.MAGO
    val isUltimate = selectedAttackType == AttackType.ULTIMATE

    // Damage bonus calculation for display
    val ultimateBonus = if (isUltimate) (attacker.hero.attack * 0.35f).toInt().coerceAtLeast(1) else 0
    val strategyBonusValue = if (strategyBonus != null) (attacker.hero.attack * strategyBonus.bonusPct).toInt() else 0
    val totalBonus = ultimateBonus + strategyBonusValue

    // Find the actual BattleUnit objects of the selected targets
    val selectedUnits = targetUnits.filter { selectedTargetIds.contains(it.id) }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(22.dp))
                .background(DeepSlateDark)
                .border(1.5.dp, SageOlive.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // --- 1. HEADER ROW (Matches wireframe: "Ataque" ... "vida: 50 [====-] X") ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHealMode) "Curación" else "Ataque",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCreamBright,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        // Health display & Bar & Close Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "vida: ${attacker.currentHp}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = WarmCreamBright,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            )

                            // Health Bar (green progress on subtle track)
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(SageOlive.copy(alpha = 0.4f))
                            ) {
                                val hpPercent = (attacker.currentHp.toFloat() / attacker.maxHp.toFloat()).coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(hpPercent)
                                        .background(if (hpPercent > 0.35f) HealthGreen else HealthRed)
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = WarmCreamBright,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // --- 2. TABS & MAGE MODE: "Basico | Ultimate" on left, "⚔ Atacar | ✚ Curar" on right ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Tabs "Basico | Ultimate" with underline
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Basico Tab
                                Text(
                                    text = "Basico",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (!isUltimate) FontWeight.Bold else FontWeight.Normal,
                                        color = if (!isUltimate) HealthRed else WarmCreamBright,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onAttackTypeChanged(AttackType.BASIC)
                                        }
                                        .padding(end = 6.dp, top = 2.dp, bottom = 4.dp)
                                )

                                Text(
                                    text = "|",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = WarmCreamBright.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )

                                // Ultimate Tab
                                Text(
                                    text = "Ultimate",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isUltimate) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isUltimate) HealthRed else WarmCreamBright,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onAttackTypeChanged(AttackType.ULTIMATE)
                                        }
                                        .padding(start = 6.dp, top = 2.dp, bottom = 4.dp)
                                )
                            }

                            // Horizontal Underline under tabs
                            Box(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(1.5.dp)
                                    .background(WarmCreamBright.copy(alpha = 0.75f))
                            )
                        }

                        // Right: Optional Heal/Attack switch for Mago placed side-by-side with tabs
                        if (isMage) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepSlate)
                                    .border(1.dp, SageOlive.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!isHealMode) DeepSlateDark else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (!isHealMode) HealthRed.copy(alpha = 0.9f) else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onHealModeChanged(false) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "⚔ Atacar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (!isHealMode) WarmCreamBright else WarmCreamMuted.copy(alpha = 0.7f),
                                            fontWeight = if (!isHealMode) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isHealMode) DeepSlateDark else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isHealMode) HealthGreen.copy(alpha = 0.9f) else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onHealModeChanged(true) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✚ Curar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isHealMode) HealthGreen else WarmCreamMuted.copy(alpha = 0.7f),
                                            fontWeight = if (isHealMode) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- 3. SCROLLABLE BODY CONTENT (Guerrero, stats, descripciones) ---
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Hero Role Title (e.g. "Guerrero", "Mago", "Místico")
                        Text(
                            text = attacker.hero.role.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCreamBright,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(start = 12.dp)
                        )

                        // Indented stats (ataque & defensa)
                        Column(
                            modifier = Modifier.padding(start = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            val attackDisplay = if (totalBonus > 0) {
                                "ataque: ${attacker.hero.attack} + $totalBonus"
                            } else if (totalBonus < 0) {
                                "ataque: ${attacker.hero.attack} $totalBonus"
                            } else {
                                "ataque: ${attacker.hero.attack}"
                            }

                            Text(
                                text = attackDisplay,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (totalBonus > 0) HealthGreen else WarmCreamBright,
                                    fontSize = 13.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )

                            Text(
                                text = "defensa: ${attacker.hero.defense}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = WarmCreamBright,
                                    fontSize = 13.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Description line matching wireframe format
                        val descriptionText = if (isUltimate) {
                            "Descripccion ultimate: ${attacker.hero.ultimateDesc.ifBlank { "Habilidad especial devastadora del personaje" }}"
                        } else if (isHealMode) {
                            "Descripccion: restaura la salud de hasta $targetCountLimit compañero(s) herido(s) de tu escuadrón con poder de ${attacker.hero.attack} puntos."
                        } else {
                            "Descripccion: realizas un ataque de ${attacker.hero.attack} puntos a un maximo de $targetCountLimit adversarios del equipo comtrario"
                        }

                        Text(
                            text = descriptionText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WarmCreamBright.copy(alpha = 0.90f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        // Strategy line matching wireframe format and rules
                        val strategyText = if (isUltimate) {
                            "Estrategia: Deshabilitada para habilidad Ultimate (solo disponible en ataques básicos)."
                        } else if (strategyBonus != null) {
                            "Estrategia activa: ${strategyBonus.message} (${if (strategyBonus.bonusPct >= 0) "+" else ""}${(strategyBonus.bonusPct * 100).toInt()}%)"
                        } else {
                            "Estrategia: ${attacker.hero.strategyDesc.ifBlank { "adivina un número aleatorio para aumentar los stats del ataque básico" }}"
                        }

                        Text(
                            text = strategyText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isUltimate) {
                                    WarmCreamMuted.copy(alpha = 0.6f)
                                } else if (strategyBonus != null) {
                                    if (strategyBonus.isWin) HealthGreen else HealthRed
                                } else {
                                    WarmCreamBright.copy(alpha = 0.90f)
                                },
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- 4. BOTTOM ROW (Left: "Atacados" [img1] (+) --- Right: [estrategia] [atacar]) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Left: Atacados / Objetivos with circular avatars and (+)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isHealMode) "Curados" else "Atacados",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = WarmCreamBright,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Selected targets avatars
                                selectedUnits.forEach { target ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(DarkPineGreen)
                                            .border(1.5.dp, if (isHealMode) HealthGreen else AntiqueBronzeBright, CircleShape)
                                            .clickable { onToggleTargetId(target.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = target.hero.avatarId.ifBlank { "img" },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = WarmCreamBright,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }
                                }

                                // (+) Button to open target selector
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DeepSlate)
                                        .border(1.5.dp, WarmCreamBright, CircleShape)
                                        .clickable { onOpenTargetSelector(true) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar objetivo",
                                        tint = WarmCreamBright,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                if (selectedUnits.isEmpty()) {
                                    Text(
                                        text = "elija objetivo",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageOlive,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }

                        // Right: [ estrategia ] [ atacar ] Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // [ estrategia ] Button (Disabled during Ultimate, enabled on Basic attack)
                            val isStrategyEnabled = !isUltimate
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isStrategyEnabled) DeepSlate else DeepSlateDark.copy(alpha = 0.6f))
                                    .border(
                                        1.5.dp,
                                        if (!isStrategyEnabled) {
                                            SageOlive.copy(alpha = 0.25f)
                                        } else if (strategyBonus != null) {
                                            HealthGreen
                                        } else {
                                            WarmCreamBright
                                        },
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = isStrategyEnabled) { onOpenStrategyModal() }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when {
                                        !isStrategyEnabled -> "estrategia (bloqueada)"
                                        strategyBonus != null -> "estrategia ✓"
                                        else -> "estrategia"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = when {
                                            !isStrategyEnabled -> WarmCreamMuted.copy(alpha = 0.4f)
                                            strategyBonus != null -> HealthGreen
                                            else -> WarmCreamBright
                                        },
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }

                            // [ atacar ] Button (Matches wireframe rounded pill style)
                            val isActionEnabled = selectedTargetIds.isNotEmpty()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isActionEnabled) PineGreen else DeepSlateDark)
                                    .border(
                                        1.5.dp,
                                        if (isActionEnabled) (if (isHealMode) HealthGreen else AntiqueBronzeBright) else SageOlive.copy(alpha = 0.4f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = isActionEnabled) { onExecuteAttack() }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isHealMode) "curar" else "atacar",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isActionEnabled) WarmCreamBright else WarmCreamMuted.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // TARGET SELECTOR POPUP DIALOG (When clicking (+))
            if (isTargetSelectorOpen) {
                TargetPickerDialog(
                    title = if (isHealMode) "Elegir Aliados" else "Elegir Objetivos Rivales",
                    maxTargets = targetCountLimit,
                    isHealMode = isHealMode,
                    units = if (isHealMode) allAllies else allOpponents,
                    selectedTargetIds = selectedTargetIds,
                    onToggleTarget = { onToggleTargetId(it) },
                    onDismiss = { onOpenTargetSelector(false) }
                )
            }
        }
    }

@Composable
private fun TargetPickerDialog(
    title: String,
    maxTargets: Int,
    isHealMode: Boolean,
    units: List<BattleUnit>,
    selectedTargetIds: List<String>,
    onToggleTarget: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 580.dp)
                .fillMaxWidth(0.90f)
                .fillMaxHeight(0.85f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(16.dp))
                .background(PineGreen)
                .border(1.5.dp, AntiqueBronzeBright, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueBronzeBright,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Text(
                                text = "Selecciona objetivos (${selectedTargetIds.size}/$maxTargets)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SageOlive,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = WarmCream)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        units.forEach { unit ->
                            val isSelected = selectedTargetIds.contains(unit.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DarkPineGreen else DeepSlateDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) AntiqueBronzeBright else SageOlive.copy(alpha = 0.4f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onToggleTarget(unit.id) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    HeroAvatarCircle(
                                        hero = unit.hero,
                                        size = 32.dp,
                                        isSelected = isSelected,
                                        onClick = { onToggleTarget(unit.id) }
                                    )

                                    Column {
                                        Text(
                                            text = "${unit.hero.name} (P${unit.playerId})",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = WarmCream,
                                                fontSize = 11.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                        Text(
                                            text = "${unit.hero.role.displayName} · HP: ${unit.currentHp}/${unit.maxHp}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (unit.currentHp < unit.maxHp * 0.4f) HealthRed else SageOlive,
                                                fontSize = 9.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onToggleTarget(unit.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) HealthRed else AntiqueBronze,
                                        contentColor = if (isSelected) WarmCream else DarkPineGreen
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = if (isSelected) "Quitar" else "+ Agregar",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    MedievalButton(
                        text = "Listo (${selectedTargetIds.size} seleccionados)",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    )
                }
            }
        }
    }
