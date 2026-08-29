package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BattleUnit
import com.example.model.FloatingCombatText
import com.example.model.HeroRole
import com.example.model.Team
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.components.UnitTokenView
import com.example.ui.theme.*

@Composable
fun BattleScreen(
    playerCount: Int,
    currentTurnPlayer: Int,
    roundNumber: Int,
    maxRounds: Int = 10,
    isUnlimitedRounds: Boolean = false,
    attacksUsedThisTurn: Int = 0,
    maxAttacksPerTurn: Int = 3,
    battleUnits: List<BattleUnit>,
    selectedTeams: Map<Int, Team> = emptyMap(),
    selectedTargetIds: List<String>,
    floatingCombatTexts: List<FloatingCombatText>,
    onUnitClicked: (BattleUnit) -> Unit,
    onPassTurnClicked: () -> Unit,
    onAbandonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p1Units = battleUnits.filter { it.playerId == 1 }
    val p2Units = battleUnits.filter { it.playerId == 2 }
    val p3Units = if (playerCount >= 3) battleUnits.filter { it.playerId == 3 } else emptyList()
    val attacksRemaining = (maxAttacksPerTurn - attacksUsedThisTurn).coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar (Matches wireframe: "Partida" | "Turno: Jugador X · Ataques: X/3" | Actions)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title "Partida"
                Text(
                    text = "Partida",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WarmCream,
                        fontSize = 24.sp
                    )
                )

                // Turn, Round & Attacks status pill indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkPineGreen)
                        .border(1.dp, AntiqueBronze, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AntiqueBronzeBright)
                    )
                    Text(
                        text = "Turno: Jugador $currentTurnPlayer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AntiqueBronzeBright,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "· Ronda $roundNumber${if (isUnlimitedRounds) "" else "/$maxRounds"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WarmCreamMuted,
                            fontSize = 11.sp
                        )
                    )
                    
                    // Attack counter indicator (3 action pips)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PineGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Ataques:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WarmCream,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        for (i in 1..maxAttacksPerTurn) {
                            val isUsed = i <= attacksUsedThisTurn
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isUsed) AntiqueBronze else SageOlive.copy(alpha = 0.4f))
                                    .border(0.5.dp, if (isUsed) AntiqueBronzeBright else SageOlive, CircleShape)
                            )
                        }
                        Text(
                            text = "$attacksUsedThisTurn/$maxAttacksPerTurn",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AntiqueBronzeBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }

                // Action Buttons: [Pasar Turno] & [Abandonar]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MedievalSecondaryButton(
                        text = "Pasar Turno",
                        onClick = onPassTurnClicked,
                        leadingIcon = Icons.Default.SkipNext,
                        modifier = Modifier.height(38.dp)
                    )

                    MedievalSecondaryButton(
                        text = "Abandonar",
                        onClick = onAbandonClicked,
                        leadingIcon = Icons.Default.ExitToApp,
                        modifier = Modifier.height(38.dp)
                    )
                }
            }

            // Tactical Battlefield Arena (Formations)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF192226))
                    .border(1.dp, SageOlive.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                val canCurrentPlayerAct = attacksRemaining > 0

                // Player 1 Formation (Left: Triangle pointed right)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                ) {
                    PlayerFormationLeft(
                        playerIndex = 1,
                        teamName = selectedTeams[1]?.name,
                        isCurrentTurn = currentTurnPlayer == 1 && canCurrentPlayerAct,
                        units = p1Units,
                        allUnits = battleUnits,
                        selectedTargetIds = selectedTargetIds,
                        onUnitClicked = onUnitClicked
                    )
                }

                // Player 2 Formation (Right: Triangle pointed left)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                ) {
                    PlayerFormationRight(
                        playerIndex = 2,
                        teamName = selectedTeams[2]?.name,
                        isCurrentTurn = currentTurnPlayer == 2 && canCurrentPlayerAct,
                        units = p2Units,
                        allUnits = battleUnits,
                        selectedTargetIds = selectedTargetIds,
                        onUnitClicked = onUnitClicked
                    )
                }

                // Player 3 Formation (Bottom Center, 2x3 column formation, if 3-Player mode active)
                if (playerCount >= 3 && p3Units.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp)
                    ) {
                        PlayerFormationBottom(
                            playerIndex = 3,
                            teamName = selectedTeams[3]?.name,
                            isCurrentTurn = currentTurnPlayer == 3 && canCurrentPlayerAct,
                            units = p3Units,
                            allUnits = battleUnits,
                            selectedTargetIds = selectedTargetIds,
                            onUnitClicked = onUnitClicked
                        )
                    }
                }

                // Floating damage & healing texts overlay
                floatingCombatTexts.forEach { fct ->
                    val bgColor = if (fct.isHeal) HealthGreen.copy(alpha = 0.95f) 
                                  else if (fct.isCritical) HealthRed.copy(alpha = 0.95f) 
                                  else DarkPineGreen.copy(alpha = 0.95f)
                    val borderColor = if (fct.isHeal) WarmCream 
                                      else if (fct.isCritical) AntiqueBronzeBright 
                                      else SageOlive

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = fct.text,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (fct.isHeal) DarkPineGreen else WarmCream,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }

            // Bottom subtle instruction hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val guardStatus = if (roundNumber <= 3) "🛡 Vanguardia con Guardia activa (Ronda $roundNumber/3)" else "⚔ Guardia expirada (Retaguardia expuesta)"
                Text(
                    text = "Turno del Jugador $currentTurnPlayer: $attacksRemaining ataques · $guardStatus · ⚡ Ultimates desbloqueadas con 1 tiro previo",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SageOlive,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

private fun isUnitShielded(unit: BattleUnit, allUnits: List<BattleUnit>): Boolean {
    return com.example.engine.BattleRules.isUnitProtected(unit, allUnits)
}

@Composable
private fun PlayerFormationLeft(
    playerIndex: Int,
    teamName: String?,
    isCurrentTurn: Boolean,
    units: List<BattleUnit>,
    allUnits: List<BattleUnit>,
    selectedTargetIds: List<String>,
    onUnitClicked: (BattleUnit) -> Unit
) {
    val magos = units.filter { it.hero.role == HeroRole.MAGO }
    val misticos = units.filter { it.hero.role == HeroRole.MISTICO }
    val guerreros = units.filter { it.hero.role == HeroRole.GUERRERO }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Jugador $playerIndex" + if (!teamName.isNullOrBlank()) " · $teamName" else "",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isCurrentTurn) AntiqueBronzeBright else WarmCream,
                fontSize = 12.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Triangular Formation: Col 1 (Magos back) -> Col 2 (Místicos mid) -> Col 3 (Guerreros front: 1-4 units)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Magos (Back line)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(min = 44.dp)
            ) {
                if (magos.isEmpty()) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    magos.forEach { unit ->
                        UnitTokenView(
                            unit = unit,
                            isActivePlayerTurn = isCurrentTurn,
                            isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                            isProtected = isUnitShielded(unit, allUnits),
                            onClick = { onUnitClicked(unit) }
                        )
                    }
                }
            }

            // Column 2: Místicos (Mid line)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(min = 44.dp)
            ) {
                if (misticos.isEmpty()) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    misticos.forEach { unit ->
                        UnitTokenView(
                            unit = unit,
                            isActivePlayerTurn = isCurrentTurn,
                            isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                            isProtected = isUnitShielded(unit, allUnits),
                            onClick = { onUnitClicked(unit) }
                        )
                    }
                }
            }

            // Column 3: Guerreros (Front line facing enemy, 2x2 grid if 4 guerreros, or single column if <=3)
            if (guerreros.size > 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val innerCol = guerreros.take(2)
                    val frontCol = guerreros.drop(2)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        innerCol.forEach { unit ->
                            UnitTokenView(
                                unit = unit,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                                isProtected = isUnitShielded(unit, allUnits),
                                onClick = { onUnitClicked(unit) }
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        frontCol.forEach { unit ->
                            UnitTokenView(
                                unit = unit,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                                isProtected = isUnitShielded(unit, allUnits),
                                onClick = { onUnitClicked(unit) }
                            )
                        }
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.widthIn(min = 44.dp)
                ) {
                    if (guerreros.isEmpty()) {
                        Box(modifier = Modifier.size(40.dp))
                    } else {
                        guerreros.forEach { unit ->
                            UnitTokenView(
                                unit = unit,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                                isProtected = isUnitShielded(unit, allUnits),
                                onClick = { onUnitClicked(unit) }
                            )
                        }
                    }
                }
            }
        }

        // Schema Role labels underneath
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Magos", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = SageOlive), modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
            Text(text = "Místicos", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = SageOlive), modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
            Text(text = "Guerreros", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = SageOlive), modifier = Modifier.width(if (guerreros.size > 3) 92.dp else 44.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun PlayerFormationRight(
    playerIndex: Int,
    teamName: String?,
    isCurrentTurn: Boolean,
    units: List<BattleUnit>,
    allUnits: List<BattleUnit>,
    selectedTargetIds: List<String>,
    onUnitClicked: (BattleUnit) -> Unit
) {
    val magos = units.filter { it.hero.role == HeroRole.MAGO }
    val misticos = units.filter { it.hero.role == HeroRole.MISTICO }
    val guerreros = units.filter { it.hero.role == HeroRole.GUERRERO }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Jugador $playerIndex" + if (!teamName.isNullOrBlank()) " · $teamName" else "",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isCurrentTurn) AntiqueBronzeBright else WarmCream,
                fontSize = 12.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Triangular Formation Mirrored: Col 1 (Guerreros front facing center) -> Col 2 (Místicos mid) -> Col 3 (Magos back)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Guerreros (Front line facing center, 2x2 grid if 4 guerreros, or single column if <=3)
            if (guerreros.size > 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val frontCol = guerreros.take(2)
                    val innerCol = guerreros.drop(2)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        frontCol.forEach { unit ->
                            UnitTokenView(
                                unit = unit,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                                isProtected = isUnitShielded(unit, allUnits),
                                onClick = { onUnitClicked(unit) }
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        innerCol.forEach { unit ->
                            UnitTokenView(
                                unit = unit,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                                isProtected = isUnitShielded(unit, allUnits),
                                onClick = { onUnitClicked(unit) }
                            )
                        }
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.widthIn(min = 44.dp)
                ) {
                    if (guerreros.isEmpty()) {
                        Box(modifier = Modifier.size(40.dp))
                    } else {
                        guerreros.forEach { unit ->
                            UnitTokenView(
                                unit = unit,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                                isProtected = isUnitShielded(unit, allUnits),
                                onClick = { onUnitClicked(unit) }
                            )
                        }
                    }
                }
            }

            // Column 2: Místicos (Mid line)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(min = 44.dp)
            ) {
                if (misticos.isEmpty()) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    misticos.forEach { unit ->
                        UnitTokenView(
                            unit = unit,
                            isActivePlayerTurn = isCurrentTurn,
                            isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                            isProtected = isUnitShielded(unit, allUnits),
                            onClick = { onUnitClicked(unit) }
                        )
                    }
                }
            }

            // Column 3: Magos (Back line)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(min = 44.dp)
            ) {
                if (magos.isEmpty()) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    magos.forEach { unit ->
                        UnitTokenView(
                            unit = unit,
                            isActivePlayerTurn = isCurrentTurn,
                            isSelectedAsTarget = selectedTargetIds.contains(unit.id),
                            isProtected = isUnitShielded(unit, allUnits),
                            onClick = { onUnitClicked(unit) }
                        )
                    }
                }
            }
        }

        // Schema Role labels underneath
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Guerreros", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = SageOlive), modifier = Modifier.width(if (guerreros.size > 3) 92.dp else 44.dp), textAlign = TextAlign.Center)
            Text(text = "Místicos", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = SageOlive), modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
            Text(text = "Magos", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = SageOlive), modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun PlayerFormationBottom(
    playerIndex: Int,
    teamName: String?,
    isCurrentTurn: Boolean,
    units: List<BattleUnit>,
    allUnits: List<BattleUnit>,
    selectedTargetIds: List<String>,
    onUnitClicked: (BattleUnit) -> Unit
) {
    val magos = units.filter { it.hero.role == HeroRole.MAGO }
    val misticos = units.filter { it.hero.role == HeroRole.MISTICO }
    val guerreros = units.filter { it.hero.role == HeroRole.GUERRERO }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Jugador $playerIndex" + if (!teamName.isNullOrBlank()) " · $teamName" else "",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isCurrentTurn) AntiqueBronzeBright else WarmCream,
                fontSize = 11.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Dynamic formation rows for Player 3
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (guerreros.isNotEmpty()) {
                if (guerreros.size > 3) {
                    val row1 = guerreros.take(2)
                    val row2 = guerreros.drop(2)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row1.forEach { u ->
                            UnitTokenView(
                                unit = u,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(u.id),
                                isProtected = isUnitShielded(u, allUnits),
                                onClick = { onUnitClicked(u) }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row2.forEach { u ->
                            UnitTokenView(
                                unit = u,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(u.id),
                                isProtected = isUnitShielded(u, allUnits),
                                onClick = { onUnitClicked(u) }
                            )
                        }
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        guerreros.forEach { u ->
                            UnitTokenView(
                                unit = u,
                                isActivePlayerTurn = isCurrentTurn,
                                isSelectedAsTarget = selectedTargetIds.contains(u.id),
                                isProtected = isUnitShielded(u, allUnits),
                                onClick = { onUnitClicked(u) }
                            )
                        }
                    }
                }
            }

            if (misticos.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    misticos.forEach { u ->
                        UnitTokenView(
                            unit = u,
                            isActivePlayerTurn = isCurrentTurn,
                            isSelectedAsTarget = selectedTargetIds.contains(u.id),
                            isProtected = isUnitShielded(u, allUnits),
                            onClick = { onUnitClicked(u) }
                        )
                    }
                }
            }

            if (magos.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    magos.forEach { u ->
                        UnitTokenView(
                            unit = u,
                            isActivePlayerTurn = isCurrentTurn,
                            isSelectedAsTarget = selectedTargetIds.contains(u.id),
                            isProtected = isUnitShielded(u, allUnits),
                            onClick = { onUnitClicked(u) }
                        )
                    }
                }
            }
        }
    }
}
