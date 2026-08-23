package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            null -> HeroCatalog.ALL_HEROES.take(4)
        }
    }

    var selectedHero by remember { mutableStateOf(candidateHeroes.firstOrNull() ?: HeroCatalog.GUERREROS[0]) }

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
                .widthIn(max = 660.dp)
                .fillMaxWidth(0.90f)
                .fillMaxHeight(0.90f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(20.dp))
                .background(PineGreen)
                .border(1.5.dp, SageOlive, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Matches wireframe: "Seleccion de guerreros" + Close X)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (roleFilter) {
                            HeroRole.GUERRERO -> "Seleccion de guerreros"
                            HeroRole.MISTICO -> "Selección de místicos"
                            HeroRole.MAGO -> "Selección de magos"
                            null -> "Selección de personajes"
                        },
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = WarmCream,
                            fontSize = 17.sp
                        )
                    )

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
                HorizontalDivider(color = SageOlive.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // 2-Column Main Content (Left: 2x2 grid | Center Divider | Right: Preview & Stats)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left: 2x2 circular hero grid (Matches wireframe: img1, img2, img3, img4)
                    Column(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            candidateHeroes.getOrNull(0)?.let { h ->
                                HeroAvatarCircle(
                                    hero = h,
                                    size = 54.dp,
                                    isSelected = selectedHero.id == h.id,
                                    onClick = { selectedHero = h }
                                )
                            }
                            candidateHeroes.getOrNull(1)?.let { h ->
                                HeroAvatarCircle(
                                    hero = h,
                                    size = 54.dp,
                                    isSelected = selectedHero.id == h.id,
                                    onClick = { selectedHero = h }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            candidateHeroes.getOrNull(2)?.let { h ->
                                HeroAvatarCircle(
                                    hero = h,
                                    size = 54.dp,
                                    isSelected = selectedHero.id == h.id,
                                    onClick = { selectedHero = h }
                                )
                            }
                            candidateHeroes.getOrNull(3)?.let { h ->
                                HeroAvatarCircle(
                                    hero = h,
                                    size = 54.dp,
                                    isSelected = selectedHero.id == h.id,
                                    onClick = { selectedHero = h }
                                )
                            }
                        }
                    }

                    // Center vertical divider
                    VerticalDivider(
                        color = SageOlive.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxHeight()
                    )

                    // Right: Large Avatar Preview + Stats Box + Seleccionar CTA (Matches wireframe)
                    Column(
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top avatar preview
                        HeroAvatarCircle(
                            hero = selectedHero,
                            size = 52.dp,
                            isSelected = true
                        )

                        // Stats card box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkPineGreen)
                                .border(1.dp, SageOlive, RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "vida: ${selectedHero.hp}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCream,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Ataque: ${selectedHero.attack}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCream,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "defensa: ${selectedHero.defense}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmCream,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                if (selectedHero.basicDesc.isNotEmpty()) {
                                    Text(
                                        text = "Básico: ${selectedHero.basicDesc}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.5.sp,
                                            color = WarmCreamBright,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        maxLines = 2
                                    )
                                }

                                Text(
                                    text = "Estrategia: ${selectedHero.strategyDesc}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 9.5.sp,
                                        color = SageOlive
                                    ),
                                    maxLines = 2
                                )

                                Text(
                                    text = "Ultimate: ${selectedHero.ultimateDesc}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 9.5.sp,
                                        color = WarmCreamMuted
                                    ),
                                    maxLines = 2
                                )
                            }
                        }

                        // [Seleccionar] CTA button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            MedievalButton(
                                text = "Seleccionar",
                                onClick = { onHeroSelected(selectedHero) },
                                modifier = Modifier.height(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
