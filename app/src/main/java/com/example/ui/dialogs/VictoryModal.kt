package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.theme.*

@Composable
fun VictoryModal(
    winnerPlayerId: Int,
    roundCount: Int,
    onPlayAgain: () -> Unit,
    onReturnToTitle: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
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
                    .border(2.dp, AntiqueBronzeBright, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Victoria",
                        tint = AntiqueBronzeBright,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "¡VICTORIA DEL JUGADOR $winnerPlayerId!",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = WarmCream,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Has dominado el campo de batalla en $roundCount rondas tácticas.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SageOlive
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MedievalSecondaryButton(
                            text = "Menú Principal",
                            onClick = onReturnToTitle,
                            leadingIcon = Icons.Default.Home
                        )

                        MedievalButton(
                            text = "Jugar de Nuevo",
                            onClick = onPlayAgain,
                            leadingIcon = Icons.Default.Refresh
                        )
                    }
                }
            }
        }
    }
}
