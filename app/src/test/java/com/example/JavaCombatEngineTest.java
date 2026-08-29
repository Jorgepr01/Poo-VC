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

        // Round 1: Warrior has active guard (3 rounds remaining) -> Mage is protected and NOT targetable
        assertEquals(3, p2Warrior.getGuardRoundsRemaining());
        assertTrue(p2Warrior.isGuardActive());
        assertTrue("Warrior should always be targetable", BattleRules.isUnitTargetable(p2Warrior, units));
        assertFalse("Mage should be protected while Warrior guard is active", BattleRules.isUnitTargetable(p2Mage, units));
        assertTrue("Mage is protected in round 1", BattleRules.isUnitProtected(p2Mage, units));

        // Round 2: Warrior still has active guard (2 rounds remaining) -> Mage protected
        List<BattleUnit> round2Units = BattleRules.refreshAllUnitsForNewRound(units);
        assertEquals(2, round2Units.get(0).getGuardRoundsRemaining());
        assertTrue("Warrior guard active in round 2", round2Units.get(0).isGuardActive());
        assertFalse("Mage still protected in round 2", BattleRules.isUnitTargetable(round2Units.get(1), round2Units));
        assertTrue("Mage is protected in round 2", BattleRules.isUnitProtected(round2Units.get(1), round2Units));

        // Round 3: Warrior still has active guard (1 round remaining) -> Mage protected
        List<BattleUnit> round3Units = BattleRules.refreshAllUnitsForNewRound(round2Units);
        assertEquals(1, round3Units.get(0).getGuardRoundsRemaining());
        assertTrue("Warrior guard active in round 3", round3Units.get(0).isGuardActive());
        assertFalse("Mage still protected in round 3", BattleRules.isUnitTargetable(round3Units.get(1), round3Units));
        assertTrue("Mage is protected in round 3", BattleRules.isUnitProtected(round3Units.get(1), round3Units));

        // Round 4: Warrior guard expires (0 rounds remaining) -> Mage becomes targetable
        List<BattleUnit> round4Units = BattleRules.refreshAllUnitsForNewRound(round3Units);
        assertEquals(0, round4Units.get(0).getGuardRoundsRemaining());
        assertFalse("Warrior guard should expire after 3 rounds", round4Units.get(0).isGuardActive());
        assertTrue("Mage is targetable once guard expires", BattleRules.isUnitTargetable(round4Units.get(1), round4Units));
        assertFalse("Mage is no longer protected in round 4", BattleRules.isUnitProtected(round4Units.get(1), round4Units));

        // When Warrior is defeated before round 4
        BattleUnit deadWarrior = p2Warrior.copyWithHp(0);
        units.set(0, deadWarrior);

        assertTrue("Mage is targetable once warrior is defeated", BattleRules.isUnitTargetable(p2Mage, units));
        assertFalse("Mage is no longer protected", BattleRules.isUnitProtected(p2Mage, units));
    }

    @Test
    public void testUltimateChargeRequirement() {
        BattleUnit freshHero = new BattleUnit("p1_u0", 1, 0, HeroCatalog.MISTICOS.get(0), "Místicos");
        assertFalse("Fresh hero cannot use ultimate before any shots", freshHero.canUseUltimate());
        assertEquals(0, freshHero.getShotsPerformed());

        BattleUnit chargedHero = freshHero.copyWithIncrementedShots();
        assertTrue("Hero with 1 shot can use ultimate", chargedHero.canUseUltimate());
        assertEquals(1, chargedHero.getShotsPerformed());
        assertTrue(BattleRules.canExecuteUltimate(chargedHero));
    }

    @Test
    public void testStrategyEngineEvaluation() {
        // Standard 2d6 evaluation (+15% on win)
        StrategyRollResult winResult = StrategyEngine.evaluateRoll(2, 3, 3);
        assertTrue("Matching number should win", winResult.isWin());
        assertEquals(0.15f, winResult.getBonusPct(), 0.001f);
        assertFalse(winResult.isBroken());
        assertEquals(2, winResult.getDiceCount());

        StrategyRollResult parityWin = StrategyEngine.evaluateRoll(2, 2, 4);
        assertTrue("Same parity should win", parityWin.isWin());
        assertEquals(0.15f, parityWin.getBonusPct(), 0.001f);

        StrategyRollResult loseResult = StrategyEngine.evaluateRoll(2, 1, 4);
        assertFalse("Odd vs Even should lose", loseResult.isWin());
        assertEquals(-0.05f, loseResult.getBonusPct(), 0.001f);

        // Broken 1d6 evaluation (+35% on win)
        StrategyRollResult brokenWin = StrategyEngine.evaluateRoll(1, 5, 5);
        assertTrue("1d6 matching number should win", brokenWin.isWin());
        assertEquals(0.35f, brokenWin.getBonusPct(), 0.001f);
        assertTrue(brokenWin.isBroken());
        assertEquals(1, brokenWin.getDiceCount());

        StrategyRollResult brokenParityWin = StrategyEngine.evaluateRoll(1, 3, 5);
        assertTrue("1d6 parity win should award broken +35% bonus", brokenParityWin.isWin());
        assertEquals(0.35f, brokenParityWin.getBonusPct(), 0.001f);

        StrategyRollResult brokenLose = StrategyEngine.evaluateRoll(1, 2, 5);
        assertFalse("1d6 mismatch should lose", brokenLose.isWin());
        assertEquals(-0.05f, brokenLose.getBonusPct(), 0.001f);
    }

    @Test
    public void testBrokenHeroesAssignment() {
        // Verify Brutus (Guerrero) has 1d6 and broken strategy
        com.example.model.Hero brutus = HeroCatalog.findById("g_barbarian");
        assertNotNull(brutus);
        assertEquals(1, brutus.getStrategyDiceCount());
        assertTrue(brutus.isBrokenStrategy());
        assertEquals(0.35f, brutus.getStrategyBonusMultiplier(), 0.001f);

        // Verify Valerius (Guerrero) has 2d6 tactical strategy
        com.example.model.Hero valerius = HeroCatalog.findById("g_paladin");
        assertNotNull(valerius);
        assertEquals(2, valerius.getStrategyDiceCount());
        assertFalse(valerius.isBrokenStrategy());
        assertEquals(0.15f, valerius.getStrategyBonusMultiplier(), 0.001f);

        // Verify Malakor (Místico) has 1d6 broken strategy
        com.example.model.Hero malakor = HeroCatalog.findById("m_shadow");
        assertNotNull(malakor);
        assertEquals(1, malakor.getStrategyDiceCount());
        assertTrue(malakor.isBrokenStrategy());

        // Verify Ignis & Kaelith (Magos) have 1d6 broken strategy
        com.example.model.Hero ignis = HeroCatalog.findById("w_pyro");
        assertNotNull(ignis);
        assertEquals(1, ignis.getStrategyDiceCount());
        assertTrue(ignis.isBrokenStrategy());

        com.example.model.Hero storm = HeroCatalog.findById("w_storm");
        assertNotNull(storm);
        assertEquals(1, storm.getStrategyDiceCount());
        assertTrue(storm.isBrokenStrategy());
    }

    @Test
    public void testTemporaryMysticBuffsExpiration() {
        BattleUnit hero = new BattleUnit("p1_u0", 1, 0, HeroCatalog.MISTICOS.get(0), "Místicos");
        // Apply high impact buff (+25% atk, +20% def) with 3 rounds duration
        BattleUnit buffedHero = hero.copyWithBuffs(0.25f, 0.20f, 3);
        assertEquals(0.25f, buffedHero.getAttackBuffPct(), 0.001f);
        assertEquals(0.20f, buffedHero.getDefenseBuffPct(), 0.001f);
        assertEquals(3, buffedHero.getBuffRoundsRemaining());
        assertTrue(buffedHero.hasActiveBuffsOrDebuffs());

        // Round 1 refresh -> 2 rounds remaining, stats stay active
        List<BattleUnit> units = new ArrayList<>();
        units.add(buffedHero);
        List<BattleUnit> round2Units = BattleRules.refreshAllUnitsForNewRound(units);
        assertEquals(2, round2Units.get(0).getBuffRoundsRemaining());
        assertEquals(0.25f, round2Units.get(0).getAttackBuffPct(), 0.001f);

        // Round 2 refresh -> 1 round remaining, stats stay active
        List<BattleUnit> round3Units = BattleRules.refreshAllUnitsForNewRound(round2Units);
        assertEquals(1, round3Units.get(0).getBuffRoundsRemaining());
        assertEquals(0.25f, round3Units.get(0).getAttackBuffPct(), 0.001f);

        // Round 3 refresh -> 0 rounds remaining, stats expire
        List<BattleUnit> round4Units = BattleRules.refreshAllUnitsForNewRound(round3Units);
        BattleUnit expiredHero = round4Units.get(0);
        assertEquals(0f, expiredHero.getAttackBuffPct(), 0.001f);
        assertEquals(0f, expiredHero.getDefenseBuffPct(), 0.001f);
        assertEquals(0, expiredHero.getBuffRoundsRemaining());
        assertFalse(expiredHero.hasActiveBuffsOrDebuffs());
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
    public void testHeroIdentityAndMottos() {
        for (com.example.model.Hero hero : HeroCatalog.ALL_HEROES) {
            assertNotNull("Hero name should not be null", hero.getName());
            assertFalse("Hero title should not be empty", hero.getTitle().isEmpty());
            assertFalse("Hero motto should not be empty", hero.getMotto().isEmpty());
            assertFalse("Hero tactical description should not be empty", hero.getTacticalReason().isEmpty());
            assertTrue("Hero HP should be positive", hero.getHp() > 0);
            assertTrue("Hero Attack should be positive", hero.getAttack() > 0);
            assertTrue("Hero Defense should be positive", hero.getDefense() > 0);
        }
    }
}
