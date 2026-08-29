package com.example.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.BattleRules
import com.example.engine.CombatCalculator
import com.example.engine.StrategyEngine
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GameScreen {
    TITLE,
    LOBBY,
    TEAM_MANAGEMENT,
    BATTLE
}

data class GameUiState(
    val currentScreen: GameScreen = GameScreen.TITLE,
    val playerCount: Int = 2,
    val allTeams: List<Team> = DefaultTeams.createDefaults(),
    val selectedTeams: Map<Int, Team> = mapOf(
        1 to DefaultTeams.createDefaults()[0],
        2 to DefaultTeams.createDefaults()[1],
        3 to DefaultTeams.createDefaults()[2]
    ),
    // Battle state
    val battleUnits: List<BattleUnit> = emptyList(),
    val currentTurnPlayer: Int = 1,
    val roundNumber: Int = 1,
    val attacksUsedThisTurn: Int = 0,
    val maxAttacksPerTurn: Int = BattleRules.MAX_ATTACKS_PER_TURN,
    val winnerPlayerId: Int? = null,
    val matchIsOver: Boolean = false,
    
    // Match Configuration Settings
    val matchSettings: MatchSettings = MatchSettings.DEFAULT,
    val isSettingsModalOpen: Boolean = false,

    // Modal states
    val isRulesModalOpen: Boolean = false,
    val isCreateTeamModalOpen: Boolean = false,
    val isHeroPickerModalOpen: Boolean = false,
    val editingTeamId: String? = null,
    val editingTeamName: String = "Mi Equipo Personal",
    val editingGuerreros: List<Hero> = listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[3]),
    val editingMisticos: List<Hero> = listOf(HeroCatalog.MISTICOS[0], HeroCatalog.MISTICOS[1]),
    val editingMagos: List<Hero> = listOf(HeroCatalog.MAGOS[0]),
    val heroPickerRoleFilter: HeroRole? = null,
    val heroPickerSlotIndex: Int = 0,
    val heroPickerTargetList: String = "guerreros", // "guerreros", "misticos", "magos"

    // Combat Dialog
    val isAttackModalOpen: Boolean = false,
    val selectedAttackerId: String? = null,
    val selectedTargetIds: List<String> = emptyList(),
    val selectedAttackType: AttackType = AttackType.BASIC,
    val activeStrategyBonus: StrategyRollResult? = null,
    val targetCountLimit: Int = 1, // 1, 2, or 3 targets property
    val isHealMode: Boolean = false, // Magos / Místicos can heal allies
    val isTargetSelectorOpen: Boolean = false,
    
    // Strategy Minigame & Round limits (1 per player per round)
    val strategyUsedByPlayersThisRound: Set<Int> = emptySet(),
    val isStrategyModalOpen: Boolean = false,
    val strategyDiceCount: Int = 2,
    val strategySelectedNumber: Int = 7,
    val isStrategyRolling: Boolean = false,
    val strategyDisplayNumber: Int = 7,
    val strategyRollFinished: Boolean = false,
    val strategyOutcome: StrategyRollResult? = null,

    // Dialogs
    val isAbandonConfirmOpen: Boolean = false,
    val isVictoryModalOpen: Boolean = false,
    
    // Floating damage texts & notifications
    val floatingCombatTexts: List<FloatingCombatText> = emptyList(),
    val latestCombatNotification: String? = null
) {
    val attacksRemaining: Int
        get() = (maxAttacksPerTurn - attacksUsedThisTurn).coerceAtLeast(0)
}

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun navigateTo(screen: GameScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setPlayerCount(count: Int) {
        if (count == 2 || count == 3) {
            _uiState.update { it.copy(playerCount = count) }
        }
    }

    fun selectTeamForPlayer(playerId: Int, team: Team) {
        _uiState.update { state ->
            val updated = state.selectedTeams.toMutableMap()
            updated[playerId] = team
            state.copy(selectedTeams = updated)
        }
    }

    fun openRules(open: Boolean) {
        _uiState.update { it.copy(isRulesModalOpen = open) }
    }

    fun openSettings(open: Boolean) {
        _uiState.update { it.copy(isSettingsModalOpen = open) }
    }

    fun updateMatchSettings(settings: MatchSettings) {
        _uiState.update { it.copy(matchSettings = settings) }
    }

    fun openAbandonConfirm(open: Boolean) {
        _uiState.update { it.copy(isAbandonConfirmOpen = open) }
    }

    // --- Team Management & Hero Picker ---
    fun openCreateTeamModal(teamToEdit: Team? = null) {
        if (teamToEdit != null) {
            _uiState.update {
                it.copy(
                    isCreateTeamModalOpen = true,
                    editingTeamId = teamToEdit.id,
                    editingTeamName = teamToEdit.name,
                    editingGuerreros = teamToEdit.guerreros,
                    editingMisticos = teamToEdit.misticos,
                    editingMagos = teamToEdit.magos
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isCreateTeamModalOpen = true,
                    editingTeamId = null,
                    editingTeamName = "Equipo Táctico ${_uiState.value.allTeams.size + 1}",
                    editingGuerreros = listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[3]),
                    editingMisticos = listOf(HeroCatalog.MISTICOS[0], HeroCatalog.MISTICOS[1]),
                    editingMagos = listOf(HeroCatalog.MAGOS[0])
                )
            }
        }
    }

    fun closeCreateTeamModal() {
        _uiState.update { it.copy(isCreateTeamModalOpen = false, editingTeamId = null) }
    }

    fun updateEditingTeamName(name: String) {
        _uiState.update { it.copy(editingTeamName = name) }
    }

    fun openHeroPicker(role: HeroRole, listType: String, slotIndex: Int) {
        _uiState.update {
            it.copy(
                isHeroPickerModalOpen = true,
                heroPickerRoleFilter = role,
                heroPickerTargetList = listType,
                heroPickerSlotIndex = slotIndex
            )
        }
    }

    fun closeHeroPicker() {
        _uiState.update { it.copy(isHeroPickerModalOpen = false) }
    }

    fun selectHeroForSlot(hero: Hero) {
        _uiState.update { state ->
            val slot = state.heroPickerSlotIndex
            val totalCurrent = state.editingGuerreros.size + state.editingMisticos.size + state.editingMagos.size

            when (state.heroPickerTargetList) {
                "guerreros" -> {
                    val list = state.editingGuerreros.toMutableList()
                    if (slot < list.size) {
                        list[slot] = hero
                        state.copy(editingGuerreros = list, isHeroPickerModalOpen = false)
                    } else if (list.size < Team.MAX_GUERREROS && totalCurrent < Team.MAX_TOTAL_HEROES) {
                        list.add(hero)
                        state.copy(editingGuerreros = list, isHeroPickerModalOpen = false)
                    } else {
                        state.copy(isHeroPickerModalOpen = false)
                    }
                }
                "misticos" -> {
                    val list = state.editingMisticos.toMutableList()
                    if (slot < list.size) {
                        list[slot] = hero
                        state.copy(editingMisticos = list, isHeroPickerModalOpen = false)
                    } else if (list.size < Team.MAX_MISTICOS && totalCurrent < Team.MAX_TOTAL_HEROES) {
                        list.add(hero)
                        state.copy(editingMisticos = list, isHeroPickerModalOpen = false)
                    } else {
                        state.copy(isHeroPickerModalOpen = false)
                    }
                }
                "magos" -> {
                    val list = state.editingMagos.toMutableList()
                    if (slot < list.size) {
                        list[slot] = hero
                        state.copy(editingMagos = list, isHeroPickerModalOpen = false)
                    } else if (list.size < Team.MAX_MAGOS && totalCurrent < Team.MAX_TOTAL_HEROES) {
                        list.add(hero)
                        state.copy(editingMagos = list, isHeroPickerModalOpen = false)
                    } else {
                        state.copy(isHeroPickerModalOpen = false)
                    }
                }
                else -> state.copy(isHeroPickerModalOpen = false)
            }
        }
    }

    fun removeHeroFromSlot(listType: String, index: Int) {
        _uiState.update { state ->
            val totalCurrent = state.editingGuerreros.size + state.editingMisticos.size + state.editingMagos.size
            if (totalCurrent <= 1) {
                // Keep at least 1 hero in squad
                return@update state
            }
            when (listType) {
                "guerreros" -> {
                    val list = state.editingGuerreros.toMutableList()
                    if (index < list.size) list.removeAt(index)
                    state.copy(editingGuerreros = list)
                }
                "misticos" -> {
                    val list = state.editingMisticos.toMutableList()
                    if (index < list.size) list.removeAt(index)
                    state.copy(editingMisticos = list)
                }
                "magos" -> {
                    val list = state.editingMagos.toMutableList()
                    if (index < list.size) list.removeAt(index)
                    state.copy(editingMagos = list)
                }
                else -> state
            }
        }
    }

    fun saveEditingTeam() {
        val state = _uiState.value
        val name = state.editingTeamName.trim().ifEmpty { "Equipo Personal" }
        val id = state.editingTeamId ?: "team_${System.currentTimeMillis()}"
        val newTeam = Team(
            id,
            name,
            false,
            state.editingGuerreros,
            state.editingMisticos,
            state.editingMagos
        )

        if (!newTeam.isValidComposition) {
            return
        }

        val updatedList = state.allTeams.toMutableList()
        val index = updatedList.indexOfFirst { it.id == id }
        if (index >= 0) {
            updatedList[index] = newTeam
        } else {
            updatedList.add(newTeam)
        }

        val updatedSelected = state.selectedTeams.toMutableMap()
        for ((p, t) in state.selectedTeams) {
            if (t.id == id) {
                updatedSelected[p] = newTeam
            }
        }

        _uiState.update {
            it.copy(
                allTeams = updatedList,
                selectedTeams = updatedSelected,
                isCreateTeamModalOpen = false,
                editingTeamId = null
            )
        }
    }

    fun deleteTeam(teamId: String) {
        _uiState.update { state ->
            val updated = state.allTeams.filter { it.id != teamId || it.isDefault }
            state.copy(allTeams = updated)
        }
    }

    // --- Battle Setup & Execution ---
    fun startBattle() {
        val state = _uiState.value
        val count = state.playerCount
        val settings = state.matchSettings
        val units = mutableListOf<BattleUnit>()

        val team1 = state.selectedTeams[1] ?: state.allTeams[0]
        units.addAll(createFormationUnits(playerId = 1, team = team1, guardRounds = settings.guardRounds, hpMultiplier = settings.hpMultiplier))

        val team2 = state.selectedTeams[2] ?: state.allTeams[1 % state.allTeams.size]
        units.addAll(createFormationUnits(playerId = 2, team = team2, guardRounds = settings.guardRounds, hpMultiplier = settings.hpMultiplier))

        if (count >= 3) {
            val team3 = state.selectedTeams[3] ?: state.allTeams[2 % state.allTeams.size]
            units.addAll(createFormationUnits(playerId = 3, team = team3, guardRounds = settings.guardRounds, hpMultiplier = settings.hpMultiplier))
        }

        _uiState.update {
            it.copy(
                currentScreen = GameScreen.BATTLE,
                battleUnits = units,
                currentTurnPlayer = 1,
                roundNumber = 1,
                attacksUsedThisTurn = 0,
                strategyUsedByPlayersThisRound = emptySet(),
                maxAttacksPerTurn = settings.maxAttacksPerTurn,
                winnerPlayerId = null,
                matchIsOver = false,
                isAttackModalOpen = false,
                isStrategyModalOpen = false,
                isVictoryModalOpen = false,
                activeStrategyBonus = null,
                floatingCombatTexts = emptyList()
            )
        }
    }

    private fun createFormationUnits(
        playerId: Int,
        team: Team,
        guardRounds: Int = 3,
        hpMultiplier: Float = 1.0f
    ): List<BattleUnit> {
        val list = mutableListOf<BattleUnit>()
        var unitIndex = 0

        team.guerreros.forEach { hero ->
            val scaledHp = (hero.hp * hpMultiplier).toInt().coerceAtLeast(10)
            list.add(
                BattleUnit(
                    "p${playerId}_u${unitIndex++}",
                    playerId,
                    list.size,
                    hero,
                    scaledHp,
                    scaledHp,
                    true,
                    false,
                    0f,
                    0f,
                    "Guerreros",
                    0,
                    guardRounds
                )
            )
        }
        team.misticos.forEach { hero ->
            val scaledHp = (hero.hp * hpMultiplier).toInt().coerceAtLeast(10)
            list.add(
                BattleUnit(
                    "p${playerId}_u${unitIndex++}",
                    playerId,
                    list.size,
                    hero,
                    scaledHp,
                    scaledHp,
                    true,
                    false,
                    0f,
                    0f,
                    "Místicos",
                    0,
                    0
                )
            )
        }
        team.magos.forEach { hero ->
            val scaledHp = (hero.hp * hpMultiplier).toInt().coerceAtLeast(10)
            list.add(
                BattleUnit(
                    "p${playerId}_u${unitIndex++}",
                    playerId,
                    list.size,
                    hero,
                    scaledHp,
                    scaledHp,
                    true,
                    false,
                    0f,
                    0f,
                    "Magos",
                    0,
                    0
                )
            )
        }

        return list
    }

    // --- Frontline & Targeting Rules (delegated to Java engine) ---
    fun isUnitTargetable(targetUnit: BattleUnit, allUnits: List<BattleUnit>): Boolean {
        return BattleRules.isUnitTargetable(targetUnit, allUnits)
    }

    fun isUnitProtected(unit: BattleUnit, allUnits: List<BattleUnit>): Boolean {
        return BattleRules.isUnitProtected(unit, allUnits)
    }

    fun showFloatingNotice(text: String) {
        val newId = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                floatingCombatTexts = listOf(
                    FloatingCombatText(newId, "", text, false, false)
                )
            )
        }
        viewModelScope.launch {
            delay(1800)
            _uiState.update { current ->
                current.copy(floatingCombatTexts = current.floatingCombatTexts.filter { it.id != newId })
            }
        }
    }

    fun onUnitClicked(unit: BattleUnit) {
        val state = _uiState.value
        if (state.matchIsOver) return

        if (unit.playerId == state.currentTurnPlayer && unit.isAlive && !unit.hasActed && state.attacksUsedThisTurn < state.maxAttacksPerTurn) {
            openAttackModal(unit)
        } else if (state.isAttackModalOpen && unit.playerId != state.currentTurnPlayer && unit.isAlive) {
            if (isUnitProtected(unit, state.battleUnits)) {
                showFloatingNotice("¡Protegido por Guerreros vivos!")
            } else {
                toggleTargetSelection(unit.id)
            }
        }
    }

    fun passTurn() {
        val state = _uiState.value
        if (state.matchIsOver) return
        closeAttackModal()
        advancePlayerTurn()
    }

    fun openAttackModal(attacker: BattleUnit) {
        val state = _uiState.value
        val isMage = attacker.hero.role == HeroRole.MAGO

        val defaultTargets = if (isMage) {
            val livingAllies = BattleRules.getLivingPlayerUnits(state.battleUnits, state.currentTurnPlayer)
            val mostWounded = livingAllies.minByOrNull { it.hpPercent }
            mostWounded?.id?.let { listOf(it) } ?: emptyList()
        } else {
            val opponentTargetableUnits = state.battleUnits.filter { 
                it.playerId != state.currentTurnPlayer && isUnitTargetable(it, state.battleUnits) 
            }
            opponentTargetableUnits.firstOrNull()?.id?.let { listOf(it) } ?: emptyList()
        }

        _uiState.update {
            it.copy(
                isAttackModalOpen = true,
                selectedAttackerId = attacker.id,
                selectedTargetIds = defaultTargets,
                selectedAttackType = AttackType.BASIC,
                targetCountLimit = 1,
                isHealMode = isMage,
                isTargetSelectorOpen = false,
                activeStrategyBonus = null,
                isStrategyModalOpen = false
            )
        }
    }

    fun closeAttackModal() {
        _uiState.update {
            it.copy(
                isAttackModalOpen = false,
                selectedAttackerId = null,
                selectedTargetIds = emptyList(),
                targetCountLimit = 1,
                isHealMode = false,
                isTargetSelectorOpen = false,
                activeStrategyBonus = null,
                isStrategyModalOpen = false
            )
        }
    }

    fun setAttackType(type: AttackType) {
        val state = _uiState.value
        val attacker = state.battleUnits.find { it.id == state.selectedAttackerId }
        val requiredShots = state.matchSettings.ultimateRequiredShots
        if (type == AttackType.ULTIMATE) {
            if (attacker != null && attacker.shotsPerformed < requiredShots) {
                showFloatingNotice("¡Requiere al menos $requiredShots ${if (requiredShots == 1) "tiro previo" else "tiros previos"} para desbloquear la Ultimate!")
                return
            }
            _uiState.update {
                it.copy(
                    selectedAttackType = type,
                    activeStrategyBonus = null,
                    isStrategyModalOpen = false
                )
            }
        } else {
            _uiState.update { it.copy(selectedAttackType = type) }
        }
    }

    fun setTargetCountLimit(count: Int) {
        val safeCount = count.coerceIn(1, 3)
        _uiState.update { state ->
            val trimmedTargets = state.selectedTargetIds.take(safeCount)
            state.copy(
                targetCountLimit = safeCount,
                selectedTargetIds = trimmedTargets
            )
        }
    }

    fun setIsHealMode(isHeal: Boolean) {
        _uiState.update { state ->
            val attacker = state.battleUnits.find { it.id == state.selectedAttackerId }
            val allowedHeal = isHeal && attacker?.hero?.role == HeroRole.MAGO

            val newTargets = if (allowedHeal) {
                val livingAllies = BattleRules.getLivingPlayerUnits(state.battleUnits, state.currentTurnPlayer)
                val mostWounded = livingAllies.minByOrNull { it.hpPercent }
                mostWounded?.id?.let { listOf(it) } ?: emptyList()
            } else {
                val opponentTargetableUnits = state.battleUnits.filter { 
                    it.playerId != state.currentTurnPlayer && isUnitTargetable(it, state.battleUnits) 
                }
                opponentTargetableUnits.firstOrNull()?.id?.let { listOf(it) } ?: emptyList()
            }

            state.copy(
                isHealMode = allowedHeal,
                selectedTargetIds = newTargets.take(state.targetCountLimit)
            )
        }
    }

    fun openTargetSelector(open: Boolean) {
        _uiState.update { it.copy(isTargetSelectorOpen = open) }
    }

    fun toggleTargetSelection(unitId: String) {
        _uiState.update { state ->
            val targetUnit = state.battleUnits.find { it.id == unitId } ?: return@update state

            if (state.isHealMode) {
                if (targetUnit.playerId != state.currentTurnPlayer || !targetUnit.isAlive) {
                    return@update state
                }
            } else {
                if (targetUnit.playerId == state.currentTurnPlayer || !targetUnit.isAlive || !isUnitTargetable(targetUnit, state.battleUnits)) {
                    return@update state
                }
            }

            val maxTargets = state.targetCountLimit.coerceIn(1, 3)
            val current = state.selectedTargetIds.toMutableList()
            if (current.contains(unitId)) {
                current.remove(unitId)
            } else {
                if (current.size < maxTargets) {
                    current.add(unitId)
                } else if (maxTargets == 1) {
                    current.clear()
                    current.add(unitId)
                } else {
                    current.removeAt(0)
                    current.add(unitId)
                }
            }
            state.copy(selectedTargetIds = current)
        }
    }

    // --- Strategy Minigame (Java StrategyEngine) ---
    fun openStrategyMinigame() {
        val state = _uiState.value
        if (!state.matchSettings.allowStrategyMinigame) {
            showFloatingNotice("El minijuego de estrategia está deshabilitado en esta partida.")
            return
        }
        if (state.selectedAttackType == AttackType.ULTIMATE) {
            showFloatingNotice("La estrategia solo está disponible para ataques básicos")
            return
        }
        if (state.strategyUsedByPlayersThisRound.contains(state.currentTurnPlayer)) {
            showFloatingNotice("¡Tu equipo ya usó la estrategia en esta ronda (máximo 1 uso por equipo por ronda)!")
            return
        }
        val attacker = state.battleUnits.find { it.id == state.selectedAttackerId }
        val diceCount = attacker?.hero?.strategyDiceCount ?: 2
        val defaultNumber = if (diceCount == 1) 3 else 7

        _uiState.update {
            it.copy(
                isStrategyModalOpen = true,
                strategyDiceCount = diceCount,
                strategySelectedNumber = defaultNumber,
                isStrategyRolling = false,
                strategyRollFinished = false,
                strategyOutcome = null,
                strategyDisplayNumber = defaultNumber
            )
        }
    }

    fun selectStrategyNumber(num: Int) {
        if (!_uiState.value.isStrategyRolling && !_uiState.value.strategyRollFinished) {
            _uiState.update { it.copy(strategySelectedNumber = num, strategyDisplayNumber = num) }
        }
    }

    fun executeStrategyRoll() {
        val playerUsingStrategy = _uiState.value.currentTurnPlayer
        val diceCount = _uiState.value.strategyDiceCount
        val chosen = _uiState.value.strategySelectedNumber
        val finalRoll = StrategyEngine.generateRoll(diceCount, chosen)
        val outcome = StrategyEngine.evaluateRoll(diceCount, chosen, finalRoll)
        val updatedUsedSet = _uiState.value.strategyUsedByPlayersThisRound + playerUsingStrategy

        _uiState.update {
            it.copy(
                isStrategyRolling = true,
                strategyOutcome = outcome,
                activeStrategyBonus = outcome,
                strategyUsedByPlayersThisRound = updatedUsedSet
            )
        }

        viewModelScope.launch {
            val rollTime = 600L
            val steps = 8
            val minRoll = if (diceCount == 1) 1 else 2
            val maxRoll = if (diceCount == 1) 6 else 12
            for (i in 0 until steps) {
                val rand = (minRoll..maxRoll).random()
                _uiState.update { it.copy(strategyDisplayNumber = rand) }
                delay(rollTime / steps)
            }

            _uiState.update {
                it.copy(
                    isStrategyRolling = false,
                    strategyRollFinished = true,
                    strategyDisplayNumber = finalRoll,
                    strategyOutcome = outcome,
                    activeStrategyBonus = outcome,
                    strategyUsedByPlayersThisRound = updatedUsedSet
                )
            }

            // Show result animation/banner, then directly execute the attack against the target(s)
            delay(1400L)
            if (_uiState.value.isStrategyModalOpen) {
                executeAttack()
            }
        }
    }

    fun confirmStrategyBonus() {
        val playerUsingStrategy = _uiState.value.currentTurnPlayer
        val updatedUsedSet = _uiState.value.strategyUsedByPlayersThisRound + playerUsingStrategy
        _uiState.update {
            it.copy(
                isStrategyRolling = false,
                strategyRollFinished = true,
                strategyUsedByPlayersThisRound = updatedUsedSet
            )
        }
        // Direct tap skips timer and executes attack immediately
        if (_uiState.value.isStrategyModalOpen) {
            executeAttack()
        }
    }

    // --- Execute Combat Action (Delegated to Java CombatCalculator) ---
    fun executeAttack() {
        val state = _uiState.value
        val attacker = state.battleUnits.find { it.id == state.selectedAttackerId } ?: return
        if (state.selectedTargetIds.isEmpty()) return

        val isUltimate = state.selectedAttackType == AttackType.ULTIMATE
        val strategyBonus = if (isUltimate) 0f else (state.activeStrategyBonus?.bonusPct ?: 0f)
        val targetCount = state.selectedTargetIds.size.coerceAtLeast(1)

        val updatedUnits = state.battleUnits.toMutableList()
        val newFloatingTexts = mutableListOf<FloatingCombatText>()

        val strategyTag = when {
            strategyBonus >= 0.20f -> " ★ +${(strategyBonus * 100).toInt()}% Estrategia ROTA"
            strategyBonus > 0f -> " ★ +${(strategyBonus * 100).toInt()}% Estrategia"
            strategyBonus < 0f -> " ⚡ -5% Estrategia"
            else -> ""
        }

        if (state.isHealMode) {
            val baseHeal = CombatCalculator.calculateHeal(attacker, targetCount, strategyBonus, isUltimate)

            state.selectedTargetIds.forEach { targetId ->
                val targetIndex = updatedUnits.indexOfFirst { it.id == targetId }
                if (targetIndex >= 0) {
                    val target = updatedUnits[targetIndex]
                    val newHp = (target.currentHp + baseHeal).coerceAtMost(target.maxHp)
                    val actualHeal = newHp - target.currentHp

                    updatedUnits[targetIndex] = target.copyWithHp(newHp)
                    newFloatingTexts.add(
                        FloatingCombatText(
                            System.currentTimeMillis() + targetIndex,
                            targetId,
                            "+$actualHeal HP!$strategyTag",
                            strategyBonus > 0f || isUltimate,
                            true
                        )
                    )
                }
            }
        } else {
            val isMysticUltimate = isUltimate && attacker.hero.role == HeroRole.MISTICO

            if (isMysticUltimate && attacker.hero.id == "m_cleric") {
                // Blessing of Serena is the highest impact support ultimate in the game -> lasts 3 full rounds
                updatedUnits.indices.forEach { idx ->
                    val unit = updatedUnits[idx]
                    if (unit.playerId == attacker.playerId && unit.isAlive) {
                        val newAtkBuff = (unit.attackBuffPct + 0.25f).coerceAtMost(1.0f)
                        val newDefBuff = (unit.defenseBuffPct + 0.20f).coerceAtMost(1.0f)
                        updatedUnits[idx] = unit.copyWithBuffs(newAtkBuff, newDefBuff, 3)
                        newFloatingTexts.add(
                            FloatingCombatText(
                                System.currentTimeMillis() + 500 + idx,
                                unit.id,
                                "+25% ATK/DEF (3r)!",
                                true,
                                true
                            )
                        )
                    }
                }
            }

            state.selectedTargetIds.forEach { targetId ->
                val targetIndex = updatedUnits.indexOfFirst { it.id == targetId }
                if (targetIndex >= 0) {
                    val target = updatedUnits[targetIndex]
                    val netDamage = CombatCalculator.calculateDamage(attacker, target, state.selectedAttackType, strategyBonus, targetCount)
                    val newHp = (target.currentHp - netDamage).coerceAtLeast(0)

                    var updatedDefBuff = target.defenseBuffPct
                    var updatedAtkBuff = target.attackBuffPct
                    var debuffText = ""
                    var mysticBuffDuration = 2

                    if (isMysticUltimate) {
                        when (attacker.hero.id) {
                            "m_solar" -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.35f).coerceAtLeast(-0.80f)
                                debuffText = "-35% DEF (1r)!"
                                mysticBuffDuration = 1
                            }
                            "m_druid" -> {
                                updatedAtkBuff = (target.attackBuffPct - 0.30f).coerceAtLeast(-0.80f)
                                debuffText = "-30% ATK (2r)!"
                                mysticBuffDuration = 2
                            }
                            "m_shadow" -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                updatedAtkBuff = (target.attackBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                debuffText = "-25% ATK/DEF (2r)!"
                                mysticBuffDuration = 2
                            }
                            "m_bard" -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                debuffText = "-25% DEF (1r)!"
                                mysticBuffDuration = 1
                            }
                            else -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                debuffText = "-25% DEF (1r)!"
                                mysticBuffDuration = 1
                            }
                        }
                    }

                    val buffRounds = if (isMysticUltimate) mysticBuffDuration else target.buffRoundsRemaining
                    val updatedTarget = target.copy(
                        null, null, null, null,
                        newHp, null, newHp > 0, null,
                        updatedAtkBuff, updatedDefBuff, null,
                        null, null, buffRounds
                    )
                    updatedUnits[targetIndex] = updatedTarget

                    val combatMessage = if (debuffText.isNotEmpty()) "-$netDamage HP ($debuffText)" else "-$netDamage HP!$strategyTag"
                    newFloatingTexts.add(
                        FloatingCombatText(
                            System.currentTimeMillis() + targetIndex,
                            targetId,
                            combatMessage,
                            strategyBonus > 0f || isUltimate,
                            false
                        )
                    )
                }
            }
        }

        val attackerIndex = updatedUnits.indexOfFirst { it.id == attacker.id }
        if (attackerIndex >= 0) {
            val newShots = if (isUltimate) 0 else attacker.shotsPerformed + 1
            updatedUnits[attackerIndex] = attacker.copy(
                null, null, null, null,
                null, null, null, true,
                0f, null, null,
                newShots, null
            )
        }

        val newAttacksUsed = state.attacksUsedThisTurn + 1

        _uiState.update {
            it.copy(
                battleUnits = updatedUnits,
                attacksUsedThisTurn = newAttacksUsed,
                isAttackModalOpen = false,
                isStrategyModalOpen = false,
                selectedAttackerId = null,
                selectedTargetIds = emptyList(),
                activeStrategyBonus = null,
                isTargetSelectorOpen = false,
                floatingCombatTexts = newFloatingTexts
            )
        }

        viewModelScope.launch {
            delay(1200)
            _uiState.update { it.copy(floatingCombatTexts = emptyList()) }
            checkTurnAndVictory()
        }
    }

    private fun checkTurnAndVictory() {
        val state = _uiState.value
        val count = state.playerCount

        val livingPlayers = (1..count).filter { p ->
            BattleRules.hasLivingUnits(state.battleUnits, p)
        }

        if (livingPlayers.size <= 1) {
            val winner = livingPlayers.firstOrNull() ?: 1
            _uiState.update {
                it.copy(
                    winnerPlayerId = winner,
                    matchIsOver = true,
                    isVictoryModalOpen = true
                )
            }
            return
        }

        val currentLivingUnacted = state.battleUnits.filter {
            it.playerId == state.currentTurnPlayer && it.isAlive && !it.hasActed
        }

        if (state.attacksUsedThisTurn >= state.maxAttacksPerTurn || currentLivingUnacted.isEmpty()) {
            advancePlayerTurn()
        }
    }

    private fun advancePlayerTurn() {
        val state = _uiState.value
        val count = state.playerCount
        var nextPlayer = (state.currentTurnPlayer % count) + 1

        var attempts = 0
        while (attempts < count && !BattleRules.hasLivingUnits(state.battleUnits, nextPlayer)) {
            nextPlayer = (nextPlayer % count) + 1
            attempts++
        }

        val isNewRound = nextPlayer <= state.currentTurnPlayer
        val nextRound = if (isNewRound) state.roundNumber + 1 else state.roundNumber
        val updatedStrategyUsed = if (isNewRound) emptySet<Int>() else state.strategyUsedByPlayersThisRound

        if (isNewRound && !state.matchSettings.isUnlimitedRounds && nextRound > state.matchSettings.maxRounds) {
            // Match completed all allowed rounds! Determine winner by highest remaining total HP
            val playerHps = (1..count).map { p ->
                val totalHp = state.battleUnits.filter { it.playerId == p && it.isAlive }.sumOf { it.currentHp }
                p to totalHp
            }
            val winner = playerHps.maxByOrNull { it.second }?.first ?: 1
            _uiState.update {
                it.copy(
                    winnerPlayerId = winner,
                    matchIsOver = true,
                    isVictoryModalOpen = true,
                    roundNumber = state.matchSettings.maxRounds
                )
            }
            return
        }

        // Refresh units for the next player so they can use all their living heroes in this round.
        // If a new round begins, all units across all teams are refreshed as well.
        val refreshedUnits = if (isNewRound) {
            BattleRules.refreshAllUnitsForNewRound(state.battleUnits)
        } else {
            BattleRules.refreshPlayerUnits(state.battleUnits, nextPlayer)
        }

        _uiState.update {
            it.copy(
                currentTurnPlayer = nextPlayer,
                roundNumber = nextRound,
                attacksUsedThisTurn = 0,
                strategyUsedByPlayersThisRound = updatedStrategyUsed,
                battleUnits = refreshedUnits
            )
        }
    }
}
