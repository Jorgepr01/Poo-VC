package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.MedievalButton
import com.example.ui.theme.*

@Composable
fun RulesModal(
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
                .padding(horizontal = 24.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .fillMaxHeight(0.88f)
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
                                desc = "• Guerreros: Vanguardia defensiva que absorbe impactos y protege a sus aliados traseros.\n• Místicos: Línea media de soporte táctico y daño equilibrado.\n• Magos: Retaguardia de gran poder arcano, cuyo ataque básico cura y restaura salud a sus compañeros."
                            )
                        }

                        item {
                            RuleItem(
                                title = "2. Los 2 Tipos de Ataque",
                                desc = "• Ataque Básico: Acción directa del héroe. Causa daño directo al rival o, en el caso del Mago, cura la salud de sus aliados.\n• Ataque Ultimate: Habilidad especial única de cada personaje con gran potencia destructiva, escudos o efectos avanzados."
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
                                title = "5. Protección de Vanguardia",
                                desc = "Mientras un equipo tenga Guerreros vivos en el frente, sus Místicos y Magos estarán protegidos (🛡) y no podrán ser seleccionados como objetivo por los rivales."
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
