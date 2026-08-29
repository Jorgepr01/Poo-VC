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
import androidx.compose.ui.text.font.FontFamily
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateScrim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(20.dp))
                .background(PineGreen)
                .border(2.dp, AntiqueBronzeBright, RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DarkPineGreen)
                                .border(1.dp, AntiqueBronzeBright, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configuración",
                                tint = AntiqueBronzeBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Configuración de Partida",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCreamBright,
                                    fontSize = 19.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Text(
                                text = "Personaliza las reglas y parámetros de combate",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SageOlive,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepSlateDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = WarmCreamBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = SageOlive.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Settings Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. NÚMERO TOTAL DE RONDAS
                    item {
                        SettingSectionCard(
                            icon = Icons.Default.Timer,
                            title = "Número de Rondas de la Partida",
                            description = "Define la duración máxima del combate. Si se agotan las rondas, ganará el equipo con mayor porcentaje de salud total."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Límite: ${settings.maxRoundsDisplay}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueBronzeBright,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    NumberStepper(
                                        value = settings.maxRounds,
                                        minValue = 3,
                                        maxValue = 30,
                                        enabled = !settings.isUnlimitedRounds,
                                        onValueChange = { settings = settings.copy(maxRounds = it, isUnlimitedRounds = false) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick preset chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val presets = listOf(5, 10, 15, 20)
                                presets.forEach { count ->
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
                            description = "Número de rondas iniciales en las que los Guerreros vivos colocan un escudo protector a sus Místicos y Magos."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (settings.guardRounds == 0) "Sin Protección (0 rondas)" else "${settings.guardRounds} Rondas de Escudo",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueBronzeBright,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )

                                NumberStepper(
                                    value = settings.guardRounds,
                                    minValue = 0,
                                    maxValue = 6,
                                    onValueChange = { settings = settings.copy(guardRounds = it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0 to "0 (Sin Escudo)",
                                    1 to "1 Ronda",
                                    2 to "2 Rondas",
                                    3 to "3 Rondas (Est.)",
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
                            description = "Acciones máximas que cada jugador/equipo puede realizar durante su turno antes de ceder el paso."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${settings.maxAttacksPerTurn} Ataques por Turno",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueBronzeBright,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )

                                NumberStepper(
                                    value = settings.maxAttacksPerTurn,
                                    minValue = 1,
                                    maxValue = 6,
                                    onValueChange = { settings = settings.copy(maxAttacksPerTurn = it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                            title = "Puntos de Vida Iniciales (HP Multiplier)",
                            description = "Ajusta la vitalidad de todos los héroes para partidas más rápidas o más tácticas y duraderas."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0.75f to "75% (Rápida)",
                                    1.0f to "100% (Estándar)",
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
                            description = "Número de tiros o acciones previas necesarias para desbloquear y disparar la habilidad definitiva."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0 to "Inmediata (0 Tiros)",
                                    1 to "1 Tiro Previo (Estándar)",
                                    2 to "2 Tiros (Carga Pesada)"
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
                            description = "Permite a los jugadores apostar una tirada de dados (1d6 ROTA +35% o 2d6 TÁCTICA +15%) en ataques básicos una vez por ronda."
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (settings.allowStrategyMinigame) "Minijuego Habilitado (1 uso/ronda)" else "Deshabilitado (Ataque directo)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (settings.allowStrategyMinigame) HealthGreen else WarmCreamMuted,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                )

                                Switch(
                                    checked = settings.allowStrategyMinigame,
                                    onCheckedChange = { settings = settings.copy(allowStrategyMinigame = it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AntiqueBronzeBright,
                                        checkedTrackColor = DarkPineGreen,
                                        uncheckedThumbColor = SageOlive,
                                        uncheckedTrackColor = DeepSlateDark
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkPineGreen)
                                .border(1.dp, SageOlive, RoundedCornerShape(12.dp))
                                .clickable { onViewRules() }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "Manual de Reglas",
                                        tint = AntiqueBronzeBright,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Column {
                                        Text(
                                            text = "Consultar Manual de Reglas y Tácticas",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = WarmCreamBright,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "Ver detalles de roles, tipos de ataque y condiciones de victoria",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SageOlive,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Abrir",
                                    tint = AntiqueBronzeBright
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SageOlive.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons (Restablecer & Guardar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MedievalSecondaryButton(
                        text = "Restablecer por Defecto",
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
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSlateDark)
            .border(1.dp, SageOlive.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AntiqueBronzeBright,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = WarmCreamBright,
                    fontSize = 13.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = WarmCreamMuted,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            .background(if (isSelected) AntiqueBronze else DarkPineGreen)
            .border(
                1.dp,
                if (isSelected) AntiqueBronzeBright else SageOlive.copy(alpha = 0.4f),
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) DeepSlate else WarmCream,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkPineGreen)
            .border(1.dp, SageOlive, RoundedCornerShape(8.dp))
            .padding(2.dp)
    ) {
        IconButton(
            onClick = { if (value > minValue) onValueChange(value - 1) },
            enabled = enabled && value > minValue,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Disminuir",
                tint = if (enabled && value > minValue) WarmCreamBright else SageOlive.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(36.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DeepSlateDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) AntiqueBronzeBright else SageOlive,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp
                )
            )
        }

        IconButton(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            enabled = enabled && value < maxValue,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Aumentar",
                tint = if (enabled && value < maxValue) WarmCreamBright else SageOlive.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
