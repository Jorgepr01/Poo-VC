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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Team
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.components.MedievalSurface
import com.example.ui.theme.*

@Composable
fun TeamManagementScreen(
    teams: List<Team>,
    onEditTeam: (Team) -> Unit,
    onDeleteTeam: (String) -> Unit,
    onCreateNewTeam: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Header (Matches wireframe: "Equipo" and (+) button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = WarmCream
                    )
                }

                Text(
                    text = "Equipo",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WarmCream,
                        fontSize = 26.sp
                    )
                )
            }

            // Top-right (+) button to create team
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DarkPineGreen)
                    .border(1.dp, SageOlive, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = AntiqueBronze),
                        onClick = onCreateNewTeam
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nuevo equipo",
                    tint = WarmCream,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Teams List (Matches wireframe: elongated cards with editar and Eliminar buttons)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(teams) { team ->
                MedievalSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Team Name and composition
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Composición: ${team.soldierCountDesc}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SageOlive,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Right: [editar] and [Eliminar] buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // [editar] button
                            OutlinedButton(
                                onClick = { onEditTeam(team) },
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 48.dp, minHeight = 36.dp)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = DarkPineGreen,
                                    contentColor = WarmCream
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SageOlive),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "editar",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = WarmCream
                                    )
                                )
                            }

                            // [Eliminar] button (disabled if default team)
                            OutlinedButton(
                                onClick = { onDeleteTeam(team.id) },
                                enabled = !team.isDefault,
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 48.dp, minHeight = 36.dp)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = DarkPineGreen,
                                    contentColor = if (team.isDefault) SageOlive.copy(alpha = 0.5f) else HealthRed,
                                    disabledContainerColor = DarkPineGreen.copy(alpha = 0.4f),
                                    disabledContentColor = SageOlive.copy(alpha = 0.4f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (team.isDefault) SageOlive.copy(alpha = 0.3f) else HealthRed.copy(alpha = 0.6f)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Eliminar",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (team.isDefault) SageOlive.copy(alpha = 0.5f) else HealthRed
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
