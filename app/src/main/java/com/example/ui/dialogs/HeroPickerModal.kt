package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import com.example.model.HeroCatalog
import com.example.model.HeroRole
import com.example.ui.components.HeroAvatarCircle
import com.example.ui.components.MedievalButton
import com.example.ui.theme.*

@Composable
fun HeroPickerModal(
    roleFilter: HeroRole?,
    onHeroSelected: (Hero) -> Unit,
    onDismiss: () -> Unit
) {
    val candidateHeroes = remember(roleFilter) {
        when (roleFilter) {
            HeroRole.GUERRERO -> HeroCatalog.GUERREROS
            HeroRole.MISTICO -> HeroCatalog.MISTICOS
            HeroRole.MAGO -> HeroCatalog.MAGOS
            null -> HeroCatalog.ALL_HEROES
        }
    }

    var selectedHero by remember { mutableStateOf(candidateHeroes.firstOrNull() ?: HeroCatalog.GUERREROS[0]) }

    BackHandler(onBack = onDismiss)

    val roleColor = when (selectedHero.role) {
        HeroRole.GUERRERO -> AntiqueBronze
        HeroRole.MISTICO -> MysticMana
        HeroRole.MAGO -> MagicPurple
    }

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
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(22.dp))
                .background(DeepSlateDark)
                .border(1.5.dp, SageOlive, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Neo-medieval styled)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val headerIcon = when (roleFilter) {
                            HeroRole.GUERRERO -> Icons.Default.Shield
                            HeroRole.MISTICO -> Icons.Default.AutoAwesome
                            HeroRole.MAGO -> Icons.Default.LocalFireDepartment
                            null -> Icons.Default.Star
                        }
                        Icon(
                            imageVector = headerIcon,
                            contentDescription = null,
                            tint = AntiqueBronze,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = when (roleFilter) {
                                HeroRole.GUERRERO -> "CÓDICE DE GUERREROS"
                                HeroRole.MISTICO -> "CÓDICE DE MÍSTICOS"
                                HeroRole.MAGO -> "CÓDICE DE MAGOS"
                                null -> "CÓDICE DE HÉROES"
                            },
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCreamBright,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = SageOlive
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = SageOlive.copy(alpha = 0.35f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // 2-Column Main Content (Left: Candidates Selector | Center Divider | Right: Codex Sheet)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column: Hero Selection Grid
                    Column(
                        modifier = Modifier
                            .weight(0.38f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ASPIRANTES DISPONIBLES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SageOlive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(candidateHeroes) { h ->
                                val isHSelected = selectedHero.id == h.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isHSelected) DeepSlate else DeepSlateDark.copy(alpha = 0.6f)
                                        )
                                        .border(
                                            width = if (isHSelected) 1.5.dp else 1.dp,
                                            color = if (isHSelected) AntiqueBronze else SageOlive.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedHero = h }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        HeroAvatarCircle(
                                            hero = h,
                                            size = 46.dp,
                                            isSelected = isHSelected
                                        )
                                        Text(
                                            text = h.name.split(" ").firstOrNull() ?: h.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isHSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isHSelected) AntiqueBronzeBright else WarmCreamMuted
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Vertical Divider
                    VerticalDivider(
                        color = SageOlive.copy(alpha = 0.35f),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxHeight()
                    )

                    // Right Column: Detailed Codex Card & Combat Effects
                    Column(
                        modifier = Modifier
                            .weight(0.62f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Scrollable Hero Detail Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top Profile Banner
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HeroAvatarCircle(
                                    hero = selectedHero,
                                    size = 54.dp,
                                    isSelected = true
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedHero.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = WarmCreamBright,
                                            fontSize = 16.sp
                                        )
                                    )
                                    if (selectedHero.title.isNotEmpty()) {
                                        Text(
                                            text = selectedHero.title,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = AntiqueBronzeBright,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Rol: ${selectedHero.roleName}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = roleColor,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Motto Banner (Lema de Batalla)
                            if (selectedHero.motto.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    AntiqueBronze.copy(alpha = 0.18f),
                                                    DeepSlateDark
                                                )
                                            )
                                        )
                                        .border(1.dp, AntiqueBronze.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = selectedHero.motto,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCream,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            // Stats Grid Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepSlate)
                                    .border(1.dp, SageOlive.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(vertical = 6.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("VIDA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SageOlive, fontWeight = FontWeight.Bold))
                                    Text("${selectedHero.hp}", style = MaterialTheme.typography.bodyMedium.copy(color = HealthGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp))
                                }
                                VerticalDivider(color = SageOlive.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.height(20.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("ATAQUE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SageOlive, fontWeight = FontWeight.Bold))
                                    Text("${selectedHero.attack}", style = MaterialTheme.typography.bodyMedium.copy(color = HealthRed, fontWeight = FontWeight.Bold, fontSize = 13.sp))
                                }
                                VerticalDivider(color = SageOlive.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.height(20.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("DEFENSA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SageOlive, fontWeight = FontWeight.Bold))
                                    Text("${selectedHero.defense}", style = MaterialTheme.typography.bodyMedium.copy(color = AntiqueBronzeBright, fontWeight = FontWeight.Bold, fontSize = 13.sp))
                                }
                            }

                            // SECCIÓN 1: ¿QUÉ HACE LA ULTIMATE (HABILIDAD DEFINITIVA)?
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepSlate.copy(alpha = 0.7f))
                                    .border(1.dp, HealthRed.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(9.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = HealthRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "EFECTO ULTIMATE: ${selectedHero.abilityName.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = HealthRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = selectedHero.abilityDescription,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCreamBright,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = "• Escala a un 135% de potencia de daño/curación sin verse afectado por penalizaciones de tirada.",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WarmCreamMuted,
                                            fontSize = 9.5.sp
                                        )
                                    )
                                }
                            }

                            // SECCIÓN 2: ¿QUÉ HACE EL USAR ESTRATEGIA?
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepSlate.copy(alpha = 0.65f))
                                    .border(1.dp, AntiqueBronze.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(9.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Casino,
                                            contentDescription = null,
                                            tint = AntiqueBronzeBright,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "¿QUÉ HACE USAR ESTRATEGIA?",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = AntiqueBronzeBright,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Al activar Estrategia, seleccionas un número táctico del 1 al 6 antes de tu turno:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCreamBright,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                    Text(
                                        text = "✦ Acierto (mismo número o misma paridad par/impar): Otorga +15% de Daño de Ataque a tus ataques básicos.\n✦ Fallo: Aplica una penalización menor de -5% a la Defensa durante esa ronda.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCream,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            // SECCIÓN 3: VENTAJAS PARA ELEGIR A ESTE HÉROE
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkPineGreen.copy(alpha = 0.65f))
                                    .border(1.dp, SageOlive, RoundedCornerShape(8.dp))
                                    .padding(9.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = AntiqueBronzeBright,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "¿POR QUÉ ELEGIR A ESTE HÉROE?",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = AntiqueBronzeBright,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = selectedHero.tacticalReason,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCream,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Bottom Actions: Seleccionar CTA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MedievalButton(
                                text = "Reclutar a ${selectedHero.name.split(" ").firstOrNull() ?: selectedHero.name}",
                                onClick = { onHeroSelected(selectedHero) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
