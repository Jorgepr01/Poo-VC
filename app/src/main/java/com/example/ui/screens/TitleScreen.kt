package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ExitToApp
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
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.theme.*

@Composable
fun TitleScreen(
    onPlayClicked: () -> Unit,
    onTeamsClicked: () -> Unit,
    onRulesClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(0.4f))

            // Main Neo-Medieval Title positioned higher above menu
            Text(
                text = "Tournaments of the Old World",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = WarmCream,
                    fontSize = 28.sp,
                    letterSpacing = 2.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.8f))

            // Main Menu Buttons (Jugar, Equipos, Salir/Reglas)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(280.dp)
            ) {
                MedievalButton(
                    text = "JUGAR",
                    onClick = onPlayClicked,
                    leadingIcon = Icons.Default.PlayArrow,
                    modifier = Modifier.fillMaxWidth()
                )

                MedievalSecondaryButton(
                    text = "EQUIPOS",
                    onClick = onTeamsClicked,
                    leadingIcon = Icons.Default.Shield,
                    modifier = Modifier.fillMaxWidth()
                )

                MedievalSecondaryButton(
                    text = "REGLAS",
                    onClick = onRulesClicked,
                    leadingIcon = Icons.Default.MenuBook,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}
