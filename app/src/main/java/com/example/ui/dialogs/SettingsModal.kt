package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchSettings
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.theme.*

@Composable
fun SettingsModal(
    currentSettings: MatchSettings,
    onSaveSettings: (MatchSettings) -> Unit,
    onViewRules: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    var settings by remember { mutableStateOf(currentSettings) }

    // Outer scrim styled to match DeepSlate theme instead of pitch black
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate.copy(alpha = 0.82f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Modal Container matching Main Menu (TitleScreen / Lobby) Neo-Medieval aesthetic
        Box(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(16.dp))
                .background(PineGreen)
                .border(1.5.dp, AntiqueBronze, RoundedCornerShape(16.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Neo-Medieval Title Style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(DarkPineGreen)
                                .border(1.dp, AntiqueBronze, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configuración",
                                tint = AntiqueBronzeBright,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Configuración de Partida",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Ajusta las reglas y parámetros de combate para tu partida",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SageOlive,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkPineGreen)
                            .border(1.dp, SageOlive.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = WarmCream,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = SageOlive.copy(alpha = 0.35f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Settings Content with smaller, refined fonts and pine card styling
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. NÚMERO TOTAL DE RONDAS
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.Timer,
                            title = "Número de Rondas de la Partida",
                            description = "Duración máxima del combate. Al agotarse, ganará el equipo con mayor salud restante."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Límite: ${settings.maxRoundsDisplay}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueBronzeBright,
                                        fontSize = 11.5.sp
                                    )
                                )

                                NumberStepper(
                                    value = settings.maxRounds,
                                    minValue = 3,
                                    maxValue = 30,
                                    enabled = !settings.isUnlimitedRounds,
                                    onValueChange = { settings = settings.copy(maxRounds = it, isUnlimitedRounds = false) }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                listOf(5, 10, 15, 20).forEach { count ->
                                    val isSelected = !settings.isUnlimitedRounds && settings.maxRounds == count
                                    SettingPresetChip(
                                        label = "$count Rondas${if (count == 10) " (Est.)" else ""}",
                                        isSelected = isSelected,
                                        onClick = { settings = settings.copy(maxRounds = count, isUnlimitedRounds = false) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                SettingPresetChip(
                                    label = "Sin Límite",
                                    isSelected = settings.isUnlimitedRounds,
                                    onClick = { settings = settings.copy(isUnlimitedRounds = true) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // 2. RONDAS DE PROTECCIÓN DE VANGUARDIA (GUERREROS)
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.Shield,
                            title = "Rondas de Protección de Vanguardia",
                            description = "Rondas iniciales donde los Guerreros vivos otorgan escudo protector a Místicos y Magos."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (settings.guardRounds == 0) "Sin Protección (0 rondas)" else "${settings.guardRounds} Rondas de Escudo",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueBronzeBright,
                                        fontSize = 11.5.sp
                                    )
                                )

                                NumberStepper(
                                    value = settings.guardRounds,
                                    minValue = 0,
                                    maxValue = 6,
                                    onValueChange = { settings = settings.copy(guardRounds = it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                listOf(
                                    0 to "0 (Sin)",
                                    1 to "1 Ronda",
                                    2 to "2 Rondas",
                                    3 to "3 R (Est.)",
                                    4 to "4 Rondas",
                                    5 to "5 Rondas"
                                ).forEach { (rounds, label) ->
                                    val isSelected = settings.guardRounds == rounds
                                    SettingPresetChip(
                                        label = label,
                                        isSelected = isSelected,
                                        onClick = { settings = settings.copy(guardRounds = rounds) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 3. CANTIDAD DE ATAQUES POR RONDA / TURNO
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.FlashOn,
                            title = "Cantidad de Ataques por Turno",
                            description = "Acciones máximas que cada equipo puede realizar por turno antes de ceder el paso."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${settings.maxAttacksPerTurn} Ataques por Turno",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueBronzeBright,
                                        fontSize = 11.5.sp
                                    )
                                )

                                NumberStepper(
                                    value = settings.maxAttacksPerTurn,
                                    minValue = 1,
                                    maxValue = 6,
                                    onValueChange = { settings = settings.copy(maxAttacksPerTurn = it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                (1..5).forEach { atk ->
                                    val isSelected = settings.maxAttacksPerTurn == atk
                                    SettingPresetChip(
                                        label = "$atk Atk${if (atk == 3) " (Est.)" else ""}",
                                        isSelected = isSelected,
                                        onClick = { settings = settings.copy(maxAttacksPerTurn = atk) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 4. MODIFICADOR DE SALUD (HP INICIAL)
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.Favorite,
                            title = "Puntos de Vida Iniciales (Multiplicador)",
                            description = "Ajusta la vitalidad de todos los héroes para partidas rápidas o más duraderas."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                listOf(
                                    0.75f to "75% (Rápida)",
                                    1.0f to "100% (Est.)",
                                    1.25f to "125% (Épica)",
                                    1.50f to "150% (Tanques)"
                                ).forEach { (mult, label) ->
                                    val isSelected = settings.hpMultiplier == mult
                                    SettingPresetChip(
                                        label = label,
                                        isSelected = isSelected,
                                        onClick = { settings = settings.copy(hpMultiplier = mult) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 5. CARGA DE ULTIMATE
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.AutoAwesome,
                            title = "Carga Requerida para Ultimate",
                            description = "Número de tiros previos necesarios para desbloquear y disparar la habilidad definitiva."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                listOf(
                                    0 to "0 (Inmediata)",
                                    1 to "1 Tiro (Est.)",
                                    2 to "2 Tiros (Pesada)"
                                ).forEach { (shots, label) ->
                                    val isSelected = settings.ultimateRequiredShots == shots
                                    SettingPresetChip(
                                        label = label,
                                        isSelected = isSelected,
                                        onClick = { settings = settings.copy(ultimateRequiredShots = shots) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 6. MINIJUEGO DE ESTRATEGIA (1d6 ROTA / 2d6 TÁCTICA)
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.Casino,
                            title = "Minijuego de Estrategia con Dados",
                            description = "Permite a los jugadores apostar dados (1d6 ROTA +35% / 2d6 TÁCTICA +15%) en ataques básicos."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (settings.allowStrategyMinigame) "Habilitado (1 uso/ronda)" else "Deshabilitado (Ataque directo)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (settings.allowStrategyMinigame) HealthGreen else WarmCreamMuted,
                                        fontSize = 11.sp
                                    )
                                )

                                Switch(
                                    checked = settings.allowStrategyMinigame,
                                    onCheckedChange = { settings = settings.copy(allowStrategyMinigame = it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AntiqueBronzeBright,
                                        checkedTrackColor = DarkPineGreen,
                                        uncheckedThumbColor = SageOlive,
                                        uncheckedTrackColor = DeepSlate
                                    )
                                )
                            }
                        }
                    }

                    // 7. BOTÓN MANUAL DE REGLAS Y TÁCTICAS
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkPineGreen)
                                .border(1.dp, SageOlive.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .clickable { onViewRules() }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "Manual de Reglas",
                                        tint = AntiqueBronzeBright,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Column {
                                        Text(
                                            text = "Consultar Manual de Reglas y Tácticas",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = WarmCream,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                        Text(
                                            text = "Ver detalles de roles, tipos de ataque y victoria",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SageOlive,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Abrir",
                                    tint = AntiqueBronzeBright,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SageOlive.copy(alpha = 0.35f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons (Restablecer & Guardar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MedievalSecondaryButton(
                        text = "Restablecer",
                        onClick = { settings = MatchSettings.DEFAULT },
                        leadingIcon = Icons.Default.Refresh,
                        modifier = Modifier.weight(1f)
                    )

                    MedievalButton(
                        text = "Guardar y Aplicar",
                        onClick = {
                            onSaveSettings(settings)
                            onDismiss()
                        },
                        leadingIcon = Icons.Default.Check,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingSectionCard(
    icon: ImageVector,
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkPineGreen)
            .border(1.dp, SageOlive.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AntiqueBronzeBright,
                modifier = Modifier.size(15.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = WarmCream,
                    fontSize = 11.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = WarmCreamMuted,
                fontSize = 9.5.sp,
                lineHeight = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        content()
    }
}

@Composable
private fun SettingPresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) AntiqueBronze else PineGreen)
            .border(
                1.dp,
                if (isSelected) AntiqueBronzeBright else SageOlive.copy(alpha = 0.4f),
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 5.dp, horizontal = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) DeepSlate else WarmCream,
                fontSize = 9.5.sp
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun NumberStepper(
    value: Int,
    minValue: Int,
    maxValue: Int,
    enabled: Boolean = true,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(PineGreen)
            .border(1.dp, SageOlive.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(2.dp)
    ) {
        IconButton(
            onClick = { if (value > minValue) onValueChange(value - 1) },
            enabled = enabled && value > minValue,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Disminuir",
                tint = if (enabled && value > minValue) WarmCream else SageOlive.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(24.dp)
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) AntiqueBronzeBright else SageOlive,
                    fontSize = 11.5.sp
                )
            )
        }

        IconButton(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            enabled = enabled && value < maxValue,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Aumentar",
                tint = if (enabled && value < maxValue) WarmCream else SageOlive.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
