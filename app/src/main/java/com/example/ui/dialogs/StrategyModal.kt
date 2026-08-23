package com.example.ui.dialogs

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.StrategyRollResult
import com.example.ui.components.MedievalButton
import com.example.ui.theme.*

@Composable
fun StrategyModal(
    selectedNumber: Int,
    isRolling: Boolean,
    displayNumber: Int,
    isFinished: Boolean,
    outcome: StrategyRollResult?,
    onSelectNumber: (Int) -> Unit,
    onRollClicked: () -> Unit,
    onConfirmBonus: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSlateScrim)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .heightIn(max = 480.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PineGreen)
                    .border(1.5.dp, SageOlive, RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header (Matches wireframe: "Elige un numero (?)" + Close X)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Elige un número",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream,
                                    fontSize = 16.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Ayuda de estrategia",
                                tint = SageOlive,
                                modifier = Modifier.size(18.dp)
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

                    if (!isFinished && !isRolling) {
                        // State 1: Guessing number (1-6) - matches wireframe
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Elegir un numero entre 1-6",
                                style = MaterialTheme.typography.bodySmall.copy(color = SageOlive)
                            )

                            // Row of 6 clickable number buttons (1 to 6, min 48x48dp)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (num in 1..6) {
                                    val isSelected = selectedNumber == num
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) AntiqueBronze else DarkPineGreen)
                                            .border(
                                                1.dp,
                                                if (isSelected) AntiqueBronzeBright else SageOlive,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onSelectNumber(num) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$num",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) DeepSlate else WarmCream,
                                                fontSize = 18.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // [Elegir] CTA button (matches wireframe)
                            MedievalButton(
                                text = "Elegir",
                                onClick = onRollClicked,
                                leadingIcon = Icons.Default.Casino,
                                modifier = Modifier.height(44.dp)
                            )
                        }
                    } else if (isRolling) {
                        // Rolling Animation
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "$displayNumber",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 48.sp,
                                    color = AntiqueBronzeBright
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Lanzando dado estratégico...",
                                style = MaterialTheme.typography.bodySmall.copy(color = SageOlive)
                            )
                        }
                    } else {
                        // State 2: Result (Matches wireframe: Big green '3', "Perfecto! ganaste un bono")
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val win = outcome?.isWin == true
                            val resultNumber = outcome?.rolledNumber ?: displayNumber

                            Text(
                                text = "$resultNumber",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 52.sp,
                                    color = if (win) HealthGreen else HealthYellow
                                )
                            )

                            Text(
                                text = outcome?.message ?: "¡Resultado estratégico!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (win) HealthGreen else WarmCream,
                                    fontSize = 14.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            // Bonus badge tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (win) HealthGreen.copy(alpha = 0.2f) else HealthYellow.copy(alpha = 0.2f))
                                    .border(1.dp, if (win) HealthGreen else HealthYellow, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (win) "+15% Daño (1 Ronda)" else "-5% Daño menor",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (win) HealthGreen else HealthYellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            MedievalButton(
                                text = "Confirmar Bono",
                                onClick = onConfirmBonus,
                                modifier = Modifier.height(42.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
