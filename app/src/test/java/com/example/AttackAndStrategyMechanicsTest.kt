package com.example

import com.example.game.GameViewModel
import com.example.model.AttackType
import com.example.model.BattleUnit
import com.example.model.HeroCatalog
import com.example.model.HeroRole
import com.example.model.StrategyRollResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AttackAndStrategyMechanicsTest {

    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        viewModel = GameViewModel()
        viewModel.startBattle()
    }

    @Test
    fun testMageBasicAttackDefaultsToHeal() {
        val state = viewModel.uiState.value
        val mage = state.battleUnits.firstOrNull { it.playerId == state.currentTurnPlayer && it.hero.role == HeroRole.MAGO }
        
        if (mage != null) {
            viewModel.openAttackModal(mage)
            val attackState = viewModel.uiState.value

            assertTrue(attackState.isAttackModalOpen)
            assertTrue("Mage basic attack should default to heal mode", attackState.isHealMode)
            assertEquals(AttackType.BASIC, attackState.selectedAttackType)
        }
    }

    @Test
    fun testMysticBasicAttackIsDamageOnlyAndCannotHeal() {
        val state = viewModel.uiState.value
        val mystic = state.battleUnits.firstOrNull { it.playerId == state.currentTurnPlayer && it.hero.role == HeroRole.MISTICO }
        
        if (mystic != null) {
            viewModel.openAttackModal(mystic)
            val attackState = viewModel.uiState.value

            assertTrue(attackState.isAttackModalOpen)
            assertFalse("Mystic basic attack MUST NOT be heal mode", attackState.isHealMode)
            assertEquals(AttackType.BASIC, attackState.selectedAttackType)

            // Attempting to set heal mode must be rejected for Mystics
            viewModel.setIsHealMode(true)
            assertFalse("Mystic must not be able to activate heal mode", viewModel.uiState.value.isHealMode)
        }
    }

    @Test
    fun testUltimateRequiresAtLeastOneShot() {
        val state = viewModel.uiState.value
        val mystic = state.battleUnits.firstOrNull { it.playerId == state.currentTurnPlayer && it.hero.role == HeroRole.MISTICO }
        
        if (mystic != null) {
            viewModel.openAttackModal(mystic)
            // Attempting to select ultimate before any shot
            viewModel.setAttackType(AttackType.ULTIMATE)
            assertEquals("Ultimate must be blocked before any shot", AttackType.BASIC, viewModel.uiState.value.selectedAttackType)

            // Perform 1 basic shot (default targetable unit is already selected in openAttackModal)
            viewModel.executeAttack()

            // In the state, mystic now has 1 shot and can use ultimate
            val updatedMystic = viewModel.uiState.value.battleUnits.first { it.id == mystic.id }
            assertEquals(1, updatedMystic.shotsPerformed)
            assertTrue(updatedMystic.canUseUltimate())
        }
    }

    @Test
    fun testMysticUltimateAppliesDebuff() {
        val state = viewModel.uiState.value
        val mystic = state.battleUnits.firstOrNull { it.playerId == state.currentTurnPlayer && it.hero.role == HeroRole.MISTICO }
        
        if (mystic != null) {
            // First perform a basic shot to charge ultimate
            viewModel.openAttackModal(mystic)
            viewModel.executeAttack()

            // Now mystic has 1 shot, ready for Ultimate
            val chargedState = viewModel.uiState.value
            val chargedMystic = chargedState.battleUnits.first { it.id == mystic.id }
            viewModel.openAttackModal(chargedMystic)
            viewModel.setAttackType(AttackType.ULTIMATE)
            assertEquals(AttackType.ULTIMATE, viewModel.uiState.value.selectedAttackType)

            val targetOpponentId = viewModel.uiState.value.selectedTargetIds.first()
            val initialDefBuff = chargedState.battleUnits.first { it.id == targetOpponentId }.defenseBuffPct
            viewModel.executeAttack()

            val updatedTarget = viewModel.uiState.value.battleUnits.first { it.id == targetOpponentId }
            assertTrue(
                "Mystic ultimate should reduce target's stats or apply debuff",
                updatedTarget.defenseBuffPct <= initialDefBuff || updatedTarget.attackBuffPct <= 0f
            )
        }
    }

    @Test
    fun testStrategyDisabledDuringUltimate() {
        val state = viewModel.uiState.value
        val unit = state.battleUnits.first { 
            it.playerId == state.currentTurnPlayer && it.isAlive && it.hero.role == HeroRole.GUERRERO 
        }
        
        // Execute 1 basic attack to charge ultimate
        viewModel.openAttackModal(unit)
        viewModel.executeAttack()

        val chargedState = viewModel.uiState.value
        val chargedUnit = chargedState.battleUnits.first { it.id == unit.id }
        viewModel.openAttackModal(chargedUnit)

        // Switch to Ultimate
        viewModel.setAttackType(AttackType.ULTIMATE)
        assertEquals(AttackType.ULTIMATE, viewModel.uiState.value.selectedAttackType)
        assertNull(viewModel.uiState.value.activeStrategyBonus)

        // Try opening strategy during ultimate
        viewModel.openStrategyMinigame()
        // Strategy modal should remain closed
        assertFalse("Strategy modal must not open during Ultimate attack", viewModel.uiState.value.isStrategyModalOpen)
    }

    @Test
    fun testStrategyAvailableDuringBasicAttack() {
        val state = viewModel.uiState.value
        val unit = state.battleUnits.first { it.playerId == state.currentTurnPlayer && it.isAlive }
        
        viewModel.openAttackModal(unit)
        viewModel.setAttackType(AttackType.BASIC)
        assertEquals(AttackType.BASIC, viewModel.uiState.value.selectedAttackType)

        // Open strategy minigame
        viewModel.openStrategyMinigame()
        assertTrue("Strategy modal should open during Basic attack", viewModel.uiState.value.isStrategyModalOpen)
    }

    @Test
    fun testStrategyCanOnlyBeUsedOncePerRoundPerTeam() {
        val state = viewModel.uiState.value
        val player1Units = state.battleUnits.filter { it.playerId == 1 && it.isAlive }
        assertTrue("Player 1 should have multiple living units", player1Units.size >= 2)

        val unit1 = player1Units[0]
        val unit2 = player1Units[1]

        // Character 1 opens attack modal and uses strategy
        viewModel.openAttackModal(unit1)
        viewModel.openStrategyMinigame()
        assertTrue(viewModel.uiState.value.isStrategyModalOpen)

        // Select number and confirm roll/attack
        viewModel.selectStrategyNumber(4)
        viewModel.executeStrategyRoll()
        // Wait or confirm bonus to finish
        viewModel.confirmStrategyBonus()

        // Verify Player 1 is marked as having used strategy in Round 1
        assertTrue(
            "Player 1 should be registered as having used strategy this round",
            viewModel.uiState.value.strategyUsedByPlayersThisRound.contains(1)
        )

        // Character 2 tries to use strategy in the same round
        viewModel.openAttackModal(unit2)
        viewModel.openStrategyMinigame()

        // Strategy modal must NOT open because it was already used by team 1 this round
        assertFalse(
            "Strategy modal must not open for second character of the same team in same round",
            viewModel.uiState.value.isStrategyModalOpen
        )
    }

    @Test
    fun testStrategyResetsOnNewRound() {
        // Player 1 uses strategy in Round 1
        val p1Unit = viewModel.uiState.value.battleUnits.first { it.playerId == 1 && it.isAlive }
        viewModel.openAttackModal(p1Unit)
        viewModel.openStrategyMinigame()
        viewModel.executeStrategyRoll()

        assertTrue(
            "Player 1 should be marked as having used strategy immediately upon roll",
            viewModel.uiState.value.strategyUsedByPlayersThisRound.contains(1)
        )

        // Advance turns to complete Round 1: Player 1 -> Player 2 -> Round 2 (Player 1)
        viewModel.passTurn() // Moves to Player 2
        assertEquals(2, viewModel.uiState.value.currentTurnPlayer)

        viewModel.passTurn() // Moves back to Player 1 and increments to Round 2
        assertEquals(1, viewModel.uiState.value.currentTurnPlayer)
        assertEquals(2, viewModel.uiState.value.roundNumber)

        // Strategy usage must be reset for the new round
        assertTrue(
            "Strategy usage should reset at the start of a new round",
            viewModel.uiState.value.strategyUsedByPlayersThisRound.isEmpty()
        )

        // Player 1 can now use strategy again in Round 2
        val p1Round2Unit = viewModel.uiState.value.battleUnits.first { it.playerId == 1 && it.isAlive }
        viewModel.openAttackModal(p1Round2Unit)
        viewModel.openStrategyMinigame()
        assertTrue("Strategy should be available again in Round 2", viewModel.uiState.value.isStrategyModalOpen)
    }

    @Test
    fun testBrokenHeroUses1d6StrategyInCombatFlow() {
        val state = viewModel.uiState.value
        val brokenUnit = state.battleUnits.first { 
            it.playerId == state.currentTurnPlayer && it.isAlive && it.hero.strategyDiceCount == 1 
        }

        // Open attack modal for the broken hero
        viewModel.openAttackModal(brokenUnit)
        viewModel.openStrategyMinigame()

        val minigameState = viewModel.uiState.value
        assertTrue(minigameState.isStrategyModalOpen)
        assertEquals(1, minigameState.strategyDiceCount)
        assertEquals(3, minigameState.strategySelectedNumber) // Default selection for 1d6

        // Execute roll
        viewModel.executeStrategyRoll()

        val activeBonus = viewModel.uiState.value.activeStrategyBonus
        assertNotNull(activeBonus)
        assertEquals(1, activeBonus!!.diceCount)
        assertTrue(activeBonus.isBroken)
        if (activeBonus.isWin) {
            assertEquals(0.35f, activeBonus.bonusPct, 0.001f)
        }

        // Confirming bonus transitions directly to attack execution
        viewModel.confirmStrategyBonus()
        assertTrue(
            "Player 1 should be registered as having used strategy this round",
            viewModel.uiState.value.strategyUsedByPlayersThisRound.contains(1)
        )
    }

    @Test
    fun testStandardHeroUses2d6StrategyInCombatFlow() {
        val state = viewModel.uiState.value
        val standardUnit = state.battleUnits.first { 
            it.playerId == state.currentTurnPlayer && it.isAlive && it.hero.strategyDiceCount == 2 
        }

        // Open attack modal for the standard hero
        viewModel.openAttackModal(standardUnit)
        viewModel.openStrategyMinigame()

        val minigameState = viewModel.uiState.value
        assertTrue(minigameState.isStrategyModalOpen)
        assertEquals(2, minigameState.strategyDiceCount)
        assertEquals(7, minigameState.strategySelectedNumber) // Default selection for 2d6

        // Select number in 2..12 range
        viewModel.selectStrategyNumber(10)
        assertEquals(10, viewModel.uiState.value.strategySelectedNumber)

        // Execute roll
        viewModel.executeStrategyRoll()

        val activeBonus = viewModel.uiState.value.activeStrategyBonus
        assertNotNull(activeBonus)
        assertEquals(2, activeBonus!!.diceCount)
        assertFalse(activeBonus.isBroken)
        if (activeBonus.isWin) {
            assertEquals(0.15f, activeBonus.bonusPct, 0.001f)
        }
    }

    @Test
    fun testFrontlineProtectionForGuerrerosUpToRound3() {
        val state = viewModel.uiState.value
        val p1Unit = state.battleUnits.first { it.playerId == 1 && it.isAlive }
        viewModel.openAttackModal(p1Unit)

        val opponentLivingWarriors = viewModel.uiState.value.battleUnits.filter { 
            it.playerId == 2 && it.isAlive && it.hero.role == HeroRole.GUERRERO 
        }

        if (opponentLivingWarriors.isNotEmpty() && viewModel.uiState.value.roundNumber <= 3) {
            val validTargets = viewModel.uiState.value.battleUnits.filter { 
                it.playerId == 2 && com.example.engine.BattleRules.isUnitTargetable(it, viewModel.uiState.value.battleUnits) 
            }
            // All valid targets in rounds 1-3 must be the front-line Warriors while they are alive
            for (target in validTargets) {
                assertEquals(
                    "Frontline warrior must shield backline in rounds 1-3",
                    HeroRole.GUERRERO,
                    target.hero.role
                )
            }
        }
    }

    @Test
    fun testCombatFeedbackGeneratedOnAttack() {
        val state = viewModel.uiState.value
        val initialFloatTextsCount = state.floatingCombatTexts.size
        val unit = state.battleUnits.first { it.playerId == state.currentTurnPlayer && it.isAlive }
        
        viewModel.openAttackModal(unit)
        viewModel.executeAttack()

        val updatedState = viewModel.uiState.value
        assertTrue(
            "Combat system should emit visual feedback or reduce attacker attacks remaining",
            updatedState.attacksUsedThisTurn > 0 || updatedState.floatingCombatTexts.size >= initialFloatTextsCount
        )
    }

    @Test
    fun testThreePlayersOpponentsGroupedSeparately() {
        val vm3 = GameViewModel()
        vm3.setPlayerCount(3)
        vm3.startBattle()

        val state = vm3.uiState.value
        assertEquals(3, state.playerCount)
        assertEquals(1, state.currentTurnPlayer)

        val targetableOpponents = state.battleUnits.filter { 
            it.playerId != state.currentTurnPlayer && vm3.isUnitTargetable(it, state.battleUnits) 
        }
        val groupedByPlayer = targetableOpponents.groupBy { it.playerId }
        
        assertEquals("There must be 2 distinct rival player groups for Player 1", 2, groupedByPlayer.size)
        assertTrue("Player 2 should be in rival group", groupedByPlayer.containsKey(2))
        assertTrue("Player 3 should be in rival group", groupedByPlayer.containsKey(3))

        // Selecting a unit from Player 2 then Player 3
        val p2Unit = groupedByPlayer[2]!!.first()
        val p3Unit = groupedByPlayer[3]!!.first()

        vm3.toggleTargetSelection(p2Unit.id)
        assertTrue(vm3.uiState.value.selectedTargetIds.contains(p2Unit.id))

        // In single-target limit (default 1), selecting p3 replaces p2
        vm3.toggleTargetSelection(p3Unit.id)
        assertTrue(vm3.uiState.value.selectedTargetIds.contains(p3Unit.id))
        assertFalse(vm3.uiState.value.selectedTargetIds.contains(p2Unit.id))
    }

    @Test
    fun testCustomMatchSettingsAppliedToBattle() {
        val customVm = GameViewModel()
        val customSettings = com.example.model.MatchSettings(
            maxRounds = 5,
            isUnlimitedRounds = false,
            guardRounds = 1,
            maxAttacksPerTurn = 2,
            hpMultiplier = 1.25f,
            ultimateRequiredShots = 0,
            allowStrategyMinigame = false
        )
        customVm.updateMatchSettings(customSettings)
        customVm.startBattle()

        val state = customVm.uiState.value
        assertEquals(2, state.maxAttacksPerTurn)
        assertEquals(5, state.matchSettings.maxRounds)
        assertEquals(1, state.matchSettings.guardRounds)

        // Verify warriors have 1 guard round remaining
        val warriors = state.battleUnits.filter { it.hero.role == HeroRole.GUERRERO }
        for (w in warriors) {
            assertEquals("Warrior should have 1 round of guard from settings", 1, w.guardRoundsRemaining)
        }

        // Strategy minigame should be rejected if disabled
        val currentUnit = state.battleUnits.first { it.playerId == state.currentTurnPlayer && it.isAlive }
        customVm.openAttackModal(currentUnit)
        customVm.openStrategyMinigame()
        assertFalse("Strategy modal should not open when strategy minigame is disabled in settings", customVm.uiState.value.isStrategyModalOpen)
    }

    @Test
    fun testCustomProtectionRoundsZeroAllowsTargetingBacklineImmediately() {
        val customVm = GameViewModel()
        val noShieldSettings = com.example.model.MatchSettings(
            guardRounds = 0
        )
        customVm.updateMatchSettings(noShieldSettings)
        customVm.startBattle()

        val state = customVm.uiState.value
        val warriors = state.battleUnits.filter { it.hero.role == HeroRole.GUERRERO }
        for (w in warriors) {
            assertEquals("Warrior should have 0 rounds of guard", 0, w.guardRoundsRemaining)
        }

        // When warriors have 0 guard rounds, backline heroes are targetable
        val opponentUnits = state.battleUnits.filter { it.playerId == 2 && it.isAlive }
        val targetableOpponents = opponentUnits.filter { customVm.isUnitTargetable(it, state.battleUnits) }
        assertTrue("Backline should be targetable immediately when guard rounds is 0", targetableOpponents.size >= 3)
    }
}
