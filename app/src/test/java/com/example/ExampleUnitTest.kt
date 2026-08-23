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
                unit.copy(currentHp = 0)
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
        val woundedAlly = units.first { it.playerId == 1 && it.hero.role == HeroRole.GUERRERO }

        viewModel.openAttackModal(mage)
        viewModel.setIsHealMode(true)
        assertTrue(viewModel.uiState.value.isHealMode)

        // Set target count to 1 and target ally
        viewModel.setTargetCountLimit(1)
        viewModel.toggleTargetSelection(woundedAlly.id)
        assertEquals(1, viewModel.uiState.value.selectedTargetIds.size)
        assertEquals(woundedAlly.id, viewModel.uiState.value.selectedTargetIds[0])
    }
}
