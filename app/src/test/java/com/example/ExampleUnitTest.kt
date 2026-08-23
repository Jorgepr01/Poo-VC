package com.example

import com.example.game.GameViewModel
import com.example.model.HeroRole
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWarriorProtectionRule() {
        val viewModel = GameViewModel()
        viewModel.setPlayerCount(2)
        viewModel.startBattle()

        val units = viewModel.uiState.value.battleUnits
        val p2Units = units.filter { it.playerId == 2 }
        val p2Warriors = p2Units.filter { it.hero.role == HeroRole.GUERRERO }
        val p2Mage = p2Units.first { it.hero.role == HeroRole.MAGO }
        val p2Mystic = p2Units.first { it.hero.role == HeroRole.MISTICO }

        // With warriors alive, mages and mystics are protected
        assertTrue(p2Warriors.all { it.isAlive })
        assertTrue(viewModel.isUnitProtected(p2Mage, units))
        assertTrue(viewModel.isUnitProtected(p2Mystic, units))
        assertFalse(viewModel.isUnitTargetable(p2Mage, units))
        assertFalse(viewModel.isUnitTargetable(p2Mystic, units))
        assertTrue(viewModel.isUnitTargetable(p2Warriors.first(), units))

        // When all warriors on team 2 are defeated
        val defeatedWarriorUnits = units.map { unit ->
            if (unit.playerId == 2 && unit.hero.role == HeroRole.GUERRERO) {
                unit.copyWithHp(0)
            } else {
                unit
            }
        }

        assertFalse(viewModel.isUnitProtected(p2Mage, defeatedWarriorUnits))
        assertFalse(viewModel.isUnitProtected(p2Mystic, defeatedWarriorUnits))
        assertTrue(viewModel.isUnitTargetable(p2Mage, defeatedWarriorUnits))
        assertTrue(viewModel.isUnitTargetable(p2Mystic, defeatedWarriorUnits))
    }

    @Test
    fun testThreeAttacksPerTurnLimit() {
        val viewModel = GameViewModel()
        viewModel.setPlayerCount(2)
        viewModel.startBattle()

        assertEquals(1, viewModel.uiState.value.currentTurnPlayer)
        assertEquals(0, viewModel.uiState.value.attacksUsedThisTurn)
        assertEquals(3, viewModel.uiState.value.attacksRemaining)

        // Passing turn resets counter and advances player
        viewModel.passTurn()
        assertEquals(2, viewModel.uiState.value.currentTurnPlayer)
        assertEquals(0, viewModel.uiState.value.attacksUsedThisTurn)
        assertEquals(3, viewModel.uiState.value.attacksRemaining)
    }

    @Test
    fun testTargetCountLimitAndSelection() {
        val viewModel = GameViewModel()
        viewModel.setPlayerCount(2)
        viewModel.startBattle()

        val units = viewModel.uiState.value.battleUnits
        val attacker = units.first { it.playerId == 1 }
        val p2Warriors = units.filter { it.playerId == 2 && it.hero.role == HeroRole.GUERRERO }

        viewModel.openAttackModal(attacker)
        assertEquals(1, viewModel.uiState.value.targetCountLimit)

        // Change target count to 2
        viewModel.setTargetCountLimit(2)
        assertEquals(2, viewModel.uiState.value.targetCountLimit)

        // Select second warrior target (first is already selected by default)
        viewModel.toggleTargetSelection(p2Warriors[1].id)
        assertEquals(2, viewModel.uiState.value.selectedTargetIds.size)
        assertTrue(viewModel.uiState.value.selectedTargetIds.contains(p2Warriors[0].id))
        assertTrue(viewModel.uiState.value.selectedTargetIds.contains(p2Warriors[1].id))
    }

    @Test
    fun testMageHealingCapability() {
        val viewModel = GameViewModel()
        viewModel.setPlayerCount(2)
        viewModel.startBattle()

        val units = viewModel.uiState.value.battleUnits
        val mage = units.first { it.playerId == 1 && it.hero.role == HeroRole.MAGO }
        val p1Warriors = units.filter { it.playerId == 1 && it.hero.role == HeroRole.GUERRERO }
        val secondAlly = p1Warriors[1]

        viewModel.openAttackModal(mage)
        viewModel.setIsHealMode(true)
        assertTrue(viewModel.uiState.value.isHealMode)

        // Set target count to 2 and add second ally
        viewModel.setTargetCountLimit(2)
        viewModel.toggleTargetSelection(secondAlly.id)
        assertEquals(2, viewModel.uiState.value.selectedTargetIds.size)
        assertTrue(viewModel.uiState.value.selectedTargetIds.contains(secondAlly.id))
    }

    @Test
    fun testAllPlayersCanUseHeroesEveryRoundWithoutCrossRoundLockout() {
        val viewModel = GameViewModel()
        viewModel.setPlayerCount(3)
        viewModel.startBattle()

        // --- ROUND 1 ---
        assertEquals(1, viewModel.uiState.value.roundNumber)
        assertEquals(1, viewModel.uiState.value.currentTurnPlayer)

        // Player 1 attacks with first unit
        val p1Units = viewModel.uiState.value.battleUnits.filter { it.playerId == 1 }
        viewModel.openAttackModal(p1Units[0])
        viewModel.executeAttack()

        // Verify that in the same turn/round, unit 0 has acted
        val p1Unit0AfterAttack = viewModel.uiState.value.battleUnits.first { it.id == p1Units[0].id }
        assertTrue(p1Unit0AfterAttack.hasActed)

        // Player 1 passes remainder of turn
        viewModel.passTurn()

        // Player 2's turn in Round 1: ALL Player 2 units must be ready (none disabled)
        assertEquals(2, viewModel.uiState.value.currentTurnPlayer)
        val p2UnitsRound1 = viewModel.uiState.value.battleUnits.filter { it.playerId == 2 }
        assertTrue("All Player 2 units must be ready to act in Round 1", p2UnitsRound1.all { !it.hasActed })

        // Player 2 uses unit 0
        viewModel.openAttackModal(p2UnitsRound1[0])
        viewModel.executeAttack()
        viewModel.passTurn()

        // Player 3's turn in Round 1: ALL Player 3 units must be ready
        assertEquals(3, viewModel.uiState.value.currentTurnPlayer)
        val p3UnitsRound1 = viewModel.uiState.value.battleUnits.filter { it.playerId == 3 }
        assertTrue("All Player 3 units must be ready to act in Round 1", p3UnitsRound1.all { !it.hasActed })

        // Player 3 uses unit 0
        viewModel.openAttackModal(p3UnitsRound1[0])
        viewModel.executeAttack()
        viewModel.passTurn()

        // --- ROUND 2 ---
        assertEquals(2, viewModel.uiState.value.roundNumber)
        assertEquals(1, viewModel.uiState.value.currentTurnPlayer)

        // Player 1 can use ANY of their living units in Round 2 including unit 0
        val p1UnitsRound2 = viewModel.uiState.value.battleUnits.filter { it.playerId == 1 }
        assertTrue("All living Player 1 units must be ready in Round 2", p1UnitsRound2.all { !it.hasActed })

        viewModel.passTurn()

        // Player 2 turn in Round 2: ALL Player 2 units must be ready (including unit 0 used in Round 1)
        assertEquals(2, viewModel.uiState.value.currentTurnPlayer)
        val p2UnitsRound2 = viewModel.uiState.value.battleUnits.filter { it.playerId == 2 }
        assertTrue("All living Player 2 units must be ready in Round 2", p2UnitsRound2.all { !it.hasActed })

        viewModel.passTurn()

        // Player 3 turn in Round 2: ALL Player 3 units must be ready
        assertEquals(3, viewModel.uiState.value.currentTurnPlayer)
        val p3UnitsRound2 = viewModel.uiState.value.battleUnits.filter { it.playerId == 3 }
        assertTrue("All living Player 3 units must be ready in Round 2", p3UnitsRound2.all { !it.hasActed })
    }
}
