package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameScreen
import com.example.game.GameViewModel
import com.example.model.HeroCatalog
import com.example.ui.components.MedievalButton
import com.example.ui.components.MedievalSecondaryButton
import com.example.ui.dialogs.*
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.TeamManagementScreen
import com.example.ui.screens.TitleScreen
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        setContent {
            MyApplicationTheme {
                TacticalRpgApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun TacticalRpgApp(
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DeepSlate
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Switcher
            when (state.currentScreen) {
                GameScreen.TITLE -> {
                    TitleScreen(
                        onPlayClicked = { viewModel.navigateTo(GameScreen.LOBBY) },
                        onTeamsClicked = { viewModel.navigateTo(GameScreen.TEAM_MANAGEMENT) },
                        onRulesClicked = { viewModel.openRules(true) }
                    )
                }

                GameScreen.LOBBY -> {
                    LobbyScreen(
                        playerCount = state.playerCount,
                        allTeams = state.allTeams,
                        selectedTeams = state.selectedTeams,
                        matchSettings = state.matchSettings,
                        onPlayerCountChanged = { viewModel.setPlayerCount(it) },
                        onTeamSelectedForPlayer = { p, t -> viewModel.selectTeamForPlayer(p, t) },
                        onOpenSettings = { viewModel.openSettings(true) },
                        onOpenCreateTeam = { viewModel.openCreateTeamModal() },
                        onStartBattle = { viewModel.startBattle() },
                        onBackToTitle = { viewModel.navigateTo(GameScreen.TITLE) }
                    )
                }

                GameScreen.TEAM_MANAGEMENT -> {
                    TeamManagementScreen(
                        teams = state.allTeams,
                        onEditTeam = { viewModel.openCreateTeamModal(it) },
                        onDeleteTeam = { viewModel.deleteTeam(it) },
                        onCreateNewTeam = { viewModel.openCreateTeamModal() },
                        onBack = { viewModel.navigateTo(GameScreen.TITLE) }
                    )
                }

                GameScreen.BATTLE -> {
                    BattleScreen(
                        playerCount = state.playerCount,
                        currentTurnPlayer = state.currentTurnPlayer,
                        roundNumber = state.roundNumber,
                        maxRounds = state.matchSettings.maxRounds,
                        isUnlimitedRounds = state.matchSettings.isUnlimitedRounds,
                        attacksUsedThisTurn = state.attacksUsedThisTurn,
                        maxAttacksPerTurn = state.maxAttacksPerTurn,
                        battleUnits = state.battleUnits,
                        selectedTeams = state.selectedTeams,
                        selectedTargetIds = state.selectedTargetIds,
                        floatingCombatTexts = state.floatingCombatTexts,
                        onUnitClicked = { viewModel.onUnitClicked(it) },
                        onPassTurnClicked = { viewModel.passTurn() },
                        onAbandonClicked = { viewModel.openAbandonConfirm(true) }
                    )
                }
            }

            // --- Modals and Overlays ---

            // 0. Settings Modal
            if (state.isSettingsModalOpen) {
                SettingsModal(
                    currentSettings = state.matchSettings,
                    onSaveSettings = { viewModel.updateMatchSettings(it) },
                    onViewRules = {
                        viewModel.openSettings(false)
                        viewModel.openRules(true)
                    },
                    onDismiss = { viewModel.openSettings(false) }
                )
            }

            // 1. Rules Modal
            if (state.isRulesModalOpen) {
                RulesModal(onDismiss = { viewModel.openRules(false) })
            }

            // 2. Create / Edit Team Modal
            if (state.isCreateTeamModalOpen) {
                CreateTeamModal(
                    teamName = state.editingTeamName,
                    guerreros = state.editingGuerreros,
                    misticos = state.editingMisticos,
                    magos = state.editingMagos,
                    onNameChange = { viewModel.updateEditingTeamName(it) },
                    onOpenHeroPicker = { role, listType, slot -> viewModel.openHeroPicker(role, listType, slot) },
                    onRemoveHero = { listType, slot -> viewModel.removeHeroFromSlot(listType, slot) },
                    onSave = { viewModel.saveEditingTeam() },
                    onDismiss = { viewModel.closeCreateTeamModal() }
                )
            }

            // 3. Hero Picker Modal
            if (state.isHeroPickerModalOpen) {
                HeroPickerModal(
                    roleFilter = state.heroPickerRoleFilter,
                    onHeroSelected = { hero -> viewModel.selectHeroForSlot(hero) },
                    onDismiss = { viewModel.closeHeroPicker() }
                )
            }

            // 4. Combat Attack Modal
            if (state.isAttackModalOpen && state.selectedAttackerId != null) {
                val attacker = state.battleUnits.find { it.id == state.selectedAttackerId }
                val targetableOpponents = state.battleUnits.filter { 
                    it.playerId != state.currentTurnPlayer && viewModel.isUnitTargetable(it, state.battleUnits) 
                }
                val livingAllies = state.battleUnits.filter {
                    it.playerId == state.currentTurnPlayer && it.isAlive
                }

                if (attacker != null) {
                    val playerTeamNames = state.selectedTeams.mapValues { it.value.name }
                    AttackModal(
                        attacker = attacker,
                        targetUnits = if (state.isHealMode) livingAllies else targetableOpponents,
                        allOpponents = targetableOpponents,
                        allAllies = livingAllies,
                        selectedTargetIds = state.selectedTargetIds,
                        selectedAttackType = state.selectedAttackType,
                        targetCountLimit = state.targetCountLimit,
                        isHealMode = state.isHealMode,
                        isTargetSelectorOpen = state.isTargetSelectorOpen,
                        strategyBonus = state.activeStrategyBonus,
                        isStrategyUsedThisRound = state.strategyUsedByPlayersThisRound.contains(state.currentTurnPlayer),
                        isStrategyMinigameAllowed = state.matchSettings.allowStrategyMinigame,
                        playerTeamNames = playerTeamNames,
                        onAttackTypeChanged = { viewModel.setAttackType(it) },
                        onTargetCountLimitChanged = { viewModel.setTargetCountLimit(it) },
                        onHealModeChanged = { viewModel.setIsHealMode(it) },
                        onOpenTargetSelector = { viewModel.openTargetSelector(it) },
                        onToggleTargetId = { viewModel.toggleTargetSelection(it) },
                        onOpenStrategyModal = { viewModel.openStrategyMinigame() },
                        onExecuteAttack = { viewModel.executeAttack() },
                        onDismiss = { viewModel.closeAttackModal() }
                    )
                }
            }

            // 5. Strategy Minigame Modal
            if (state.isStrategyModalOpen) {
                StrategyModal(
                    selectedNumber = state.strategySelectedNumber,
                    isRolling = state.isStrategyRolling,
                    displayNumber = state.strategyDisplayNumber,
                    isFinished = state.strategyRollFinished,
                    outcome = state.strategyOutcome,
                    diceCount = state.strategyDiceCount,
                    onSelectNumber = { viewModel.selectStrategyNumber(it) },
                    onRollClicked = { viewModel.executeStrategyRoll() },
                    onConfirmBonus = { viewModel.confirmStrategyBonus() },
                    onDismiss = { viewModel.closeAttackModal() }
                )
            }

            // 6. Victory Modal
            if (state.isVictoryModalOpen && state.winnerPlayerId != null) {
                VictoryModal(
                    winnerPlayerId = state.winnerPlayerId!!,
                    roundCount = state.roundNumber,
                    onPlayAgain = { viewModel.startBattle() },
                    onReturnToTitle = { viewModel.navigateTo(GameScreen.TITLE) }
                )
            }

            // 7. Abandon Match Confirmation
            if (state.isAbandonConfirmOpen) {
                androidx.activity.compose.BackHandler(onBack = { viewModel.openAbandonConfirm(false) })

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepSlateScrim)
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { viewModel.openAbandonConfirm(false) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 460.dp)
                            .fillMaxWidth(0.85f)
                            .heightIn(max = 400.dp)
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null
                            ) { /* Consume click inside */ }
                            .clip(RoundedCornerShape(18.dp))
                            .background(PineGreen)
                            .border(1.5.dp, SageOlive, RoundedCornerShape(18.dp))
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "¿Abandonar la Partida?",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream,
                                    fontSize = 18.sp
                                )
                            )

                            Text(
                                text = "El progreso actual de la batalla se perderá y regresarás al menú.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SageOlive
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MedievalSecondaryButton(
                                    text = "Cancelar",
                                    onClick = { viewModel.openAbandonConfirm(false) },
                                    modifier = Modifier.weight(1f)
                                )

                                MedievalButton(
                                    text = "Salir",
                                    onClick = {
                                        viewModel.openAbandonConfirm(false)
                                        viewModel.navigateTo(GameScreen.LOBBY)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
