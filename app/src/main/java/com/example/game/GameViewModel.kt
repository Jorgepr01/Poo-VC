package com.example.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

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
    val maxAttacksPerTurn: Int = 3,
    val winnerPlayerId: Int? = null,
    val matchIsOver: Boolean = false,
    
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
    
    // Strategy Minigame
    val isStrategyModalOpen: Boolean = false,
    val strategySelectedNumber: Int = 1,
    val isStrategyRolling: Boolean = false,
    val strategyDisplayNumber: Int = 1,
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
            id = id,
            name = name,
            isDefault = false,
            guerreros = state.editingGuerreros,
            misticos = state.editingMisticos,
            magos = state.editingMagos
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
        val units = mutableListOf<BattleUnit>()

        // Build Player 1 units (Triangle formation left: 1 Mago back, 2 Místicos mid, 3 Guerreros front)
        val team1 = state.selectedTeams[1] ?: state.allTeams[0]
        units.addAll(createFormationUnits(playerId = 1, team = team1, isLeftPlayer = true))

        // Build Player 2 units (Triangle formation right: 3 Guerreros front, 2 Místicos mid, 1 Mago back)
        val team2 = state.selectedTeams[2] ?: state.allTeams[1 % state.allTeams.size]
        units.addAll(createFormationUnits(playerId = 2, team = team2, isLeftPlayer = false))

        // If 3 players mode, build Player 3 units (Bottom center: 2 Guerreros top, 2 Místicos mid, 2 Magos bot)
        if (count >= 3) {
            val team3 = state.selectedTeams[3] ?: state.allTeams[2 % state.allTeams.size]
            units.addAll(createPlayer3FormationUnits(playerId = 3, team = team3))
        }

        _uiState.update {
            it.copy(
                currentScreen = GameScreen.BATTLE,
                battleUnits = units,
                currentTurnPlayer = 1,
                roundNumber = 1,
                attacksUsedThisTurn = 0,
                maxAttacksPerTurn = 3,
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

    private fun createFormationUnits(playerId: Int, team: Team, isLeftPlayer: Boolean): List<BattleUnit> {
        val list = mutableListOf<BattleUnit>()
        var unitIndex = 0

        // Guerreros (Front line)
        team.guerreros.forEach { hero ->
            list.add(
                BattleUnit(
                    id = "p${playerId}_u${unitIndex++}",
                    playerId = playerId,
                    slotIndex = list.size,
                    hero = hero,
                    rowLabel = "Guerreros"
                )
            )
        }

        // Místicos (Mid line)
        team.misticos.forEach { hero ->
            list.add(
                BattleUnit(
                    id = "p${playerId}_u${unitIndex++}",
                    playerId = playerId,
                    slotIndex = list.size,
                    hero = hero,
                    rowLabel = "Místicos"
                )
            )
        }

        // Magos (Back line)
        team.magos.forEach { hero ->
            list.add(
                BattleUnit(
                    id = "p${playerId}_u${unitIndex++}",
                    playerId = playerId,
                    slotIndex = list.size,
                    hero = hero,
                    rowLabel = "Magos"
                )
            )
        }

        return list
    }

    private fun createPlayer3FormationUnits(playerId: Int, team: Team): List<BattleUnit> {
        val list = mutableListOf<BattleUnit>()
        var unitIndex = 0

        // Guerreros (Top / Front)
        team.guerreros.forEach { hero ->
            list.add(
                BattleUnit(
                    id = "p${playerId}_u${unitIndex++}",
                    playerId = playerId,
                    slotIndex = list.size,
                    hero = hero,
                    rowLabel = "Guerreros"
                )
            )
        }

        // Místicos (Mid line)
        team.misticos.forEach { hero ->
            list.add(
                BattleUnit(
                    id = "p${playerId}_u${unitIndex++}",
                    playerId = playerId,
                    slotIndex = list.size,
                    hero = hero,
                    rowLabel = "Místicos"
                )
            )
        }

        // Magos (Back line)
        team.magos.forEach { hero ->
            list.add(
                BattleUnit(
                    id = "p${playerId}_u${unitIndex++}",
                    playerId = playerId,
                    slotIndex = list.size,
                    hero = hero,
                    rowLabel = "Magos"
                )
            )
        }

        return list
    }

    // --- Frontline & Targeting Rules ---
    fun isUnitTargetable(targetUnit: BattleUnit, allUnits: List<BattleUnit>): Boolean {
        if (!targetUnit.isAlive) return false
        // Guerreros can always be targeted as frontline
        if (targetUnit.hero.role == HeroRole.GUERRERO) return true
        // Místicos and Magos are protected while their player still has living Guerreros
        val hasLivingWarriors = allUnits.any {
            it.playerId == targetUnit.playerId && it.hero.role == HeroRole.GUERRERO && it.isAlive
        }
        return !hasLivingWarriors
    }

    fun isUnitProtected(unit: BattleUnit, allUnits: List<BattleUnit>): Boolean {
        if (!unit.isAlive) return false
        if (unit.hero.role == HeroRole.GUERRERO) return false
        return allUnits.any {
            it.playerId == unit.playerId && it.hero.role == HeroRole.GUERRERO && it.isAlive
        }
    }

    fun showFloatingNotice(text: String) {
        val newId = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                floatingCombatTexts = listOf(
                    FloatingCombatText(
                        id = newId,
                        targetUnitId = "",
                        text = text,
                        isCritical = false
                    )
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

        // If it belongs to current player, is alive, has not acted, and player has attacks remaining (max 3)
        if (unit.playerId == state.currentTurnPlayer && unit.isAlive && !unit.hasActed && state.attacksUsedThisTurn < state.maxAttacksPerTurn) {
            openAttackModal(unit)
        } else if (state.isAttackModalOpen && unit.playerId != state.currentTurnPlayer && unit.isAlive) {
            // Selecting target from battlefield
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

        // For Mago, default to healing mode and target the most wounded ally; for others, default to attack mode
        val defaultTargets = if (isMage) {
            val livingAllies = state.battleUnits.filter { it.playerId == state.currentTurnPlayer && it.isAlive }
            val mostWounded = livingAllies.minByOrNull { it.currentHp.toFloat() / it.maxHp.toFloat() }
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
        _uiState.update { state ->
            // If switching to Ultimate, disable/clear strategy bonus as strategy is only for basic attacks
            if (type == AttackType.ULTIMATE) {
                state.copy(
                    selectedAttackType = type,
                    activeStrategyBonus = null,
                    isStrategyModalOpen = false
                )
            } else {
                state.copy(selectedAttackType = type)
            }
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
            val newTargets = if (isHeal) {
                // Default target to most wounded living ally
                val livingAllies = state.battleUnits.filter { it.playerId == state.currentTurnPlayer && it.isAlive }
                val mostWounded = livingAllies.minByOrNull { it.currentHp.toFloat() / it.maxHp.toFloat() }
                mostWounded?.id?.let { listOf(it) } ?: emptyList()
            } else {
                // Default target to first targetable opponent
                val opponentTargetableUnits = state.battleUnits.filter { 
                    it.playerId != state.currentTurnPlayer && isUnitTargetable(it, state.battleUnits) 
                }
                opponentTargetableUnits.firstOrNull()?.id?.let { listOf(it) } ?: emptyList()
            }

            state.copy(
                isHealMode = isHeal,
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
                // Heal mode: only living allies of current player
                if (targetUnit.playerId != state.currentTurnPlayer || !targetUnit.isAlive) {
                    return@update state
                }
            } else {
                // Attack mode: must be alive opponent and targetable according to frontline warrior rule
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
                    // Replace oldest selected target or cycle
                    current.removeAt(0)
                    current.add(unitId)
                }
            }
            state.copy(selectedTargetIds = current)
        }
    }

    // --- Strategy Minigame ---
    fun openStrategyMinigame() {
        val state = _uiState.value
        if (state.selectedAttackType == AttackType.ULTIMATE) {
            showFloatingNotice("La estrategia solo está disponible para ataques básicos")
            return
        }
        _uiState.update {
            it.copy(
                isStrategyModalOpen = true,
                strategySelectedNumber = 3,
                isStrategyRolling = false,
                strategyRollFinished = false,
                strategyOutcome = null,
                strategyDisplayNumber = 3
            )
        }
    }

    fun selectStrategyNumber(num: Int) {
        if (!_uiState.value.isStrategyRolling && !_uiState.value.strategyRollFinished) {
            _uiState.update { it.copy(strategySelectedNumber = num, strategyDisplayNumber = num) }
        }
    }

    fun executeStrategyRoll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isStrategyRolling = true) }

            // Simulated 600ms roll animation
            val chosen = _uiState.value.strategySelectedNumber
            val rollTime = 600L
            val steps = 8
            for (i in 0 until steps) {
                val rand = Random.nextInt(1, 7)
                _uiState.update { it.copy(strategyDisplayNumber = rand) }
                delay(rollTime / steps)
            }

            // Determine outcome (generous win chance for engaging tactical feel, matching wireframe sample winning 3)
            val finalRoll = if (Random.nextFloat() < 0.65f) chosen else Random.nextInt(1, 7)
            val isWin = (finalRoll == chosen) || (finalRoll % 2 == chosen % 2)
            val bonusPct = if (isWin) 0.15f else -0.05f
            val msg = if (isWin) "¡Perfecto! Ganaste un bono" else "¡Intento fallido! Penalización menor"

            val outcome = StrategyRollResult(
                selectedNumber = chosen,
                rolledNumber = finalRoll,
                isWin = isWin,
                bonusPct = bonusPct,
                message = msg
            )

            _uiState.update {
                it.copy(
                    isStrategyRolling = false,
                    strategyRollFinished = true,
                    strategyDisplayNumber = finalRoll,
                    strategyOutcome = outcome
                )
            }
        }
    }

    fun confirmStrategyBonus() {
        val outcome = _uiState.value.strategyOutcome
        _uiState.update {
            it.copy(
                isStrategyModalOpen = false,
                activeStrategyBonus = outcome
            )
        }
    }

    // --- Execute Combat Action (Attack or Heal) ---
    fun executeAttack() {
        val state = _uiState.value
        val attacker = state.battleUnits.find { it.id == state.selectedAttackerId } ?: return
        if (state.selectedTargetIds.isEmpty()) return

        val isUltimate = state.selectedAttackType == AttackType.ULTIMATE
        // Strategy bonus ONLY applies to basic attacks, strictly 0 for ultimate
        val strategyBonus = if (isUltimate) 0f else (state.activeStrategyBonus?.bonusPct ?: 0f)
        val targetCount = state.selectedTargetIds.size.coerceAtLeast(1)

        val updatedUnits = state.battleUnits.toMutableList()
        val newFloatingTexts = mutableListOf<FloatingCombatText>()

        if (state.isHealMode) {
            // Healing mode for Magos / Místicos
            val healFactor = when (targetCount) {
                1 -> 1.0f
                2 -> 0.80f
                else -> 0.65f
            }
            val baseHeal = ((attacker.effectiveAttack * 0.60f + 16f) * healFactor * (1f + strategyBonus.coerceAtLeast(0f))).toInt().coerceAtLeast(12)

            state.selectedTargetIds.forEach { targetId ->
                val targetIndex = updatedUnits.indexOfFirst { it.id == targetId }
                if (targetIndex >= 0) {
                    val target = updatedUnits[targetIndex]
                    val newHp = (target.currentHp + baseHeal).coerceAtMost(target.maxHp)
                    val actualHeal = newHp - target.currentHp

                    updatedUnits[targetIndex] = target.copy(currentHp = newHp)
                    newFloatingTexts.add(
                        FloatingCombatText(
                            id = System.currentTimeMillis() + targetIndex,
                            targetUnitId = targetId,
                            text = "+$actualHeal HP!",
                            isCritical = strategyBonus > 0f || isUltimate,
                            isHeal = true
                        )
                    )
                }
            }
        } else {
            // Damage mode (Attacking rivals)
            val spreadFactor = when (targetCount) {
                1 -> 1.0f
                2 -> 0.85f
                else -> 0.70f
            }
            val baseDamage = attacker.effectiveAttack * (if (isUltimate) 1.35f else 1.0f) * spreadFactor * (1f + strategyBonus)

            state.selectedTargetIds.forEach { targetId ->
                val targetIndex = updatedUnits.indexOfFirst { it.id == targetId }
                if (targetIndex >= 0) {
                    val target = updatedUnits[targetIndex]
                    val effectiveDef = target.effectiveDefense * 0.45f
                    val netDamage = (baseDamage - effectiveDef).toInt().coerceAtLeast(10)
                    val newHp = (target.currentHp - netDamage).coerceAtLeast(0)

                    updatedUnits[targetIndex] = target.copy(currentHp = newHp)
                    newFloatingTexts.add(
                        FloatingCombatText(
                            id = System.currentTimeMillis() + targetIndex,
                            targetUnitId = targetId,
                            text = "-$netDamage HP!",
                            isCritical = strategyBonus > 0f || isUltimate,
                            isHeal = false
                        )
                    )
                }
            }
        }

        // Mark attacker as having acted
        val attackerIndex = updatedUnits.indexOfFirst { it.id == attacker.id }
        if (attackerIndex >= 0) {
            updatedUnits[attackerIndex] = attacker.copy(
                hasActed = true,
                attackBuffPct = 0f // consume strategy buff
            )
        }

        val newAttacksUsed = state.attacksUsedThisTurn + 1

        _uiState.update {
            it.copy(
                battleUnits = updatedUnits,
                attacksUsedThisTurn = newAttacksUsed,
                isAttackModalOpen = false,
                selectedAttackerId = null,
                selectedTargetIds = emptyList(),
                activeStrategyBonus = null,
                isTargetSelectorOpen = false,
                floatingCombatTexts = newFloatingTexts
            )
        }

        // Clean up floating text after delay and advance turn
        viewModelScope.launch {
            delay(1200)
            _uiState.update { it.copy(floatingCombatTexts = emptyList()) }
            checkTurnAndVictory()
        }
    }

    private fun checkTurnAndVictory() {
        val state = _uiState.value
        val count = state.playerCount

        // Check living status for each player
        val livingPlayers = (1..count).filter { p ->
            state.battleUnits.any { it.playerId == p && it.isAlive }
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

        // Check if current player has any more unacted living units
        val currentLivingUnacted = state.battleUnits.filter {
            it.playerId == state.currentTurnPlayer && it.isAlive && !it.hasActed
        }

        // End turn if maximum attacks (3) reached OR no more units can attack
        if (state.attacksUsedThisTurn >= state.maxAttacksPerTurn || currentLivingUnacted.isEmpty()) {
            // Advance to next active player
            advancePlayerTurn()
        }
    }

    private fun advancePlayerTurn() {
        val state = _uiState.value
        val count = state.playerCount
        var nextPlayer = (state.currentTurnPlayer % count) + 1

        // Skip players with 0 living units
        var attempts = 0
        while (attempts < count && !state.battleUnits.any { it.playerId == nextPlayer && it.isAlive }) {
            nextPlayer = (nextPlayer % count) + 1
            attempts++
        }

        // Check if full round completed (e.g. if nextPlayer is <= currentTurnPlayer or 1)
        val isNewRound = nextPlayer <= state.currentTurnPlayer
        val nextRound = if (isNewRound) state.roundNumber + 1 else state.roundNumber

        val refreshedUnits = state.battleUnits.map { unit ->
            if (isNewRound && unit.playerId == nextPlayer) {
                unit.copy(hasActed = false)
            } else if (unit.playerId == nextPlayer && !state.battleUnits.any { it.playerId == nextPlayer && it.isAlive && !it.hasActed }) {
                unit.copy(hasActed = false)
            } else {
                unit
            }
        }

        _uiState.update {
            it.copy(
                currentTurnPlayer = nextPlayer,
                roundNumber = nextRound,
                attacksUsedThisTurn = 0,
                battleUnits = refreshedUnits
            )
        }
    }
}
