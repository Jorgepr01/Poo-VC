package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .widthIn(max = 520.dp)
                .fillMaxWidth(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click inside */ }
                .clip(RoundedCornerShape(22.dp))
                .background(DeepSlateDark)
                .border(1.5.dp, SageOlive.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header (Aligned with AttackModal style: Title + Close Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Estrategia",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCreamBright,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Dado táctico",
                            tint = AntiqueBronze,
                            modifier = Modifier.size(20.dp)
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

                // Horizontal accent divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SageOlive.copy(alpha = 0.35f))
                )

                if (!isFinished && !isRolling) {
                    // State 1: Guessing number (1-6)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Elige un número del 1 al 6 para lanzar la táctica:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = WarmCreamMuted,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center
                        )

                        // Row of 6 clickable number buttons (1 to 6)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            for (num in 1..6) {
                                val isSelected = selectedNumber == num
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) AntiqueBronze else DeepSlate)
                                        .border(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) AntiqueBronzeBright else SageOlive.copy(alpha = 0.5f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSelectNumber(num) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$num",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) DeepSlateDark else WarmCreamBright,
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Si aciertas el resultado del dado obtendrás bonificación de combate.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SageOlive,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // [Elegir] CTA button
                        MedievalButton(
                            text = "Lanzar Dado",
                            onClick = onRollClicked,
                            leadingIcon = Icons.Default.Casino,
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(44.dp)
                        )
                    }
                } else if (isRolling) {
                    // Rolling Animation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 20.dp)
                    ) {
                        Text(
                            text = "$displayNumber",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 52.sp,
                                color = AntiqueBronzeBright,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lanzando dado táctico...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = WarmCreamMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                } else {
                    // State 2: Result
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        val win = outcome?.isWin == true
                        val resultNumber = outcome?.rolledNumber ?: displayNumber

                        Text(
                            text = "$resultNumber",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 54.sp,
                                color = if (win) HealthGreen else HealthYellow,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        Text(
                            text = outcome?.message ?: "¡Resultado estratégico!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (win) HealthGreen else WarmCreamBright,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center
                        )

                        // Bonus badge tag in DeepSlate container with matching border
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepSlate)
                                .border(
                                    1.dp,
                                    if (win) HealthGreen else HealthYellow.copy(alpha = 0.7f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (win) "★ +15% Daño otorgado (1 Ronda)" else "⚡ -5% Efectividad menor",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (win) HealthGreen else HealthYellow,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        MedievalButton(
                            text = "Confirmar Bono",
                            onClick = onConfirmBonus,
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(44.dp)
                        )
                    }
                }
            }
        }
    }
}
