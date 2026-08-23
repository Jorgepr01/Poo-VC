package com.example;

import com.example.engine.BattleRules;
import com.example.engine.CombatAiLogic;
import com.example.engine.CombatCalculator;
import com.example.engine.StrategyEngine;
import com.example.model.AttackType;
import com.example.model.BattleUnit;
import com.example.model.HeroCatalog;
import com.example.model.HeroRole;
import com.example.model.StrategyRollResult;
import com.example.model.Team;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaCombatEngineTest {

    @Test
    public void testCombatDamageCalculationWithDefenseMitigation() {
        BattleUnit attacker = new BattleUnit("p1_u0", 1, 0, HeroCatalog.GUERREROS.get(0), "Guerreros");
        BattleUnit target = new BattleUnit("p2_u0", 2, 0, HeroCatalog.GUERREROS.get(1), "Guerreros");

        int basicDamage = CombatCalculator.calculateDamage(attacker, target, AttackType.BASIC, 0f, 1);
        assertTrue("Basic damage should be positive and mitigated", basicDamage >= 10);

        int ultimateDamage = CombatCalculator.calculateDamage(attacker, target, AttackType.ULTIMATE, 0f, 1);
        assertTrue("Ultimate damage should exceed basic damage", ultimateDamage > basicDamage);
    }

    @Test
    public void testMageHealingCalculation() {
        BattleUnit mage = new BattleUnit("p1_u4", 1, 4, HeroCatalog.MAGOS.get(0), "Magos");

        int singleHeal = CombatCalculator.calculateHeal(mage, 1, 0f, false);
        int multiHeal = CombatCalculator.calculateHeal(mage, 3, 0f, false);

        assertTrue("Single target heal should be higher than multi target heal per unit", singleHeal >= multiHeal);
    }

    @Test
    public void testFrontlineProtectionRule() {
        List<BattleUnit> units = new ArrayList<>();
        BattleUnit p2Warrior = new BattleUnit("p2_g0", 2, 0, HeroCatalog.GUERREROS.get(0), "Guerreros");
        BattleUnit p2Mage = new BattleUnit("p2_w0", 2, 1, HeroCatalog.MAGOS.get(0), "Magos");

        units.add(p2Warrior);
        units.add(p2Mage);

        // Warrior is alive -> Mage is protected and NOT targetable
        assertTrue("Warrior should always be targetable", BattleRules.isUnitTargetable(p2Warrior, units));
        assertFalse("Mage should be protected while Warrior is alive", BattleRules.isUnitTargetable(p2Mage, units));
        assertTrue("Mage is protected", BattleRules.isUnitProtected(p2Mage, units));

        // When Warrior is defeated
        BattleUnit deadWarrior = p2Warrior.copyWithHp(0);
        units.set(0, deadWarrior);

        assertTrue("Mage is targetable once warrior is defeated", BattleRules.isUnitTargetable(p2Mage, units));
        assertFalse("Mage is no longer protected", BattleRules.isUnitProtected(p2Mage, units));
    }

    @Test
    public void testStrategyEngineEvaluation() {
        StrategyRollResult winResult = StrategyEngine.evaluateRoll(3, 3);
        assertTrue("Matching number should win", winResult.isWin());
        assertEquals(0.15f, winResult.getBonusPct(), 0.001f);

        StrategyRollResult parityWin = StrategyEngine.evaluateRoll(2, 4);
        assertTrue("Same parity should win", parityWin.isWin());

        StrategyRollResult loseResult = StrategyEngine.evaluateRoll(1, 4);
        assertFalse("Odd vs Even should lose", loseResult.isWin());
        assertEquals(-0.05f, loseResult.getBonusPct(), 0.001f);
    }

    @Test
    public void testCombatAiLogicDecision() {
        List<BattleUnit> units = new ArrayList<>();
        units.add(new BattleUnit("p1_u0", 1, 0, HeroCatalog.GUERREROS.get(0), "Guerreros"));
        units.add(new BattleUnit("p2_u0", 2, 0, HeroCatalog.GUERREROS.get(1), "Guerreros"));
        units.add(new BattleUnit("p2_u1", 2, 1, HeroCatalog.MAGOS.get(0), "Magos"));

        CombatAiLogic.AiActionDecision decision = CombatAiLogic.decideAction(2, units, 1);
        assertNotNull("AI should decide an action", decision);
        assertNotNull("AI attacker must exist", decision.getAttacker());
        assertFalse("AI targets should not be empty", decision.getTargetIds().isEmpty());
    }

    @Test
    public void testUnitRefreshMethods() {
        List<BattleUnit> units = new ArrayList<>();
        BattleUnit p1Unit = new BattleUnit("p1_u0", 1, 0, HeroCatalog.GUERREROS.get(0), 100, 100, true, true, 0f, 0f, "Guerreros");
        BattleUnit p2Unit = new BattleUnit("p2_u0", 2, 0, HeroCatalog.GUERREROS.get(1), 100, 100, true, true, 0f, 0f, "Guerreros");
        units.add(p1Unit);
        units.add(p2Unit);

        assertTrue(units.get(0).isHasActed());
        assertTrue(units.get(1).isHasActed());

        // Refresh player 2
        List<BattleUnit> refreshedP2 = BattleRules.refreshPlayerUnits(units, 2);
        assertTrue("Player 1 unit should remain acted", refreshedP2.get(0).isHasActed());
        assertFalse("Player 2 unit should be refreshed", refreshedP2.get(1).isHasActed());

        // Refresh all for new round
        List<BattleUnit> refreshedAll = BattleRules.refreshAllUnitsForNewRound(units);
        assertFalse("Player 1 unit should be refreshed for new round", refreshedAll.get(0).isHasActed());
        assertFalse("Player 2 unit should be refreshed for new round", refreshedAll.get(1).isHasActed());
    }
}
