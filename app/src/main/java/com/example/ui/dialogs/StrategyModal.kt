package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.LocalFireDepartment
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
    diceCount: Int = 2,
    onSelectNumber: (Int) -> Unit,
    onRollClicked: () -> Unit,
    onConfirmBonus: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val effectiveDiceCount = outcome?.diceCount ?: diceCount
    val isBroken = effectiveDiceCount == 1

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
                .border(1.5.dp, if (isBroken) AntiqueBronzeBright else SageOlive.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
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
                            text = if (isBroken) "Estrategia ROTA (1d6)" else "Estrategia Táctica (2d6)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isBroken) AntiqueBronzeBright else WarmCreamBright,
                                fontSize = 17.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Icon(
                            imageVector = if (isBroken) Icons.Default.LocalFireDepartment else Icons.Default.Casino,
                            contentDescription = "Dado táctico",
                            tint = if (isBroken) HealthRed else AntiqueBronze,
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

                // Badge for Broken vs Standard Strategy
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isBroken) HealthRed.copy(alpha = 0.18f) else DeepSlate)
                        .border(
                            1.dp,
                            if (isBroken) HealthRed.copy(alpha = 0.8f) else SageOlive.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBroken) "🔥 HÉROE CON ESTRATEGIA ROTA: ¡+35% DAÑO DEMOLEDOR AL ACERTAR! (1-6)"
                               else "🎲 ESTRATEGIA ESTÁNDAR: +15% DAÑO AL ACERTAR (2-12)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isBroken) WarmCreamBright else SageOlive,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                // Horizontal accent divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SageOlive.copy(alpha = 0.35f))
                )

                if (!isFinished && !isRolling) {
                    // State 1: Guessing number (1-6 for 1d6, 2-12 for 2d6)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isBroken)
                                "Elige un número del 1 al 6:"
                            else
                                "Elige un número del 2 al 12 (suma de 2 dados):",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = WarmCreamMuted,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center
                        )

                        if (isBroken) {
                            // 1d6: Row of 6 buttons (1..6)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                for (num in 1..6) {
                                    val isSelected = selectedNumber == num
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
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
                                                fontSize = 17.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            // 2d6: Two rows (2..7 and 8..12)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (num in 2..7) {
                                        val isSelected = selectedNumber == num
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) AntiqueBronze else DeepSlate)
                                                .border(
                                                    if (isSelected) 1.5.dp else 1.dp,
                                                    if (isSelected) AntiqueBronzeBright else SageOlive.copy(alpha = 0.5f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { onSelectNumber(num) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$num",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) DeepSlateDark else WarmCreamBright,
                                                    fontSize = 15.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (num in 8..12) {
                                        val isSelected = selectedNumber == num
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) AntiqueBronze else DeepSlate)
                                                .border(
                                                    if (isSelected) 1.5.dp else 1.dp,
                                                    if (isSelected) AntiqueBronzeBright else SageOlive.copy(alpha = 0.5f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { onSelectNumber(num) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$num",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) DeepSlateDark else WarmCreamBright,
                                                    fontSize = 15.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (isBroken)
                                "Tirada de 1 dado: Si coincide el número o la paridad, obtendrás +35% de Daño."
                            else
                                "Tirada de 2 dados: Si coincide la suma o la paridad, obtendrás +15% de Daño.",
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
                            text = if (isBroken) "Lanzar Dado ROTO (1d6)" else "Lanzar Dados (2d6)",
                            onClick = onRollClicked,
                            leadingIcon = Icons.Default.Casino,
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
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
                                color = if (isBroken) HealthRed else AntiqueBronzeBright,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBroken) "Lanzando dado ROTO (1d6)..." else "Lanzando dados tácticos (2d6)...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = WarmCreamMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                } else {
                    // State 2: Result (Win / Loss Animation + Direct Attack Transition)
                    val win = outcome?.isWin == true
                    val resultNumber = outcome?.rolledNumber ?: displayNumber
                    val bonusPct = outcome?.bonusPct ?: (if (win) (if (isBroken) 0.35f else 0.15f) else -0.05f)
                    val bonusText = "+${(bonusPct * 100).toInt()}%"

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // Win / Loss Banner
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (win) HealthGreen.copy(alpha = 0.18f) else HealthRed.copy(alpha = 0.18f))
                                .border(
                                    1.5.dp,
                                    if (win) HealthGreen else HealthRed.copy(alpha = 0.8f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (win) {
                                    if (isBroken) "★ ¡GANASTE! ($bonusText DAÑO ROTO) ★" else "★ ¡GANASTE! ($bonusText DAÑO) ★"
                                } else {
                                    "⚡ ¡PERDISTE! (-5% DEF) ⚡"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (win) HealthGreen else HealthRed,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Dice Result Number
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DeepSlate)
                                .border(
                                    2.dp,
                                    if (win) HealthGreen else HealthRed.copy(alpha = 0.7f),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$resultNumber",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 42.sp,
                                    color = if (win) HealthGreen else HealthYellow,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Text(
                            text = outcome?.message ?: if (win) "¡Estrategia exitosa!" else "¡Estrategia fallida!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = WarmCreamBright,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.Center
                        )

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepSlateDark)
                                .border(
                                    1.dp,
                                    if (win) HealthGreen.copy(alpha = 0.6f) else SageOlive.copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (win) {
                                    if (isBroken) "✓ Bonificación ROTA de $bonusText aplicada a este ataque" else "✓ Bonificación de $bonusText aplicada a este ataque"
                                } else {
                                    "✗ Penalización menor de -5% a la defensa"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (win) HealthGreen else WarmCreamMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        // Transition Action Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "⚔ Ejecutando ataque directamente...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AntiqueBronzeBright,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        MedievalButton(
                            text = "¡Atacar Ya!",
                            onClick = onConfirmBonus,
                            modifier = Modifier
                                .fillMaxWidth(0.70f)
                                .height(44.dp)
                        )
                    }
                }
            }
        }
    }
}
