package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import com.example.model.HeroRole
import com.example.model.Team
import com.example.ui.components.HeroAvatarCircle
import com.example.ui.components.MedievalButton
import com.example.ui.theme.*

@Composable
fun CreateTeamModal(
    teamName: String,
    guerreros: List<Hero>,
    misticos: List<Hero>,
    magos: List<Hero>,
    onNameChange: (String) -> Unit,
    onOpenHeroPicker: (HeroRole, String, Int) -> Unit,
    onRemoveHero: (String, Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val totalHeroes = guerreros.size + misticos.size + magos.size
    val isTeamFull = totalHeroes >= Team.MAX_TOTAL_HEROES
    val isValid = totalHeroes in 1..Team.MAX_TOTAL_HEROES &&
            guerreros.size <= Team.MAX_GUERREROS &&
            misticos.size <= Team.MAX_MISTICOS &&
            magos.size <= Team.MAX_MAGOS

    BackHandler(onBack = onDismiss)

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
                .widthIn(max = 680.dp)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(20.dp))
                .background(PineGreen)
                .border(1.5.dp, SageOlive, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modal Header (Title + Cap Indicator + Close X)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Crear equipo",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCream,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Total escuadrón: $totalHeroes/${Team.MAX_TOTAL_HEROES} héroes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (totalHeroes == Team.MAX_TOTAL_HEROES) HealthGreen else SageOlive,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = WarmCream
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Team Name input field
                OutlinedTextField(
                    value = teamName,
                    onValueChange = onNameChange,
                    placeholder = { Text("Nombre del equipo", color = SageOlive, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = WarmCream,
                        fontWeight = FontWeight.SemiBold
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueBronze,
                        unfocusedBorderColor = SageOlive,
                        focusedContainerColor = DarkPineGreen,
                        unfocusedContainerColor = DarkPineGreen
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable 3-section roster: Guerreros (max 4), Místicos (max 3), Magos (max 2)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Section 1: Guerreros (Max 4)
                    item {
                        TeamRoleSection(
                            title = "Guerreros (${guerreros.size}/${Team.MAX_GUERREROS})",
                            subtitle = "Vanguardia defensiva · Máx ${Team.MAX_GUERREROS}",
                            role = HeroRole.GUERRERO,
                            heroes = guerreros,
                            maxCount = Team.MAX_GUERREROS,
                            isTeamFull = isTeamFull,
                            onAdd = { onOpenHeroPicker(HeroRole.GUERRERO, "guerreros", guerreros.size) },
                            onEditSlot = { idx -> onOpenHeroPicker(HeroRole.GUERRERO, "guerreros", idx) },
                            onRemove = { idx -> onRemoveHero("guerreros", idx) }
                        )
                    }

                    // Section 2: Místicos (Max 3)
                    item {
                        TeamRoleSection(
                            title = "Místicos (${misticos.size}/${Team.MAX_MISTICOS})",
                            subtitle = "Línea media de soporte/daño · Máx ${Team.MAX_MISTICOS}",
                            role = HeroRole.MISTICO,
                            heroes = misticos,
                            maxCount = Team.MAX_MISTICOS,
                            isTeamFull = isTeamFull,
                            onAdd = { onOpenHeroPicker(HeroRole.MISTICO, "misticos", misticos.size) },
                            onEditSlot = { idx -> onOpenHeroPicker(HeroRole.MISTICO, "misticos", idx) },
                            onRemove = { idx -> onRemoveHero("misticos", idx) }
                        )
                    }

                    // Section 3: Magos (Max 2)
                    item {
                        TeamRoleSection(
                            title = "Magos (${magos.size}/${Team.MAX_MAGOS})",
                            subtitle = "Retaguardia de curación y ráfaga · Máx ${Team.MAX_MAGOS}",
                            role = HeroRole.MAGO,
                            heroes = magos,
                            maxCount = Team.MAX_MAGOS,
                            isTeamFull = isTeamFull,
                            onAdd = { onOpenHeroPicker(HeroRole.MAGO, "magos", magos.size) },
                            onEditSlot = { idx -> onOpenHeroPicker(HeroRole.MAGO, "magos", idx) },
                            onRemove = { idx -> onRemoveHero("magos", idx) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom CTA: Guardar button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (totalHeroes == 0) "Agrega al menos 1 personaje" else if (totalHeroes > Team.MAX_TOTAL_HEROES) "Excede el máximo de 6" else "Composición válida ($totalHeroes/6)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isValid) SageOlive else HealthRed,
                            fontSize = 11.sp
                        )
                    )

                    MedievalButton(
                        text = "Guardar",
                        onClick = onSave,
                        enabled = isValid,
                        modifier = Modifier.height(42.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamRoleSection(
    title: String,
    subtitle: String,
    role: HeroRole,
    heroes: List<Hero>,
    maxCount: Int,
    isTeamFull: Boolean,
    onAdd: () -> Unit,
    onEditSlot: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    val canAddMore = heroes.size < maxCount && !isTeamFull

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkPineGreen.copy(alpha = 0.5f))
            .border(1.dp, SageOlive.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        // Section Header with [+] button
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
                        color = WarmCream,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageOlive,
                        fontSize = 9.5.sp
                    )
                )
            }

            if (canAddMore) {
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar héroe",
                        tint = AntiqueBronze,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (isTeamFull && heroes.size < maxCount) {
                Text(
                    text = "Equipo Lleno",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageOlive,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hero Slot Rows (Matches wireframe: (circle) Nombre - tipo [Eliminar])
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            heroes.forEachIndexed { index, hero ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PineGreen)
                        .border(1.dp, SageOlive, RoundedCornerShape(8.dp))
                        .clickable { onEditSlot(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        HeroAvatarCircle(hero = hero, size = 32.dp)
                        Column {
                            Text(
                                text = "${hero.name} · ${hero.role.displayName}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = WarmCreamBright,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (hero.title.isNotEmpty()) {
                                Text(
                                    text = hero.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AntiqueBronzeBright,
                                        fontSize = 9.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { onRemove(index) },
                        modifier = Modifier
                            .defaultMinSize(minWidth = 48.dp, minHeight = 32.dp)
                            .height(32.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = DarkPineGreen,
                            contentColor = HealthRed
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HealthRed.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Eliminar",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HealthRed,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
