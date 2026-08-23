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
        val units = mutableListOf<BattleUnit>()

        val team1 = state.selectedTeams[1] ?: state.allTeams[0]
        units.addAll(createFormationUnits(playerId = 1, team = team1))

        val team2 = state.selectedTeams[2] ?: state.allTeams[1 % state.allTeams.size]
        units.addAll(createFormationUnits(playerId = 2, team = team2))

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
                maxAttacksPerTurn = BattleRules.MAX_ATTACKS_PER_TURN,
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

    private fun createFormationUnits(playerId: Int, team: Team): List<BattleUnit> {
        val list = mutableListOf<BattleUnit>()
        var unitIndex = 0

        team.guerreros.forEach { hero ->
            list.add(BattleUnit("p${playerId}_u${unitIndex++}", playerId, list.size, hero, "Guerreros"))
        }
        team.misticos.forEach { hero ->
            list.add(BattleUnit("p${playerId}_u${unitIndex++}", playerId, list.size, hero, "Místicos"))
        }
        team.magos.forEach { hero ->
            list.add(BattleUnit("p${playerId}_u${unitIndex++}", playerId, list.size, hero, "Magos"))
        }

        return list
    }

    private fun createPlayer3FormationUnits(playerId: Int, team: Team): List<BattleUnit> {
        val list = mutableListOf<BattleUnit>()
        var unitIndex = 0

        team.guerreros.forEach { hero ->
            list.add(BattleUnit("p${playerId}_u${unitIndex++}", playerId, list.size, hero, "Guerreros"))
        }
        team.misticos.forEach { hero ->
            list.add(BattleUnit("p${playerId}_u${unitIndex++}", playerId, list.size, hero, "Místicos"))
        }
        team.magos.forEach { hero ->
            list.add(BattleUnit("p${playerId}_u${unitIndex++}", playerId, list.size, hero, "Magos"))
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
        _uiState.update { state ->
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

            val chosen = _uiState.value.strategySelectedNumber
            val rollTime = 600L
            val steps = 8
            for (i in 0 until steps) {
                val rand = (1..6).random()
                _uiState.update { it.copy(strategyDisplayNumber = rand) }
                delay(rollTime / steps)
            }

            val finalRoll = StrategyEngine.generateRoll(chosen)
            val outcome = StrategyEngine.evaluateRoll(chosen, finalRoll)

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
                            "+$actualHeal HP!",
                            strategyBonus > 0f || isUltimate,
                            true
                        )
                    )
                }
            }
        } else {
            val isMysticUltimate = isUltimate && attacker.hero.role == HeroRole.MISTICO

            if (isMysticUltimate && attacker.hero.id == "m_cleric") {
                updatedUnits.indices.forEach { idx ->
                    val unit = updatedUnits[idx]
                    if (unit.playerId == attacker.playerId && unit.isAlive) {
                        val newAtkBuff = (unit.attackBuffPct + 0.25f).coerceAtMost(1.0f)
                        val newDefBuff = (unit.defenseBuffPct + 0.20f).coerceAtMost(1.0f)
                        updatedUnits[idx] = unit.copyWithBuffs(newAtkBuff, newDefBuff)
                        newFloatingTexts.add(
                            FloatingCombatText(
                                System.currentTimeMillis() + 500 + idx,
                                unit.id,
                                "+25% ATK/DEF!",
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

                    if (isMysticUltimate) {
                        when (attacker.hero.id) {
                            "m_solar" -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.35f).coerceAtLeast(-0.80f)
                                debuffText = "-35% DEF!"
                            }
                            "m_druid" -> {
                                updatedAtkBuff = (target.attackBuffPct - 0.30f).coerceAtLeast(-0.80f)
                                debuffText = "-30% ATK!"
                            }
                            "m_shadow" -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                updatedAtkBuff = (target.attackBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                debuffText = "-25% ATK/DEF!"
                            }
                            else -> {
                                updatedDefBuff = (target.defenseBuffPct - 0.25f).coerceAtLeast(-0.80f)
                                debuffText = "-25% DEF!"
                            }
                        }
                    }

                    val updatedTarget = target.copy(
                        null, null, null, null,
                        newHp, null, newHp > 0, null,
                        updatedAtkBuff, updatedDefBuff, null
                    )
                    updatedUnits[targetIndex] = updatedTarget

                    newFloatingTexts.add(
                        FloatingCombatText(
                            System.currentTimeMillis() + targetIndex,
                            targetId,
                            if (debuffText.isNotEmpty()) "-$netDamage HP ($debuffText)" else "-$netDamage HP!",
                            strategyBonus > 0f || isUltimate,
                            false
                        )
                    )
                }
            }
        }

        val attackerIndex = updatedUnits.indexOfFirst { it.id == attacker.id }
        if (attackerIndex >= 0) {
            updatedUnits[attackerIndex] = attacker.copy(
                null, null, null, null,
                null, null, null, true,
                0f, null, null
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
                battleUnits = refreshedUnits
            )
        }
    }
}
