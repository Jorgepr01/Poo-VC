package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Team
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalIconButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.components.MedievalSurface
import com.example.ui.theme.*

@Composable
fun LobbyScreen(
    playerCount: Int,
    allTeams: List<Team>,
    selectedTeams: Map<Int, Team>,
    onPlayerCountChanged: (Int) -> Unit,
    onTeamSelectedForPlayer: (Int, Team) -> Unit,
    onOpenRules: () -> Unit,
    onOpenCreateTeam: () -> Unit,
    onStartBattle: () -> Unit,
    onBackToTitle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Top Navigation & Action Bar (matches wireframe: Play | 2/3 players | Reglas | Jugar | +)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back button + Title "Play"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onBackToTitle,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = WarmCream
                    )
                }

                Text(
                    text = "PLAY",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WarmCream,
                        fontSize = 24.sp
                    )
                )

                // 2 / 3 Player selector toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkPineGreen)
                        .border(1.dp, SageOlive, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    val is2p = playerCount == 2
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (is2p) AntiqueBronze else Color.Transparent)
                            .clickable { onPlayerCountChanged(2) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "2 JUGADORES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (is2p) DeepSlate else WarmCream
                            )
                        )
                    }

                    val is3p = playerCount == 3
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (is3p) AntiqueBronze else Color.Transparent)
                            .clickable { onPlayerCountChanged(3) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "3 JUGADORES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (is3p) DeepSlate else WarmCream
                            )
                        )
                    }
                }
            }

            // Right Actions: Reglas, JUGAR, (+) Add Team
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MedievalSecondaryButton(
                    text = "Reglas",
                    onClick = onOpenRules,
                    leadingIcon = Icons.Default.MenuBook
                )

                MedievalButton(
                    text = "JUGAR",
                    onClick = onStartBattle,
                    leadingIcon = Icons.Default.PlayArrow
                )

                // Round (+) button to open create team modal
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkPineGreen)
                        .border(1.dp, SageOlive, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = AntiqueBronze),
                            onClick = onOpenCreateTeam
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear Equipo",
                        tint = WarmCream,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Columns for Players (2 or 3 equal columns)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (p in 1..playerCount) {
                PlayerTeamColumn(
                    playerIndex = p,
                    allTeams = allTeams,
                    selectedTeam = selectedTeams[p] ?: allTeams.firstOrNull(),
                    onTeamSelected = { team -> onTeamSelectedForPlayer(p, team) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PlayerTeamColumn(
    playerIndex: Int,
    allTeams: List<Team>,
    selectedTeam: Team?,
    onTeamSelected: (Team) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkPineGreen.copy(alpha = 0.5f))
            .border(1.dp, SageOlive.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        // Column Header (e.g., "Jugador 1", "Jugador 2")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Jugador $playerIndex",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = WarmCream,
                    fontSize = 14.sp
                )
            )

            if (selectedTeam != null) {
                Text(
                    text = "Seleccionado",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AntiqueBronze,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Scrollable List of 5 team cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(allTeams.take(8)) { team ->
                val isSelected = selectedTeam?.id == team.id
                MedievalSurface(
                    isSelected = isSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { onTeamSelected(team) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) WarmCream else WarmCreamMuted
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "soldados (${team.soldierCountDesc})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = if (isSelected) AntiqueBronze else SageOlive
                                ),
                                maxLines = 1
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AntiqueBronze,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
