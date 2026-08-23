package com.example

import com.example.game.GameViewModel
import com.example.model.AttackType
import com.example.model.BattleUnit
import com.example.model.HeroCatalog
import com.example.model.HeroRole
import com.example.model.StrategyRollResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun testWarriorBasicAttackDefaultsToDamage() {
        val state = viewModel.uiState.value
        val warrior = state.battleUnits.firstOrNull { it.playerId == state.currentTurnPlayer && it.hero.role == HeroRole.GUERRERO }
        
        if (warrior != null) {
            viewModel.openAttackModal(warrior)
            val attackState = viewModel.uiState.value

            assertTrue(attackState.isAttackModalOpen)
            assertFalse("Warrior basic attack should not be heal mode", attackState.isHealMode)
            assertEquals(AttackType.BASIC, attackState.selectedAttackType)
        }
    }

    @Test
    fun testStrategyDisabledDuringUltimate() {
        val state = viewModel.uiState.value
        val unit = state.battleUnits.first { it.playerId == state.currentTurnPlayer && it.isAlive }
        
        viewModel.openAttackModal(unit)
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
}
