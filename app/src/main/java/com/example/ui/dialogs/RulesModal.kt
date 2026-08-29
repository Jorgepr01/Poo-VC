package com.example.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Casino
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
import com.example.ui.components.MedievalButton
import com.example.ui.theme.*

@Composable
fun RulesModal(
    onDismiss: () -> Unit
) {
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reglas y Tácticas de Batalla",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmCream,
                                fontSize = 18.sp
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
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            RuleItem(
                                title = "1. Roles de los Personajes",
                                desc = "• Guerreros: Vanguardia defensiva que absorbe impactos y activa una barrera de guardia que protege a sus aliados traseros hasta la ronda 3 (rondas 1, 2 y 3).\n• Místicos: Línea media táctica y de apoyo. La duración de sus efectos se adapta al impacto de su Ultimate: 1 ronda para quiebres explosivos de defensa (Elysia, Lyra), 2 rondas para maldiciones continuas de desgaste (Malakor, Kaelen), y 3 rondas completas para la bendición de mayor impacto (Hermana Serena).\n• Magos: Retaguardia de gran poder arcano, cuyo ataque básico cura y restaura la salud de sus compañeros heridos."
                            )
                        }

                        item {
                            RuleItem(
                                title = "2. Los 2 Tipos de Ataque y Carga de Ultimate",
                                desc = "• Ataque Básico: Acción estándar del héroe. Causa daño directo al rival (o cura en el caso del Mago). Cada disparo básico carga 1 punto de energía de Ultimate.\n• Ataque Ultimate: Habilidad especial devastadora única. Requiere haber realizado al menos 1 tiro previo para desbloquearse y desatar todo su poder."
                            )
                        }

                        item {
                            RuleItem(
                                title = "3. Minijuego de Estrategia (Adivinar Número)",
                                desc = "Antes de ejecutar un Ataque Básico, puedes pulsar 'Estrategia' para jugar un minijuego donde eliges un número del 1 al 6. Si adivinas el resultado, aumentarás los stats de daño o curación de tu ataque.\n\n⚠️ Nota Importante: La estrategia está disponible ÚNICAMENTE en Ataques Básicos; al seleccionar una Ultimate estará deshabilitada."
                            )
                        }

                        item {
                            RuleItem(
                                title = "4. Límite de 3 Ataques por Turno",
                                desc = "En cada ronda, cada jugador puede realizar un máximo de 3 ataques con personajes diferentes. Al completar los 3 ataques (o pulsar 'Pasar Turno'), el control pasará al siguiente jugador."
                            )
                        }

                        item {
                            RuleItem(
                                title = "5. Guardia de Vanguardia (Hasta Ronda 3)",
                                desc = "Durante las 3 primeras rondas (Ronda 1, 2 y 3), los Guerreros vivos proporcionan un escudo protector (🛡) a sus Místicos y Magos, impidiendo que sean seleccionados como objetivo. A partir de la ronda 4 (o si todos los guerreros caen antes), la formación queda expuesta y cualquier objetivo puede ser atacado directamente."
                            )
                        }

                        item {
                            RuleItem(
                                title = "6. Condición de Victoria",
                                desc = "El último jugador con personajes en pie sobre el campo de batalla reclama la victoria total."
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        MedievalButton(
                            text = "Entendido",
                            onClick = onDismiss,
                            modifier = Modifier.height(40.dp)
                        )
                    }
                }
            }
        }
    }

@Composable
private fun RuleItem(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkPineGreen)
            .border(1.dp, SageOlive.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AntiqueBronzeBright,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(
                color = WarmCream,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        )
    }
}
